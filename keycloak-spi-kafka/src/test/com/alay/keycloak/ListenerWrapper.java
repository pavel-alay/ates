package com.alay.keycloak;

public class ListenerWrapper extends Listener {

    String topic;
    String event;

    public ListenerWrapper() {
        super(null);
    }

    @Override
    protected void publishEvent(String topic, String key, String event) {
        this.topic = topic;
        this.event = event;
    }

    public String getTopic() {
        return topic;
    }

    public String getEvent() {
        return event;
    }
}
