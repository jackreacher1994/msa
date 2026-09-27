package com.msa.customer.core.infra.adapter.outbound.integration.repositories.eventpublishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.customer.core.application.ports.outbound.repositories.eventpublisher.CustomerEventPublisherOutboundPort;
import com.msa.customer.core.domain.events.DomainEvent;
import com.msa.customer.core.infra.adapter.configs.CustomerEventProperties;
import com.msa.customer.core.infra.adapter.inbound.restful.aop.exceptions.technicality.EventPublishException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

/**
 * Kafka event backend: implements the application's event publisher port by appending each domain
 * event as JSON to the {@code customer-events} topic.
 *
 * <ul>
 *   <li>Key = aggregate id, so all events of one customer land on the same partition (ordering).</li>
 *   <li>Header {@code event-type} = simple class name, so consumers can route without parsing the body.</li>
 *   <li>Synchronous send (bounded timeout): a broker failure throws {@link EventPublishException}
 *       (mapped to HTTP 503), which rolls the use-case transaction back. A production system would
 *       instead use a transactional outbox + relay for atomic commit and at-least-once delivery.</li>
 * </ul>
 *
 * <p>The pre-existing {@link LoggingCustomerEventPublisherAdapter} stays active alongside this
 * adapter so every event is still a structured log record (shipped to Loki); Kafka is the
 * integration backend, logs remain the observability backend.
 */
@Component
@ConditionalOnProperty(name = "app.events.kafka-enabled", havingValue = "true", matchIfMissing = true)
public class KafkaCustomerEventPublisherAdapter implements CustomerEventPublisherOutboundPort {

    private static final Logger log = LoggerFactory.getLogger(KafkaCustomerEventPublisherAdapter.class);
    private static final Duration SEND_TIMEOUT = Duration.ofSeconds(10);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final CustomerEventProperties properties;

    public KafkaCustomerEventPublisherAdapter(KafkaTemplate<String, String> kafkaTemplate,
                                             ObjectMapper objectMapper,
                                             CustomerEventProperties properties) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public void publish(List<DomainEvent> events) {
        for (DomainEvent event : events) {
            publishOne(event);
        }
    }

    private void publishOne(DomainEvent event) {
        try {
            CustomerEventEnvelope envelope = CustomerEventEnvelope.of(event, objectMapper);
            String key = envelope.aggregateId();
            String value = objectMapper.writeValueAsString(envelope);
            ProducerRecord<String, String> record =
                    new ProducerRecord<>(properties.topic(), null, key, value);
            record.headers().add("event-type",
                    envelope.eventType().getBytes(StandardCharsets.UTF_8));
            kafkaTemplate.send(record).get(SEND_TIMEOUT.toMillis(),
                    java.util.concurrent.TimeUnit.MILLISECONDS);
            log.info("Domain event published to Kafka: type={} aggregateId={} eventId={} topic={}",
                    envelope.eventType(), envelope.aggregateId(), envelope.eventId(), properties.topic());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new EventPublishException(e);
        } catch (Exception e) {
            throw new EventPublishException(e);
        }
    }
}
