package com.msa.customer.core.domain.entities;

import java.util.Objects;

/**
 * DDD Entity: an object defined by its identity (not by its attributes). Two entities are equal
 * when their identities are equal. Child entities of an aggregate (e.g. a future {@code Address})
 * extend this class; aggregate roots extend {@link com.msa.customer.core.domain.aggregateroots.AggregateRoot}.
 */
public abstract class DomainEntity<ID> {

    private final ID id;

    protected DomainEntity(ID id) {
        this.id = Objects.requireNonNull(id, "id must not be null");
    }

    public ID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return id.equals(((DomainEntity<?>) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
