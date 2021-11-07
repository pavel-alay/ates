package com.alay.keycloak;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class Producer {

    private final static String BOOTSTRAP_SERVER = "broker:9092";

    public static void publishEvent(String topic, String key, String value) {
        resetThreadContext();

        KafkaProducer<String, String> producer = new KafkaProducer<>(getProperties());
        ProducerRecord<String, String> eventRecord = new ProducerRecord<>(topic, key, value);
        producer.send(eventRecord);
        producer.flush();
        producer.close();
    }

    private static void resetThreadContext() {
        Thread.currentThread().setContextClassLoader(null);
    }

    public static Properties getProperties() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return properties;
    }
}
