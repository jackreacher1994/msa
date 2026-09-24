package com.msa.customer.core.application.ports.inbound.queryservices;

import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;
import com.msa.customer.core.domain.queries.FindAllCustomersQuery;
import com.msa.customer.core.domain.queries.GetCustomerByIdQuery;

import java.util.List;

/** Inbound port for read-only use cases (the "Q" of CQS). */
public interface CustomerQueryInboundPort {

    CustomerDomainEntity getCustomerById(GetCustomerByIdQuery query);

    List<CustomerDomainEntity> findAllCustomers(FindAllCustomersQuery query);
}
