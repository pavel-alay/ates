package com.alay.billing;

import com.alay.events.TaskCostUpdated;
import com.alay.events.TaskCreated;
import com.alay.events.TransactionCreated;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

    public static final String ADMIN_EVENTS_TOPIC = "keycloak_admin_events.stream";
    public static final String TASK_CREATED_TOPIC = "task-created.stream";
    public static final String TASK_ASSIGNED_TOPIC = "task-assigned";
    public static final String TASK_COMPLETED_TOPIC = "task-completed";
    public static final String TASK_COST_UPDATED_TOPIC = "task-cost-updated.stream";
    public static final String BILLING_TRANSACTION_CREATED = "billing-transaction-created";


    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.kafka.consumer.group-id")
    private String groupId;

    @Autowired
    ProducerFactory<String, TaskCostUpdated> taskCostUpdatedProducerFactory;

    @Autowired
    ProducerFactory<String, TransactionCreated> transactionCreatedProducerFactory;

    @Bean
    public KafkaTemplate<String, TaskCostUpdated> taskCreatedTemplate() {
        KafkaTemplate<String, TaskCostUpdated> kafkaTemplate = new KafkaTemplate<>(taskCostUpdatedProducerFactory);
        kafkaTemplate.setDefaultTopic(TASK_COST_UPDATED_TOPIC);
        return kafkaTemplate;
    }

    @Bean
    public KafkaTemplate<String, TransactionCreated> transactionCreatedTemplate() {
        KafkaTemplate<String, TransactionCreated> kafkaTemplate = new KafkaTemplate<>(transactionCreatedProducerFactory);
        kafkaTemplate.setDefaultTopic(BILLING_TRANSACTION_CREATED);
        return kafkaTemplate;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskCreated> taskCreatedContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaskCreated> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(taskCreatedConsumerFactory());
        return factory;
    }

    public ConsumerFactory<String, TaskCreated> taskCreatedConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        JsonDeserializer<TaskCreated> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("com.alay.*");
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }
}
