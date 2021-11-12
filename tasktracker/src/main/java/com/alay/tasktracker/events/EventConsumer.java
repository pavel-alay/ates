package com.alay.tasktracker.events;

import com.alay.tasktracker.entities.User;
import com.alay.tasktracker.repositories.UserRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final UserRepository userRepository;

    public EventConsumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class CreateUser {
        String resourcePath;
        String representation;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class CreateUserRepresentation {
        String username;
    }

    @KafkaListener(topics = "streaming_keycloak_admin_events", groupId = "task_tracker")
    public void receive(ConsumerRecord<String, String> consumerRecord) {
        if ("USER/CREATE".equals(consumerRecord.key())) {
            try {
                CreateUser createUser = mapper.readValue(consumerRecord.value(), CreateUser.class);
                CreateUserRepresentation representation = mapper.readValue(createUser.representation, CreateUserRepresentation.class);
                String publicId = createUser.resourcePath.substring(6);
                String username = representation.username;
                userRepository.save(new User(username, publicId));
                LOGGER.info("New user added {}: {}", username, publicId);
            } catch (JsonProcessingException e) {
                LOGGER.error("Cannot deserialize create user: {}, {}", consumerRecord.key(), consumerRecord.value());
            }
        }
    }
}
