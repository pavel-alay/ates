package com.alay.keycloak;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class ProducerTest {

    @Test
    void publishEvent() {
        Producer.publishEvent(KeycloakCustomEventListener.ADMIN_EVENTS_TOPIC, "TEST/TEST", "{}");
    }
}
