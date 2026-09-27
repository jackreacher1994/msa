package com.msa.customer.core.infra.adapter.inbound.restful.controllers;

import com.msa.customer.core.application.ports.inbound.commandservices.CustomerCommandInboundPort;
import com.msa.customer.core.application.ports.inbound.queryservices.CustomerQueryInboundPort;
import com.msa.customer.core.domain.commands.RegisterCustomerCommand;
import com.msa.customer.core.domain.commands.UpdateCustomerProfileCommand;
import com.msa.customer.core.domain.queries.FindAllCustomersQuery;
import com.msa.customer.core.domain.queries.GetCustomerByIdQuery;
import com.msa.customer.core.infra.adapter.inbound.restful.apis.dtos.CustomerResponse;
import com.msa.customer.core.infra.adapter.inbound.restful.apis.dtos.RegisterCustomerRequest;
import com.msa.customer.core.infra.adapter.inbound.restful.apis.dtos.UpdateCustomerProfileRequest;
import com.msa.customer.core.infra.adapter.outbound.aop.observability.ObservedUseCase;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

/**
 * Inbound (driving) REST adapter. Implements the OpenAPI contract
 * (POST/GET /api/v1/customers, GET /api/v1/customers/{id}, PUT .../profile),
 * translates DTOs into domain commands/queries and delegates to inbound ports.
 */
@Path("/api/v1/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authenticated
public class CustomerApiImpl {

    private final CustomerCommandInboundPort customerCommandInboundPort;
    private final CustomerQueryInboundPort customerQueryInboundPort;

    @Inject
    public CustomerApiImpl(CustomerCommandInboundPort customerCommandInboundPort,
                           CustomerQueryInboundPort customerQueryInboundPort) {
        this.customerCommandInboundPort = customerCommandInboundPort;
        this.customerQueryInboundPort = customerQueryInboundPort;
    }

    @POST
    @Transactional
    @ObservedUseCase
    public Response registerCustomer(@Valid RegisterCustomerRequest request) {
        var command = new RegisterCustomerCommand(request.fullName(), request.email(),
                request.phoneNumber(), request.countryCode());
        var customer = customerCommandInboundPort.registerCustomer(command);
        return Response.status(Response.Status.CREATED).entity(CustomerRestMapper.toResponse(customer)).build();
    }

    @PUT
    @Path("/{customerId}/profile")
    @Transactional
    @ObservedUseCase
    public CustomerResponse updateCustomerProfile(@PathParam("customerId") UUID customerId,
                                                  @Valid UpdateCustomerProfileRequest request) {
        var command = new UpdateCustomerProfileCommand(customerId, request.fullName(),
                request.phoneNumber(), request.countryCode());
        return CustomerRestMapper.toResponse(customerCommandInboundPort.updateCustomerProfile(command));
    }

    @GET
    @Path("/{customerId}")
    @ObservedUseCase
    public CustomerResponse getCustomerById(@PathParam("customerId") UUID customerId) {
        var customer = customerQueryInboundPort.getCustomerById(new GetCustomerByIdQuery(customerId));
        return CustomerRestMapper.toResponse(customer);
    }

    @GET
    @ObservedUseCase
    public List<CustomerResponse> getCustomers(@QueryParam("limit") @DefaultValue("20") Integer limit) {
        var customers = customerQueryInboundPort.findAllCustomers(new FindAllCustomersQuery(limit));
        return customers.stream().map(CustomerRestMapper::toResponse).toList();
    }
}
