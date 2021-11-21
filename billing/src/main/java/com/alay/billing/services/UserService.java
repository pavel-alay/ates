package com.alay.billing.services;

import com.alay.billing.entities.User;
import com.alay.billing.repositories.UserRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.alay.billing.KafkaConsumerConfig.ADMIN_EVENTS_TOPIC;

@Slf4j
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

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = ADMIN_EVENTS_TOPIC)
    public void receiveUserEvent(ConsumerRecord<String, String> consumerRecord) {
        if ("USER/CREATE".equals(consumerRecord.key())) {
            try {
                CreateUser createUser = MAPPER.readValue(consumerRecord.value(), CreateUser.class);
                CreateUserRepresentation representation = MAPPER.readValue(createUser.representation, CreateUserRepresentation.class);
                User user = updateOrCreateUser(createUser.resourcePath.substring(6), representation.username);
                log.info("<<< UserCreated ({} {})", user.getUsername(), user.getPublicId());
            } catch (JsonProcessingException e) {
                log.error("Cannot deserialize create user: {}, {}", consumerRecord.key(), consumerRecord.value());
            }
        }
    }

    @Retryable
    public User findOrCreateUser(String publicId) {
        return userRepository.findByPublicId(publicId)
                .orElseGet(() -> userRepository.saveAndFlush(User.builder()
                        .publicId(publicId).build()));
    }

    private User updateOrCreateUser(String publicId, String username) {
        Optional<User> user = userRepository.findByPublicId(publicId);
        if (user.isEmpty()) {
            return userRepository.saveAndFlush(User.builder()
                    .publicId(publicId).username(username).build());
        } else {
            return userRepository.saveAndFlush(
                    user.get().setUsername(username));
        }
    }
}
