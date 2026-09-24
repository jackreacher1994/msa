package com.msa.customer.core.infra.adapter.outbound.integration.repositories.eventpublishers;

import com.msa.customer.core.application.ports.outbound.repositories.eventpublisher.CustomerEventPublisherOutboundPort;
import com.msa.customer.core.domain.events.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Outbound event publisher adapter. To keep the sample small it publishes domain events as structured
 * log records (which the OpenTelemetry agent ships to the Collector). A production system would add an
 * {@code eventpublishers.kafka} adapter implementing the same port, ideally fed by a transactional outbox
 * so that the state change and the event are committed atomically.
 */
@Component
public class LoggingCustomerEventPublisherAdapter implements CustomerEventPublisherOutboundPort {

    private static final Logger log = LoggerFactory.getLogger(LoggingCustomerEventPublisherAdapter.class);

    @Override
    public void publish(List<DomainEvent> events) {
        events.forEach(event -> log.info("Domain event published: type={} aggregateId={} eventId={} payload={}",
                event.eventType(), event.aggregateId(), event.eventId(), event));
    }
}
