package com.msa.customer.core.infra.adapter.outbound.integration.repositories.restclients;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * MicroProfile REST client for the Reference Data service.
 * Base URL and timeouts come from quarkus.rest-client.reference-data.* properties.
 */
@RegisterRestClient(configKey = "reference-data")
@Path("/api/v1/countries")
public interface ReferenceDataClient {

    @GET
    @Path("/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    CountryWire findCountry(@HeaderParam("Authorization") String authorization,
                            @PathParam("code") String code);

    /** Wire format of the supplier's API; translated to CountryDTO at the adapter boundary. */
    @RegisterForReflection
    record CountryWire(String code, String name, String dialCode, boolean active) {
    }
}
