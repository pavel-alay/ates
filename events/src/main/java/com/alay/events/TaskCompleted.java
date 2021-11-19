package com.alay.events;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskCompleted {
    String publicTaskId;
    String publicUserId;

    public TaskCompleted(String publicTaskId, String publicUserId) {
        this.publicTaskId = publicTaskId;
        this.publicUserId = publicUserId;
    }
}
