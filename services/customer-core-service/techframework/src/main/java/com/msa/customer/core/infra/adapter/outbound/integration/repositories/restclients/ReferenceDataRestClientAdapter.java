package com.msa.customer.core.infra.adapter.outbound.integration.repositories.restclients;

import com.msa.customer.core.application.ports.outbound.dtos.CountryDTO;
import com.msa.customer.core.application.ports.outbound.repositories.referencedata.ReferenceDataOutboundPort;
import com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.technicality.ReferenceDataUnavailableException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

/**
 * Outbound REST client adapter to the Reference Data service (synchronous inter-service communication).
 * - Service discovery: the base URL is a DNS name (Docker Compose service / Kubernetes Service).
 * - Resilience: connect/read timeouts are configured on the client (see RestClientConfig).
 * - Security: the caller's JWT is relayed so the downstream service can authenticate the request.
 * - Tracing: W3C trace context headers are added automatically by the OpenTelemetry agent.
 */
@Component
public class ReferenceDataRestClientAdapter implements ReferenceDataOutboundPort {

    private final RestClient referenceDataRestClient;

    public ReferenceDataRestClientAdapter(RestClient referenceDataRestClient) {
        this.referenceDataRestClient = referenceDataRestClient;
    }

    @Override
    public Optional<CountryDTO> findCountry(String countryCode) {
        try {
            CountryResponse country = referenceDataRestClient.get()
                    .uri("/api/v1/countries/{code}", countryCode)
                    .headers(this::relayBearerToken)
                    .retrieve()
                    .body(CountryResponse.class);
            return Optional.ofNullable(country).map(c -> new CountryDTO(c.code(), c.name(), c.active()));
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw new ReferenceDataUnavailableException(e);
        }
    }

    private void relayBearerToken(HttpHeaders headers) {
        if (SecurityContextHolder.getContext().getAuthentication() instanceof JwtAuthenticationToken jwt) {
            headers.setBearerAuth(jwt.getToken().getTokenValue());
        }
    }

    /** Wire format of the supplier's API; translated to {@link CountryDTO} at this boundary. */
    record CountryResponse(String code, String name, String dialCode, boolean active) {
    }
}
