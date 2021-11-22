package com.alay.billing;

import com.alay.events.BalanceUpdated;
import com.alay.events.TaskCompleted;
import com.alay.events.TaskUpdated;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import static com.alay.events.Topics.TASK_COMPLETED_TOPIC;
import static com.alay.events.Topics.TASK_UPDATED_TOPIC;
import static com.alay.events.Topics.USER_BALANCE_UPDATED;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, BalanceUpdated> balanceUpdatedTemplate(ProducerFactory<String, BalanceUpdated> balanceUpdatedProducerFactory) {
        KafkaTemplate<String, BalanceUpdated> kafkaTemplate = new KafkaTemplate<>(balanceUpdatedProducerFactory);
        kafkaTemplate.setDefaultTopic(USER_BALANCE_UPDATED);
        return kafkaTemplate;
    }

    @Bean
    public KafkaTemplate<String, TaskCompleted> taskCompletedTemplate(ProducerFactory<String, TaskCompleted> taskCompletedProducerFactory) {
        KafkaTemplate<String, TaskCompleted> kafkaTemplate = new KafkaTemplate<>(taskCompletedProducerFactory);
        kafkaTemplate.setDefaultTopic(TASK_COMPLETED_TOPIC);
        return kafkaTemplate;
    }

    @Bean
    public KafkaTemplate<String, TaskUpdated> taskUpdatedTemplate(ProducerFactory<String, TaskUpdated> taskUpdatedProducerFactory) {
        KafkaTemplate<String, TaskUpdated> kafkaTemplate = new KafkaTemplate<>(taskUpdatedProducerFactory);
        kafkaTemplate.setDefaultTopic(TASK_UPDATED_TOPIC);
        return kafkaTemplate;
    }
}
