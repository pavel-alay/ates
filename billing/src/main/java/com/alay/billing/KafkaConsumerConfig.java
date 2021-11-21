package com.alay.billing;

import com.alay.events.TaskAssigned;
import com.alay.events.TaskCompleted;
import com.alay.events.TaskCreated;
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

    public static final String ADMIN_EVENTS_TOPIC = "keycloak_admin_events.stream";
    public static final String TASK_CREATED_TOPIC = "task-created.stream";
    public static final String TASK_ASSIGNED_TOPIC = "task-assigned";
    public static final String TASK_COMPLETED_TOPIC = "task-completed";

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.kafka.consumer.group-id")
    private String groupId;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskCreated> taskCreatedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaskCreated> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(taskCreatedConsumerFactory());
        setupErrorHandling(factory);
        return factory;
    }

    public ConsumerFactory<String, TaskCreated> taskCreatedConsumerFactory() {
        JsonDeserializer<TaskCreated> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("com.alay.events");
        return new DefaultKafkaConsumerFactory<>(getDefaultProperties(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskAssigned> taskAssignedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaskAssigned> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(taskAssignedConsumerFactory());
        setupErrorHandling(factory);
        return factory;
    }

    public ConsumerFactory<String, TaskAssigned> taskAssignedConsumerFactory() {
        JsonDeserializer<TaskAssigned> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("com.alay.events");
        return new DefaultKafkaConsumerFactory<>(getDefaultProperties(), new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskCompleted> taskCompletedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaskCompleted> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(taskCompletedConsumerFactory());
        setupErrorHandling(factory);
        return factory;
    }

    public ConsumerFactory<String, TaskCompleted> taskCompletedConsumerFactory() {
        JsonDeserializer<TaskCompleted> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("com.alay.events");
        return new DefaultKafkaConsumerFactory<>(getDefaultProperties(), new StringDeserializer(), deserializer);
    }

    private void setupErrorHandling(ConcurrentKafkaListenerContainerFactory<?, ?> factory) {
        factory.setRetryTemplate(RetryTemplate.builder()
                .maxAttempts(10)
                .exponentialBackoff(1000, 2, 60000, true)
                .build());
        factory.setRecoveryCallback((context -> {
            log.error("Failed to process event after {} attempt: {}", context.getRetryCount(), context.getLastThrowable().getMessage());
            log.error("Failed event: {}", context.getAttribute("record"));
            log.error("Alright. We'll implement a better error handling later. Someday.");
            return null;
        }));
    }

    private Map<String, Object> getDefaultProperties() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress,
                ConsumerConfig.GROUP_ID_CONFIG, groupId
        );
    }
}
