package com.alay.analytics.services;

import com.alay.analytics.entities.User;
import com.alay.analytics.repositories.UserRepository;
import com.alay.events.BalanceUpdated;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.alay.events.Topics.ADMIN_EVENTS_TOPIC;
import static com.alay.events.Topics.USER_BALANCE_UPDATED;

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
                User user = userRepository.updateOrCreate(createUser.resourcePath.substring(6), representation.username);
                log.info("<<< UserCreated ({} {})", user.getUsername(), user.getPublicId());
            } catch (JsonProcessingException e) {
                log.error("Cannot deserialize create user: {}, {}", consumerRecord.key(), consumerRecord.value());
            }
        }
    }

    @KafkaListener(topics = USER_BALANCE_UPDATED, containerFactory = "balanceUpdatedContainerFactory")
    public void taskCreated(BalanceUpdated balanceUpdated) {
        log.info("<<< {}", balanceUpdated);
        userRepository.updateBalance(balanceUpdated.getPublicUserId(), balanceUpdated.getBalance());
    }
}
