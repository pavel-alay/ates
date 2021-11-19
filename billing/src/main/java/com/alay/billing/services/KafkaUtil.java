package com.alay.billing.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
class KafkaUtil {

    static <T> void sendEvent(T event, KafkaTemplate<String, T> template) {
        log.info("sending payload='{}' to topic='{}'", event, template.getDefaultTopic());
        template.sendDefault(event);
    }
}
