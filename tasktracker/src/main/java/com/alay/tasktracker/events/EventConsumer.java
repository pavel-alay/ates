package com.alay.tasktracker.events;

import com.alay.tasktracker.services.UserService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    private final UserService userService;

    public EventConsumer(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(topics = "keycloak_admin_events.stream", groupId = "task_tracker")
    public void receive(ConsumerRecord<String, String> consumerRecord) {
        // TODO add typed message
        userService.processUserEvent(consumerRecord);
    }
}
