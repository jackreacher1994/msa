package com.msa.customer.core.domain.events;

import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;

import java.time.Instant;
import java.util.UUID;

/** Raised when a customer's profile data has changed. */
public record CustomerProfileUpdatedEvent(UUID eventId, Instant occurredAt, UUID aggregateId,
                                          String fullName, String phoneNumber, String countryCode)
        implements DomainEvent {

    public static CustomerProfileUpdatedEvent of(CustomerDomainEntity customer) {
        return new CustomerProfileUpdatedEvent(UUID.randomUUID(), Instant.now(), customer.getId().value(),
                customer.getFullName().value(), customer.getPhoneNumber().value(), customer.getCountryCode().value());
    }
}
