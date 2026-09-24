package com.msa.customer.core.application.ports.outbound.repositories.persistence;

import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;
import com.msa.customer.core.domain.valueobjects.CustomerId;
import com.msa.customer.core.domain.valueobjects.EmailAddress;

import java.util.List;
import java.util.Optional;

/**
 * Outbound (driven) port: repository for the Customer aggregate, expressed in domain terms.
 * Implemented by a persistence adapter in the techframework module (Spring Data JPA today).
 */
public interface CustomerRepositoryOutboundPort {

    CustomerDomainEntity save(CustomerDomainEntity customer);

    Optional<CustomerDomainEntity> findById(CustomerId customerId);

    Optional<CustomerDomainEntity> findByEmail(EmailAddress email);

    List<CustomerDomainEntity> findAll(int limit);
}
