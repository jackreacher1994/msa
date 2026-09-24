package com.msa.customer.core.domain.events;

import java.time.Instant;
import java.util.UUID;

/** Domain event: an immutable fact, named in the past tense, that something meaningful happened in the domain. */
public interface DomainEvent {

    UUID eventId();

    Instant occurredAt();

    UUID aggregateId();

    default String eventType() {
        return getClass().getSimpleName();
    }
}
