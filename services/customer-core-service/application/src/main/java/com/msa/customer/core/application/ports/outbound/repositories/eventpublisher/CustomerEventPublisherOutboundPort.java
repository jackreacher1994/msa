package com.msa.customer.core.application.ports.outbound.repositories.eventpublisher;

import com.msa.customer.core.domain.events.DomainEvent;

import java.util.List;

/** Outbound port: publishes domain events to other bounded contexts (asynchronous inter-service communication). */
public interface CustomerEventPublisherOutboundPort {

    void publish(List<DomainEvent> events);
}
