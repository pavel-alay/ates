package com.alay.tasktracker.services;

import com.alay.tasktracker.entities.User;
import com.alay.tasktracker.events.EventConsumer;
import com.alay.tasktracker.repositories.UserRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

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

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void processUserEvent(ConsumerRecord<String, String> consumerRecord) {
        if ("USER/CREATE".equals(consumerRecord.key())) {
            try {
                CreateUser createUser = MAPPER.readValue(consumerRecord.value(), CreateUser.class);
                CreateUserRepresentation representation = MAPPER.readValue(createUser.representation, CreateUserRepresentation.class);
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
