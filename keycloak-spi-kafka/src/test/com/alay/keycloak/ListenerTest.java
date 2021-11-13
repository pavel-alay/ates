package com.alay.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.keycloak.events.admin.AdminEvent;

import static org.assertj.core.api.Assertions.assertThat;

class ListenerTest {

    ObjectMapper mapper = new ObjectMapper();

    String roleAdded = "{\n" +
            "    \"authDetails\": {\n" +
            "        \"clientId\": \"3280a973-d7ed-42bd-ad9c-e9320f2d2549\",\n" +
            "        \"ipAddress\": \"192.168.1.208\",\n" +
            "        \"realmId\": \"master\",\n" +
            "        \"userId\": \"b124dbef-0213-49fd-8440-154ce4ad4d38\"\n" +
            "    },\n" +
            "    \"error\": null,\n" +
            "    \"id\": \"c5ba3b30-ee8b-4868-a13a-adfaf96ba60b\",\n" +
            "    \"operationType\": \"CREATE\",\n" +
            "    \"realmId\": \"ates\",\n" +
            "    \"representation\": \"[{\\\"id\\\":\\\"c8ee67ae-2bd6-4a0c-b77c-71af78a4c76b\\\",\\\"name\\\":\\\"manager\\\",\\\"composite\\\":false,\\\"clientRole\\\":false,\\\"containerId\\\":\\\"ates\\\"}]\",\n" +
            "    \"resourcePath\": \"users/f6432fb3-d08b-4cf2-b3c0-5cb799b7ca17/role-mappings/realm\",\n" +
            "    \"resourceType\": \"REALM_ROLE_MAPPING\",\n" +
            "    \"resourceTypeAsString\": \"REALM_ROLE_MAPPING\",\n" +
            "    \"time\": 1636268439636\n" +
            "}";

    @Test
    void onRoleAdded() throws JsonProcessingException {
        AdminEvent adminEvent = mapper.readValue(roleAdded, AdminEvent.class);
        ListenerWrapper listenerWrapper = new ListenerWrapper();
        listenerWrapper.onEvent(adminEvent, true);
        assertThat(listenerWrapper.getTopic()).isEqualTo(Listener.ROLE_CHANGED_TOPIC);
        assertThat(listenerWrapper.getEvent()).isNotNull().
                contains("manager").
                contains("f6432fb3-d08b-4cf2-b3c0-5cb799b7ca17").
                contains("ADDED");

    }


    String userAdded = "{\n" +
            "    \"authDetails\": {\n" +
            "        \"clientId\": \"3280a973-d7ed-42bd-ad9c-e9320f2d2549\",\n" +
            "        \"ipAddress\": \"192.168.1.208\",\n" +
            "        \"realmId\": \"master\",\n" +
            "        \"userId\": \"b124dbef-0213-49fd-8440-154ce4ad4d38\"\n" +
            "    },\n" +
            "    \"error\": null,\n" +
            "    \"id\": \"b238ba94-6cb2-4f0b-84e5-ae6f668b28fb\",\n" +
            "    \"operationType\": \"CREATE\",\n" +
            "    \"realmId\": \"ates\",\n" +
            "    \"representation\": \"{\\\"username\\\":\\\"manager1\\\",\\\"enabled\\\":true,\\\"attributes\\\":{},\\\"groups\\\":[]}\",\n" +
            "    \"resourcePath\": \"users/f6432fb3-d08b-4cf2-b3c0-5cb799b7ca17\",\n" +
            "    \"resourceType\": \"USER\",\n" +
            "    \"resourceTypeAsString\": \"USER\",\n" +
            "    \"time\": 1636268430640\n" +
            "}";

    @Test
    void onUserAdded() throws JsonProcessingException {
        AdminEvent adminEvent = mapper.readValue(userAdded, AdminEvent.class);
        ListenerWrapper listenerWrapper = new ListenerWrapper();
        listenerWrapper.onEvent(adminEvent, true);
        assertThat(listenerWrapper.getTopic()).isEqualTo(Listener.USER_EVENTS_TOPIC);
        assertThat(listenerWrapper.getEvent()).isNotNull().
                contains("manager1").
                contains("f6432fb3-d08b-4cf2-b3c0-5cb799b7ca17").
                contains("ADDED");
    }

    @Disabled
    @Test
    void publishEvent() throws JsonProcessingException {
        AdminEvent adminEvent = mapper.readValue(userAdded, AdminEvent.class);
        Listener listener = new Listener(new Producer());
        listener.onEvent(adminEvent, true);
    }
}