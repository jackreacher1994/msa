package com.msa.customer.core.domain.valueobjects;

import java.util.Objects;
import java.util.UUID;

/** Value object for the aggregate identity. UUIDs keep ids database-vendor neutral (skeleton v1.2.3). */
public record CustomerId(UUID value) {

    public CustomerId {
        Objects.requireNonNull(value, "customer id must not be null");
    }

    public static CustomerId newId() {
        return new CustomerId(UUID.randomUUID());
    }
}
