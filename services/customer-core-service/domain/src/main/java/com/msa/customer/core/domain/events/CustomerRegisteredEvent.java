package com.msa.customer.core.domain.events;

import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;

import java.time.Instant;
import java.util.UUID;

/** Raised when a new customer has been registered. Other bounded contexts may react to it (choreography). */
public record CustomerRegisteredEvent(UUID eventId, Instant occurredAt, UUID aggregateId,
                                      String email, String fullName, String countryCode) implements DomainEvent {

    public static CustomerRegisteredEvent of(CustomerDomainEntity customer) {
        return new CustomerRegisteredEvent(UUID.randomUUID(), Instant.now(), customer.getId().value(),
                customer.getEmail().value(), customer.getFullName().value(), customer.getCountryCode().value());
    }
}
