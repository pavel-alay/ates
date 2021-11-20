package com.alay.billing;

import com.alay.events.TransactionCreated;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    public static final String BILLING_TRANSACTION_CREATED = "billing-transaction-created";

    @Bean
    public KafkaTemplate<String, TransactionCreated> transactionCreatedTemplate(
            ProducerFactory<String, TransactionCreated> transactionCreatedProducerFactory) {
        KafkaTemplate<String, TransactionCreated> kafkaTemplate = new KafkaTemplate<>(transactionCreatedProducerFactory);
        kafkaTemplate.setDefaultTopic(BILLING_TRANSACTION_CREATED);
        return kafkaTemplate;
    }
}
