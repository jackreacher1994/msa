package com.msa.customer.core.domain.aggregateroots;

import com.msa.customer.core.domain.entities.DomainEntity;
import com.msa.customer.core.domain.events.DomainEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * DDD Aggregate Root: the single entry point to an aggregate and its consistency boundary.
 * State changes record domain events; the application layer pulls and publishes them after the
 * aggregate has been persisted.
 */
public abstract class AggregateRoot<ID> extends DomainEntity<ID> {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected AggregateRoot(ID id) {
        super(id);
    }

    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /** Returns the pending events and clears them, so each event is published exactly once. */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }
}
