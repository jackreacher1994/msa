package com.msa.customer.core.infra.adapter.outbound.integration.repositories.eventpublishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.msa.customer.core.domain.commands.RegisterCustomerCommand;
import com.msa.customer.core.domain.aggregateroots.CustomerDomainEntity;
import com.msa.customer.core.domain.events.DomainEvent;
import com.msa.customer.core.infra.adapter.configs.CustomerEventProperties;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaCustomerEventPublisherAdapterTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private KafkaCustomerEventPublisherAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new KafkaCustomerEventPublisherAdapter(kafkaTemplate, objectMapper,
                new CustomerEventProperties("customer-events", true));
    }

    @Test
    @SuppressWarnings("unchecked")
    void publishesEachEventToConfiguredTopicWithAggregateKeyAndTypeHeader() throws Exception {
        CustomerDomainEntity customer = CustomerDomainEntity.register(
                new RegisterCustomerCommand("Alice Nguyen", "alice@example.com", "+84901234567", "VN"));
        List<DomainEvent> events = customer.pullDomainEvents();
        given(kafkaTemplate.send(any(ProducerRecord.class)))
                .willReturn(CompletableFuture.<SendResult<String, String>>completedFuture(null));

        adapter.publish(events);

        ArgumentCaptor<ProducerRecord<String, String>> records = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(records.capture());
        ProducerRecord<String, String> record = records.getValue();
        assertThat(record.topic()).isEqualTo("customer-events");
        assertThat(record.key()).isEqualTo(customer.getId().value().toString());
        assertThat(new String(record.headers().lastHeader("event-type").value()))
                .isEqualTo(events.get(0).eventType());
        assertThat(record.value()).contains(events.get(0).eventType());
    }
}
