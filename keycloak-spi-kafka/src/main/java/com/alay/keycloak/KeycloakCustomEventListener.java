package com.alay.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.admin.AdminEvent;

public class KeycloakCustomEventListener implements EventListenerProvider {

    public static final String ADMIN_EVENTS_TOPIC = "keycloak_admin_events.stream";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onEvent(Event event) {
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        String operation = adminEvent.getResourceTypeAsString() + "/" + adminEvent.getOperationType();
        System.out.println("Admin Event: " + operation);

        try {
            Producer.publishEvent(ADMIN_EVENTS_TOPIC, operation,
                    mapper.writeValueAsString(adminEvent));
        } catch (JsonProcessingException e) {
            System.out.println("ERROR. Admin Event: " + operation);
        }
    }

    @Override
    public void close() {
    }
}
