# Customer MSA Sample

An educational microservices system for a **Customer** domain, built with **Java 21, Spring Boot 3.5 and Maven**.
It follows the package structure of `Java_Spring_Based_Microservice_Code_Skeleton_v1.2.6.pdf` and the MSA/DDD
concepts of the Day 1 / Day 2 slide decks. It deliberately implements only two business flows:

1. **Register a customer**
2. **Update a customer profile**

Clarity is favoured over completeness.

## Architecture

```
                    +-----------+   OIDC (password / auth-code)
   client --------> | Keycloak  |  realm "customer", client "customer-app", user alice
      |             +-----------+
      | Bearer JWT                          database-per-service
      v                                     +------------------+
 +---------+  /api/v1/customers   +-------->| customer-core-db |
 |  Kong   |----------------------+         +------------------+
 | gateway |          customer-core-service (DDD / hexagonal, core subdomain)
 | (JWT)   |                      | REST (relays JWT)
 +---------+  /api/v1/countries   v         +--------------------+
      |---------------------> reference-data-service ---->| reference-data-db  |
      |                       (CRUD / MVC, supporting)    +--------------------+
      |
      |  OTLP (traces, metrics, logs) from Kong + both services
      v
 +----------------+  traces (OTLP)  +--------+
 | OTel Collector |---------------->| Jaeger |
 |                |  metrics :8889  +--------+       +---------+
 |                |<----------------- Prometheus --->| Grafana |
 +----------------+  logs -> debug exporter          +---------+
```

| Bounded context | Service | Subdomain | Style |
|---|---|---|---|
| Customer | `customer-core-service` | Core | DDD, hexagonal (ports & adapters), multi-module |
| Reference data (countries) | `reference-data-service` | Supporting | CRUD, layered MVC, single module |

MSA patterns shown: API Gateway, edge authentication with an external IdP (OIDC/JWT), database per service,
synchronous REST with timeouts and token relay, correlation id, distributed tracing, externalized (12-factor) config,
health probes, and domain events.

## Repository layout

```
services/
  customer-core-service/        DDD archetype (skeleton "Variant 1")
    domain/                     Maven module: pure Java, no framework dependencies
    application/                Maven module: inbound/outbound ports + use cases (depends on domain only)
    techframework/              Maven module: Spring Boot app, adapters, config (depends on application)
  reference-data-service/       CRUD archetype (skeleton "Variant 2")
infra/                          Docker Compose configs: kong, keycloak, otel-collector, prometheus, grafana, jaeger
helm/                           One Helm chart per component/service
scripts/smoke-test.sh           End-to-end test through Kong
docker-compose.yml
```

## Customer Core Service (DDD / hexagonal)

Base package `com.msa.customer.core`:

```
domain/            com.msa.customer.core.domain
  aggregateroots/  AggregateRoot, CustomerDomainEntity (aggregate root; holds pending domain events)
  entities/        DomainEntity (identity-based equality)
  valueobjects/    CustomerId, EmailAddress, FullName, PhoneNumber, CountryCode, CustomerStatus
  commands/        RegisterCustomerCommand, UpdateCustomerProfileCommand
  queries/         GetCustomerByIdQuery, FindAllCustomersQuery
  events/          DomainEvent, CustomerRegisteredEvent, CustomerProfileUpdatedEvent
  services/        CustomerDomainService (rule spanning aggregates: unique email)
  exceptions/business/  BusinessException + specific business exceptions
  constants/, utils/

application/       com.msa.customer.core.application.ports
  inbound/commandservices/  CustomerCommandInboundPort (+Impl)   -- use cases that change state
  inbound/queryservices/    CustomerQueryInboundPort (+Impl)     -- read-only use cases (CQS)
  outbound/repositories/persistence/     CustomerRepositoryOutboundPort
  outbound/repositories/eventpublisher/  CustomerEventPublisherOutboundPort
  outbound/repositories/referencedata/   ReferenceDataOutboundPort
  outbound/dtos/                         CountryDTO

techframework/     com.msa.customer.core.infra.adapter
  inbound/restful/apis/        (generated from openapi/customer-core-api.yaml: CustomersApi + DTOs)
  inbound/restful/controllers/ CustomerApiImpl, CustomerRestMapper
  inbound/restful/filters/     CorrelationIdFilter
  inbound/restful/aop/security/    SecurityConfig (JWT resource server)
  inbound/restful/aop/exceptions/  GlobalExceptionHandler, business/, technicality/, description/
  outbound/aop/observability/      UseCaseObservabilityAspect (OpenTelemetry API spans + metric)
  outbound/integration/repositories/springdatajpa/  JPA adapter, Spring Data repo, entities/
  outbound/integration/repositories/eventpublishers/ LoggingCustomerEventPublisherAdapter
  outbound/integration/repositories/restclients/    ReferenceDataRestClientAdapter
  configs/                     BeanConfig (composition root), TransactionConfig, JpaConfig, RestClientConfig
```

### Why each DDD layer is a separate Maven module

* **The compiler enforces the dependency rule** `techframework -> application -> domain`. The domain cannot
  accidentally import Spring, JPA or HTTP classes because they are simply not on its classpath.
* **Replaceability**: a layer can be swapped without touching the others, e.g. a new `techframework`
  (Quarkus, Kafka, MongoDB, gRPC) can reuse the same `domain` and `application` jars.
* **Testability**: `domain` and `application` are tested with plain JUnit and in-memory fakes of the outbound ports.
* The application module has no `@Transactional`/`@Service`: use-case beans are wired in `BeanConfig`, and the
  transaction boundary (one ACID transaction per use case) is applied from the outside in `TransactionConfig`.

### The two flows

`POST /api/v1/customers` — register:
`CustomerApiImpl` -> `CustomerCommandInboundPort.registerCustomer` -> email uniqueness (`CustomerDomainService`) ->
country check via `ReferenceDataOutboundPort` (calls the supporting service) -> `CustomerDomainEntity.register`
(raises `CustomerRegisteredEvent`) -> `CustomerRepositoryOutboundPort.save` -> `CustomerEventPublisherOutboundPort.publish`.

`PUT /api/v1/customers/{id}/profile` — update profile: load aggregate -> country check ->
`CustomerDomainEntity.updateProfile` (only `ACTIVE` customers; email is immutable; raises
`CustomerProfileUpdatedEvent`) -> save -> publish.

Also available: `GET /api/v1/customers/{id}` and `GET /api/v1/customers?limit=20`.
Business errors map to HTTP: not found 404, duplicate email / not active 409, unsupported country 422,
invalid data 400, reference data unavailable 503.

Domain events are published by a logging adapter to keep the sample small; a Kafka adapter (ideally fed by a
transactional outbox) would implement the same outbound port.

## Reference Data Service (CRUD / MVC)

`com.msa.customer.referencedata`: `controller` -> `service` -> `repository` with a JPA `entity` and `dto`s.
CRUD for countries at `/api/v1/countries` (`GET`, `GET /{code}`, `POST`, `PUT /{code}`, `DELETE /{code}`).
Seeded with VN, SG, JP, US, DE (active) and AQ (inactive).

## Security: Kong + Keycloak (OIDC)

* Keycloak imports `infra/keycloak/customer-realm.json`: realm **`customer`**, public client **`customer-app`**
  (auth-code + PKCE and, for demos, direct access grants), test user **`alice` / `alice123`**.
* **Kong** (`infra/kong/kong.yml`, DB-less) enforces authentication on every route with the `jwt` plugin: the
  token's `iss` must be `http://localhost:8080/realms/customer` and its RS256 signature must match the realm key.
  Kong also adds `X-Correlation-ID`, rate limiting, Prometheus metrics and OpenTelemetry tracing.
* The services are OAuth2 resource servers that re-verify the same JWT against Keycloak's JWKS (defense in depth
  for traffic that bypasses the gateway). Customer Core relays the caller's token to Reference Data.

> The realm ships with a **fixed, development-only RSA key** so that Kong's public key is known in advance.
> Never reuse it outside local development: generate a new key, put it in Keycloak, and set `jwt.rsaPublicKey`
> in the Kong chart.

## Observability (OpenTelemetry-first)

* Both services run with the **OpenTelemetry Java agent** (downloaded by Maven into the image), configured only
  through standard `OTEL_*` environment variables. It auto-instruments HTTP server/client, JDBC, JVM and logs
  (and injects `trace_id`/`span_id` into the log MDC).
* Application code uses only the vendor-neutral **OpenTelemetry API** (`UseCaseObservabilityAspect`): a span per
  use case and the business counter `customer.use_case.invocations{use_case,outcome}`.
* Everything is sent via **OTLP to the OpenTelemetry Collector** (`infra/otel-collector/otel-collector-config.yaml`),
  which also scrapes Kong's Prometheus endpoint and then exports:
  * **metrics -> Prometheus exporter (:8889)**, scraped by Prometheus, shown in Grafana
    (datasources + "Customer MSA - Overview" dashboard are provisioned);
  * **traces -> Jaeger** over OTLP (Kong -> customer-core -> reference-data in one trace);
  * **logs -> `debug` exporter** (Collector stdout). Add a log backend exporter here without changing services.

## Run locally with Docker Compose

Prerequisites: Docker with Compose v2. Nothing else — the images are built with Maven inside Docker.

```bash
docker compose up -d --build
# If Maven Central rate-limits you, use a mirror:
# MAVEN_MIRROR_URL=https://maven-central.storage-download.googleapis.com/maven2 docker compose up -d --build
docker compose ps          # wait until the services are healthy (~1 min)
./scripts/smoke-test.sh    # token + both flows through Kong
```

| URL | What |
|---|---|
| http://localhost:8000 | Kong proxy (the only entry point to the services) |
| http://localhost:8080 | Keycloak (admin console: `admin` / `admin`) |
| http://localhost:16686 | Jaeger UI (traces) |
| http://localhost:9090 | Prometheus |
| http://localhost:3000 | Grafana (`admin` / `admin`; anonymous view enabled) -> dashboard "Customer MSA - Overview" |

### Authenticate through Keycloak

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/realms/customer/protocol/openid-connect/token \
  -d grant_type=password -d client_id=customer-app -d username=alice -d password=alice123 | jq -r .access_token)
```

(The password grant is used only to keep the demo scriptable; browser apps should use authorization code + PKCE.)

### Call the APIs through Kong

```bash
# Supporting service
curl -H "Authorization: Bearer $TOKEN" http://localhost:8000/api/v1/countries

# Register a customer
curl -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -X POST http://localhost:8000/api/v1/customers \
  -d '{"fullName":"Alice Nguyen","email":"alice@example.com","phoneNumber":"+84901234567","countryCode":"VN"}'

# Update the profile (use the id returned above)
curl -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -X PUT http://localhost:8000/api/v1/customers/<id>/profile \
  -d '{"fullName":"Alice Tran","phoneNumber":"+6591234567","countryCode":"SG"}'

# Without a token Kong answers 401
curl -i http://localhost:8000/api/v1/customers
```

### Where to look

* **Traces**: Jaeger -> service `kong` or `customer-core-service` -> a `PUT /api/v1/customers/{customerId}/profile`
  trace shows Kong plugins, the `UseCase updateCustomerProfile` span, JDBC calls and the downstream call to
  `reference-data-service`.
* **Metrics** (Prometheus / Grafana): `http_server_request_duration_seconds_*`, `http_client_request_duration_seconds_*`,
  `jvm_memory_used_bytes`, `customer_use_case_invocations_total`, `kong_http_requests_total`.
* **Logs**: `docker compose logs customer-core-service` (log lines carry `trace_id`, `span_id` and correlation id);
  `docker compose logs otel-collector` shows the OTLP log records.

## Build and test without Docker

```bash
mvn -B verify   # Java 21 + Maven 3.9; builds both services and runs unit/web-slice tests
```

## Kubernetes (Helm)

One chart per component in `helm/`: `customer-core-service`, `reference-data-service`, `kong`, `keycloak`,
`otel-collector`, `prometheus`, `grafana`, `jaeger`. Service names are fixed (`fullnameOverride`) so charts find each
other by DNS; everything is configurable in each chart's `values.yaml` (images, replicas, resources, URLs,
credentials, rate limit, JWT issuer/key, database, etc.).

```bash
kubectl create namespace customer-msa
for c in otel-collector jaeger prometheus grafana keycloak reference-data-service customer-core-service kong; do
  helm upgrade --install $c ./helm/$c -n customer-msa
done
kubectl -n customer-msa port-forward svc/keycloak 8080:8080 &
kubectl -n customer-msa port-forward svc/kong 8000:80 &
```

Notes:
* Push the service images to a registry and set `image.repository`/`image.tag`.
* The service charts include an optional single-node PostgreSQL (`postgresql.enabled`); point `database.url` at a
  managed database for production and use `database.existingSecret`.
* Keycloak's public URL (`keycloak.hostname`) defines the token issuer; keep `kong.jwt.issuer` in sync.
* `helm/keycloak/files/customer-realm.json` and `helm/grafana/files/*.json` are copies of the files in `infra/`.

## Educational simplifications

Password grant for demos, in-memory Jaeger storage, logging event publisher instead of a broker/outbox, no
retries/circuit breaker beyond timeouts, dev-only Keycloak key, and default credentials everywhere.
