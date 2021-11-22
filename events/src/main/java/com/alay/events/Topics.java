package com.alay.events;

public final class Topics {
    private Topics() {}

    public static final String ADMIN_EVENTS_TOPIC = "keycloak_admin_events.stream";
    public static final String TASK_CREATED_TOPIC = "task-created.stream";
    public static final String TASK_UPDATED_TOPIC = "task-updated.stream";
    public static final String TASK_ASSIGNED_TOPIC = "task-assigned";
    public static final String TASK_COMPLETED_TOPIC = "task-completed";

    public static final String USER_BALANCE_UPDATED = "user-balance-updated.stream";

}
