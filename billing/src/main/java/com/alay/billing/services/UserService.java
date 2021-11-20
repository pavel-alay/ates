package com.alay.billing.services;

import com.alay.billing.entities.User;
import com.alay.billing.repositories.UserRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
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

    public User findOrCreateUser(String publicId) {
        return userRepository.findByPublicId(publicId).orElseGet(() -> tryToCreate(User.builder()
                .publicId(publicId)
                .build(), false));
    }

    public User updateOrCreateUser(String publicId, String username) {
        Optional<User> user = userRepository.findByPublicId(publicId);
        if (user.isEmpty()) {
            return tryToCreate(User.builder()
                    .publicId(publicId).username(username)
                    .build(), true);
        } else {
            user.get().setUsername(username);
            return userRepository.saveAndFlush(user.get());
        }
    }

    // Simplify with @Retry
    private User tryToCreate(User user, boolean update) {
        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            log.info("tryToCreate retry: {}", e.getMessage());
            User newUser = userRepository.findByPublicId(user.getPublicId())
                    .orElseThrow(() -> new IllegalStateException("Cannot create nor update User", e));
            if (update) {
                return userRepository.saveAndFlush(newUser.setUsername(user.getUsername()));
            } else {
                return newUser;
            }
        } catch (Exception e) {
            log.error("tryToCreate failed: {}", e.getMessage());
            throw e;
        }
    }
}
