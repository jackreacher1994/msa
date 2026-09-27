package com.msa.customer.core.infra.adapter.outbound.integration.repositories.eventpublishers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.msa.customer.core.domain.events.DomainEvent;

/**
 * Kafka wire format for a domain event.
 *
 * <p>Envelope carries routing/observability metadata (topic partitioning uses {@code aggregateKey}
 * so all events of one aggregate stay ordered) while {@code payload} is the domain event itself
 * serialized as JSON. Consumers route on {@code eventType} (also sent as the Kafka header
 * {@code event-type}) and deserialize {@code payload} accordingly.
 */
public record CustomerEventEnvelope(String eventId, String eventType, String aggregateId,
                                    String occurredAt, JsonNode payload) {

    public static CustomerEventEnvelope of(DomainEvent event, ObjectMapper mapper) {
        return new CustomerEventEnvelope(
                event.eventId().toString(),
                event.eventType(),
                event.aggregateId().toString(),
                event.occurredAt().toString(),
                mapper.valueToTree(event));
    }
}
