package com.alay.events;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskCreated {
    String publicTaskId;
    String jiraId;
    String title;

    public TaskCreated(String publicTaskId, String jiraId, String title) {
        this.publicTaskId = publicTaskId;
        this.jiraId = jiraId;
        this.title = title;
    }
}
