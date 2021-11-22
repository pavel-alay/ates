package com.alay.analytics;

import com.alay.events.TaskCompleted;
import com.alay.events.TaskCreated;
import com.alay.events.TaskUpdated;
import com.alay.events.BalanceUpdated;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.retry.support.RetryTemplate;

import java.util.Map;

@Slf4j
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskCreated> taskCreatedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaskCreated> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumerFactory());
        setupErrorHandling(factory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskUpdated> taskUpdatedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaskUpdated> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumerFactory());
        setupErrorHandling(factory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskCompleted> taskCompletedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaskCompleted> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumerFactory());
        setupErrorHandling(factory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BalanceUpdated> balanceUpdatedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BalanceUpdated> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumerFactory());
        setupErrorHandling(factory);
        return factory;
    }

    private <V> ConsumerFactory<String, V> createConsumerFactory() {
        JsonDeserializer<V> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("com.alay.events");
        return new DefaultKafkaConsumerFactory<>(
            Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
                ConsumerConfig.GROUP_ID_CONFIG, groupId
            ),
            new StringDeserializer(), deserializer);
    }

    private void setupErrorHandling(ConcurrentKafkaListenerContainerFactory<?, ?> factory) {
        factory.setRetryTemplate(RetryTemplate.builder()
            .maxAttempts(10)
            .exponentialBackoff(1000, 2, 2*60000, true)
            .build());
        factory.setRecoveryCallback((context -> {
            log.error("Failed to process event after {} attempt: {}", context.getRetryCount(), context.getLastThrowable().getMessage());
            log.error("Failed event: {}", context.getAttribute("record"));
            log.error("Alright. We'll implement a better error handling later. Someday.");
            return null;
        }));
    }
}
