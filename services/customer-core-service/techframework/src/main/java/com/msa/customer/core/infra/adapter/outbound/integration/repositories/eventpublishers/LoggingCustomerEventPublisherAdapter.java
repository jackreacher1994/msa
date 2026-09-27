package com.msa.customer.core.infra.adapter.outbound.integration.repositories.eventpublishers;

import com.msa.customer.core.application.ports.outbound.repositories.eventpublisher.CustomerEventPublisherOutboundPort;
import com.msa.customer.core.domain.events.DomainEvent;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * Outbound event publisher adapter. To keep the sample small it publishes domain events as structured
 * log records (shipped to the Collector by the Quarkus OpenTelemetry extension). A production system
 * would add an {@code eventpublishers.kafka} adapter implementing the same port, ideally fed by a
 * transactional outbox so that the state change and the event are committed atomically.
 */
@ApplicationScoped
public class LoggingCustomerEventPublisherAdapter implements CustomerEventPublisherOutboundPort {

    private static final Logger LOG = Logger.getLogger(LoggingCustomerEventPublisherAdapter.class);

    @Override
    public void publish(List<DomainEvent> events) {
        events.forEach(event -> LOG.infof("Domain event published: type=%s aggregateId=%s eventId=%s payload=%s",
                event.eventType(), event.aggregateId(), event.eventId(), event));
    }
}
