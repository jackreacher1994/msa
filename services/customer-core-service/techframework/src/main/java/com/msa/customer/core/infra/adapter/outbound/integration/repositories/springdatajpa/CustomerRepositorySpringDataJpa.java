package com.msa.customer.core.infra.adapter.outbound.integration.repositories.springdatajpa;

import com.msa.customer.core.infra.adapter.outbound.integration.repositories.springdatajpa.entities.CustomerJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/** Spring Data JPA repository; Spring generates the implementation at runtime. */
public interface CustomerRepositorySpringDataJpa extends JpaRepository<CustomerJpaEntity, UUID> {

    Optional<CustomerJpaEntity> findByEmail(String email);
}
