package com.msa.customer.core.infra.adapter.outbound.integration.repositories.restclients;

import com.msa.customer.core.application.ports.outbound.dtos.CountryDTO;
import com.msa.customer.core.application.ports.outbound.repositories.referencedata.ReferenceDataOutboundPort;
import com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.technicality.ReferenceDataUnavailableException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Optional;

/**
 * Outbound REST client adapter to the Reference Data service (synchronous inter-service communication).
 * - Service discovery: the base URL is a DNS name (Docker Compose service / Kubernetes Service).
 * - Resilience: connect/read timeouts via quarkus.rest-client.reference-data.* (Timeout pattern).
 * - Security: the caller's JWT is relayed so the downstream service can authenticate the request.
 * - Tracing: W3C trace context is propagated by the Quarkus OpenTelemetry extension.
 */
@ApplicationScoped
public class ReferenceDataRestClientAdapter implements ReferenceDataOutboundPort {

    private final ReferenceDataClient referenceDataClient;
    private final Instance<JsonWebToken> jwt;

    @Inject
    public ReferenceDataRestClientAdapter(@RestClient ReferenceDataClient referenceDataClient,
                                          Instance<JsonWebToken> jwt) {
        this.referenceDataClient = referenceDataClient;
        this.jwt = jwt;
    }

    @Override
    public Optional<CountryDTO> findCountry(String countryCode) {
        try {
            String authorization = bearerTokenOrNull();
            ReferenceDataClient.CountryWire country = referenceDataClient.findCountry(authorization, countryCode);
            return Optional.ofNullable(country).map(c -> new CountryDTO(c.code(), c.name(), c.active()));
        } catch (NotFoundException e) {
            return Optional.empty();
        } catch (WebApplicationException e) {
            if (e.getResponse() != null && e.getResponse().getStatus() == 404) {
                return Optional.empty();
            }
            throw new ReferenceDataUnavailableException(e);
        } catch (Exception e) {
            throw new ReferenceDataUnavailableException(e);
        }
    }

    private String bearerTokenOrNull() {
        try {
            if (jwt == null || jwt.isUnsatisfied()) {
                return null;
            }
            JsonWebToken token = jwt.get();
            String raw = token == null ? null : token.getRawToken();
            return raw == null ? null : "Bearer " + raw;
        } catch (Exception e) {
            return null;
        }
    }
}
