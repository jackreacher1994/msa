package com.msa.customer.core.infra.adapter.outbound.integration.repositories.springdatajpa;

import com.msa.customer.core.infra.adapter.outbound.integration.repositories.springdatajpa.entities.CustomerJpaEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Quarkus persistence repository (replaces Spring Data JPA).
 * Thin wrapper over the JPA EntityManager; query methods mirror the old derived queries.
 */
@ApplicationScoped
public class CustomerRepositorySpringDataJpa {

    @Inject
    EntityManager entityManager;

    public CustomerJpaEntity saveAndFlush(CustomerJpaEntity entity) {
        if (entityManager.find(CustomerJpaEntity.class, entity.getId()) == null) {
            entityManager.persist(entity);
        } else {
            entity = entityManager.merge(entity);
        }
        entityManager.flush();
        return entity;
    }

    public Optional<CustomerJpaEntity> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(CustomerJpaEntity.class, id));
    }

    public Optional<CustomerJpaEntity> findByEmail(String email) {
        TypedQuery<CustomerJpaEntity> query =
                entityManager.createQuery("from CustomerJpaEntity c where c.email = :email", CustomerJpaEntity.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst();
    }

    public List<CustomerJpaEntity> findAllOrderedByRegisteredAtDesc(int limit) {
        TypedQuery<CustomerJpaEntity> query = entityManager.createQuery(
                "from CustomerJpaEntity c order by c.registeredAt desc", CustomerJpaEntity.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}
