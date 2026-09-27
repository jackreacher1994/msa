package com.msa.customer.referencedata.controller;

import com.msa.customer.referencedata.dto.CountryRequest;
import com.msa.customer.referencedata.dto.CountryResponse;
import com.msa.customer.referencedata.service.CountryService;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

/**
 * HTTP adapter: mapping + validation only; delegates to the service layer.
 * Authentication is enforced by SmallRye JWT (any valid Keycloak JWT passes);
 * Kong validates at the edge, the service re-validates defense-in-depth.
 */
@Path("/api/v1/countries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class CountryController {

    private final CountryService countryService;

    @Inject
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GET
    public List<CountryResponse> findAll() {
        return countryService.findAll();
    }

    @GET
    @Path("/{code}")
    public CountryResponse findByCode(@PathParam("code") String code) {
        return countryService.findByCode(code);
    }

    @POST
    public Response create(@Valid CountryRequest request) {
        CountryResponse created = countryService.create(request);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    @PUT
    @Path("/{code}")
    public CountryResponse update(@PathParam("code") String code, @Valid CountryRequest request) {
        return countryService.update(code, request);
    }

    @DELETE
    @Path("/{code}")
    public Response delete(@PathParam("code") String code) {
        countryService.delete(code);
        return Response.noContent().build();
    }
}
