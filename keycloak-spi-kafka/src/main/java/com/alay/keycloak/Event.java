package com.alay.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface Event {

    ObjectMapper mapper = new ObjectMapper();

    default String asJson() throws JsonProcessingException {
        return mapper.writeValueAsString(this);
    }
}
