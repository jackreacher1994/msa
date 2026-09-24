package com.msa.customer.core.application.ports.inbound.queryservices;

import com.msa.customer.core.application.ports.outbound.repositories.persistence.CustomerRepositoryOutboundPort;
import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;
import com.msa.customer.core.domain.exceptions.business.CustomerNotFoundException;
import com.msa.customer.core.domain.queries.FindAllCustomersQuery;
import com.msa.customer.core.domain.queries.GetCustomerByIdQuery;
import com.msa.customer.core.domain.valueobjects.CustomerId;

import java.util.List;

public class CustomerQueryInboundPortImpl implements CustomerQueryInboundPort {

    private final CustomerRepositoryOutboundPort customerRepositoryOutboundPort;

    public CustomerQueryInboundPortImpl(CustomerRepositoryOutboundPort customerRepositoryOutboundPort) {
        this.customerRepositoryOutboundPort = customerRepositoryOutboundPort;
    }

    @Override
    public CustomerDomainEntity getCustomerById(GetCustomerByIdQuery query) {
        return customerRepositoryOutboundPort.findById(new CustomerId(query.customerId()))
                .orElseThrow(() -> new CustomerNotFoundException(query.customerId()));
    }

    @Override
    public List<CustomerDomainEntity> findAllCustomers(FindAllCustomersQuery query) {
        return customerRepositoryOutboundPort.findAll(query.limit());
    }
}
