package com.alay.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;

public class Listener implements EventListenerProvider {

    public static final String USER_EVENTS_TOPIC = "user_management.stream";
    public static final String ROLE_CHANGED_TOPIC = "role_changed.stream";
    private final Producer producer;

    public Listener(Producer producer) {
        this.producer = producer;
    }

    @Override
    public void onEvent(org.keycloak.events.Event event) {
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        String operation = adminEvent.getResourceTypeAsString() + "/" + adminEvent.getOperationType();
        System.out.println("Admin Event: " + operation);

        try {
            switch (adminEvent.getResourceType()) {
                case REALM_ROLE_MAPPING:
                    realmRoleMappingType(adminEvent);
                    break;
                case USER:
                    userType(adminEvent);
                    break;
            }
        } catch (Exception e) {
            System.err.printf("Failed to send event %s %s%n", adminEvent.getOperationType(), adminEvent.getResourcePath());
        }
    }

    private void realmRoleMappingType(AdminEvent event) throws JsonProcessingException {
        if (event.getOperationType() == OperationType.CREATE || event.getOperationType() != OperationType.DELETE) {
            publishEvent(ROLE_CHANGED_TOPIC, event.getOperationType().toString(),
                    new RoleEvent(event).asJson());
        }
    }

    private void userType(AdminEvent event) throws JsonProcessingException {
        if (event.getOperationType() == OperationType.CREATE || event.getOperationType() != OperationType.DELETE) {
            publishEvent(USER_EVENTS_TOPIC, event.getOperationType().toString(),
                    new UserEvent(event).asJson());
        }
    }

    protected void publishEvent(String topic, String key, String event) {
        producer.send(new ProducerRecord<>(topic, key, event));
    }

    @Override
    public void close() {
        producer.close();
    }
}
