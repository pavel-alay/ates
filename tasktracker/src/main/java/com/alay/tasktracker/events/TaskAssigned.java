package com.alay.tasktracker.events;

import lombok.Data;

@Data
public class TaskAssigned {
    final String publicTaskId;
    final String publicUserId;

    public TaskAssigned(String publicTaskId, String publicUserId) {
        this.publicTaskId = publicTaskId;
        this.publicUserId = publicUserId;
    }
}
