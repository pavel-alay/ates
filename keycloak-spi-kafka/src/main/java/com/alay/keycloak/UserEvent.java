package com.alay.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;

import java.util.Map;

/**
 * Possible issues with consistency: it's important to send events in the right order.
 * The actual role mapping could be fetched from keycloak_role.name keycloak_role.client=FALSE and user_role_mapping.
 */
public class UserEvent implements Event {

    public enum EventType {
        ADDED,
        DELETED
    }

    final String userId;
    final String username;
    final EventType type;

    public UserEvent(AdminEvent e) throws JsonProcessingException {
        Map<String, Object> map = mapper.readValue(e.getRepresentation(), Map.class);
        username = (String) map.get("username");
        userId = e.getResourcePath().split("/")[1];
        if (e.getOperationType() == OperationType.CREATE) {
            type = EventType.ADDED;
        } else if (e.getOperationType() == OperationType.DELETE) {
            type = EventType.DELETED;
        } else {
            throw new IllegalStateException("Unsupported operation type");
        }
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public EventType getType() {
        return type;
    }
}
