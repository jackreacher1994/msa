package com.msa.customer.core.infra.adapter.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Event backend configuration (12-factor: overridable through environment variables).
 *
 * <pre>
 * app.events.topic=customer-events
 * app.events.kafka-enabled=true
 * </pre>
 *
 * When {@code kafka-enabled} is {@code false} the Kafka adapter is not created and the
 * logging adapter alone acts as the event backend (useful for local runs without a broker).
 */
@ConfigurationProperties(prefix = "app.events")
public record CustomerEventProperties(String topic, boolean kafkaEnabled) {

    public CustomerEventProperties {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("app.events.topic must not be blank");
        }
    }
}
