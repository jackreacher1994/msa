package com.msa.customer.core.infra.adapter.inbound.restful.controllers;

import com.msa.customer.core.application.ports.inbound.commandservices.CustomerCommandInboundPort;
import com.msa.customer.core.application.ports.inbound.queryservices.CustomerQueryInboundPort;
import com.msa.customer.core.domain.commands.RegisterCustomerCommand;
import com.msa.customer.core.domain.commands.UpdateCustomerProfileCommand;
import com.msa.customer.core.domain.queries.FindAllCustomersQuery;
import com.msa.customer.core.domain.queries.GetCustomerByIdQuery;
import com.msa.customer.core.infra.adapter.inbound.restful.apis.CustomersApi;
import com.msa.customer.core.infra.adapter.inbound.restful.apis.dtos.CustomerResponseDTO;
import com.msa.customer.core.infra.adapter.inbound.restful.apis.dtos.RegisterCustomerRequestDTO;
import com.msa.customer.core.infra.adapter.inbound.restful.apis.dtos.UpdateCustomerProfileRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Inbound (driving) REST adapter. Implements the interface generated from the OpenAPI contract
 * (API-first), translates DTOs into domain commands/queries and delegates to the inbound ports.
 * It never touches repositories or the domain's internals directly.
 */
@RestController
public class CustomerApiImpl implements CustomersApi {

    private final CustomerCommandInboundPort customerCommandInboundPort;
    private final CustomerQueryInboundPort customerQueryInboundPort;

    public CustomerApiImpl(CustomerCommandInboundPort customerCommandInboundPort,
                           CustomerQueryInboundPort customerQueryInboundPort) {
        this.customerCommandInboundPort = customerCommandInboundPort;
        this.customerQueryInboundPort = customerQueryInboundPort;
    }

    @Override
    public ResponseEntity<CustomerResponseDTO> registerCustomer(RegisterCustomerRequestDTO request) {
        var command = new RegisterCustomerCommand(request.getFullName(), request.getEmail(),
                request.getPhoneNumber(), request.getCountryCode());
        var customer = customerCommandInboundPort.registerCustomer(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(CustomerRestMapper.toResponse(customer));
    }

    @Override
    public ResponseEntity<CustomerResponseDTO> updateCustomerProfile(UUID customerId,
                                                                     UpdateCustomerProfileRequestDTO request) {
        var command = new UpdateCustomerProfileCommand(customerId, request.getFullName(),
                request.getPhoneNumber(), request.getCountryCode());
        var customer = customerCommandInboundPort.updateCustomerProfile(command);
        return ResponseEntity.ok(CustomerRestMapper.toResponse(customer));
    }

    @Override
    public ResponseEntity<CustomerResponseDTO> getCustomerById(UUID customerId) {
        var customer = customerQueryInboundPort.getCustomerById(new GetCustomerByIdQuery(customerId));
        return ResponseEntity.ok(CustomerRestMapper.toResponse(customer));
    }

    @Override
    public ResponseEntity<List<CustomerResponseDTO>> getCustomers(Integer limit) {
        var customers = customerQueryInboundPort.findAllCustomers(new FindAllCustomersQuery(limit == null ? 20 : limit));
        return ResponseEntity.ok(customers.stream().map(CustomerRestMapper::toResponse).toList());
    }
}
