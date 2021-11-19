package com.alay.events;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskAssigned {
    String publicTaskId;
    String publicUserId;

    public TaskAssigned(String publicTaskId, String publicUserId) {
        this.publicTaskId = publicTaskId;
        this.publicUserId = publicUserId;
    }
}
