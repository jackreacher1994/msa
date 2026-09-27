package com.msa.customer.core.infra.adapter.outbound.integration.repositories.eventpublishers;

import com.msa.customer.core.application.ports.outbound.repositories.eventpublisher.CustomerEventPublisherOutboundPort;
import com.msa.customer.core.domain.events.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Observability side of event publishing: every domain event is also a structured log record
 * (which the OpenTelemetry agent ships to the Collector/Loki). The integration backend is the
 * Kafka adapter ({@link KafkaCustomerEventPublisherAdapter}) implementing the same port;
 * both adapters are active by default. Set {@code app.events.kafka-enabled=false} to run
 * without a broker (logging only).
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
