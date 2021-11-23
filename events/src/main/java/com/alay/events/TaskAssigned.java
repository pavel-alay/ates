package com.alay.events;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskAssigned {
    private String publicTaskId;
    private String publicUserId;
    private String title;
    private String jiraId;

    public TaskAssigned(String publicUserId, String publicTaskId, String title, String jiraId) {
        this.publicTaskId = publicTaskId;
        this.publicUserId = publicUserId;
        this.title = title;
        this.jiraId = jiraId;
    }
}
