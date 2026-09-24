package com.msa.customer.core.infra.adapter.outbound.integration.repositories.springdatajpa;

import com.msa.customer.core.application.ports.outbound.repositories.persistence.CustomerRepositoryOutboundPort;
import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;
import com.msa.customer.core.domain.valueobjects.CountryCode;
import com.msa.customer.core.domain.valueobjects.CustomerId;
import com.msa.customer.core.domain.valueobjects.CustomerStatus;
import com.msa.customer.core.domain.valueobjects.EmailAddress;
import com.msa.customer.core.domain.valueobjects.FullName;
import com.msa.customer.core.domain.valueobjects.PhoneNumber;
import com.msa.customer.core.infra.adapter.outbound.integration.repositories.springdatajpa.entities.CustomerJpaEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Outbound (driven) persistence adapter: implements the application's repository port with Spring Data JPA
 * and maps between the domain aggregate and the JPA entity. Replacing JPA (e.g. with JDBC or MongoDB)
 * means writing another adapter; the domain and application modules stay untouched.
 */
@Component
public class CustomerRepositorySpringDataJpaAdapter implements CustomerRepositoryOutboundPort {

    private final CustomerRepositorySpringDataJpa customerRepositorySpringDataJpa;

    public CustomerRepositorySpringDataJpaAdapter(CustomerRepositorySpringDataJpa customerRepositorySpringDataJpa) {
        this.customerRepositorySpringDataJpa = customerRepositorySpringDataJpa;
    }

    @Override
    public CustomerDomainEntity save(CustomerDomainEntity customer) {
        return toDomain(customerRepositorySpringDataJpa.saveAndFlush(toJpa(customer)));
    }

    @Override
    public Optional<CustomerDomainEntity> findById(CustomerId customerId) {
        return customerRepositorySpringDataJpa.findById(customerId.value()).map(this::toDomain);
    }

    @Override
    public Optional<CustomerDomainEntity> findByEmail(EmailAddress email) {
        return customerRepositorySpringDataJpa.findByEmail(email.value()).map(this::toDomain);
    }

    @Override
    public List<CustomerDomainEntity> findAll(int limit) {
        var page = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "registeredAt"));
        return customerRepositorySpringDataJpa.findAll(page).map(this::toDomain).getContent();
    }

    private CustomerJpaEntity toJpa(CustomerDomainEntity c) {
        return new CustomerJpaEntity(c.getId().value(), c.getEmail().value(), c.getFullName().value(),
                c.getPhoneNumber().value(), c.getCountryCode().value(), c.getStatus().name(),
                c.getRegisteredAt(), c.getUpdatedAt(), c.getVersion());
    }

    private CustomerDomainEntity toDomain(CustomerJpaEntity e) {
        return CustomerDomainEntity.rehydrate(new CustomerId(e.getId()), new EmailAddress(e.getEmail()),
                new FullName(e.getFullName()), new PhoneNumber(e.getPhoneNumber()),
                new CountryCode(e.getCountryCode()), CustomerStatus.valueOf(e.getStatus()),
                e.getRegisteredAt(), e.getUpdatedAt(), e.getVersion());
    }
}
