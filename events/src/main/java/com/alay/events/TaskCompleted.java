package com.alay.events;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TaskCompleted {
    String publicTaskId;
    String publicUserId;
    LocalDateTime completedAt;

    public TaskCompleted(String publicTaskId, String publicUserId, LocalDateTime completedAt) {
        this.publicTaskId = publicTaskId;
        this.publicUserId = publicUserId;
        this.completedAt = completedAt;
    }
}
