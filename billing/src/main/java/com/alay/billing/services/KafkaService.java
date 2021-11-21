package com.alay.billing.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class KafkaService {

    @Retryable(maxAttempts = 4, backoff = @Backoff(random = true, delay = 1000, maxDelay = 5000, multiplier = 2))
    public <T> void sendEvent(T event, KafkaTemplate<String, T> template) {
        log.info("sending payload='{}' to topic='{}'", event, template.getDefaultTopic());
        template.sendDefault(event);
    }

    @Recover
    public <T> void recover(Exception e, T event, KafkaTemplate<String, T> template) {
        log.error("Failed to send event: {}", e.getMessage());
        log.error("Alright. We'll implement outbox pattern later. Someday. {} {}", event, template.getDefaultTopic());
    }
}
