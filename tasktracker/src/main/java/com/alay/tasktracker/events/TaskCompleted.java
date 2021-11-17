package com.alay.tasktracker.events;

import lombok.Data;

@Data
public class TaskCompleted {
    final String publicTaskId;
    final String publicUserId;

    public TaskCompleted(String publicTaskId, String publicUserId) {
        this.publicTaskId = publicTaskId;
        this.publicUserId = publicUserId;
    }
}
