package com.alay.tasktracker.events;

import lombok.Data;

@Data
public class TaskCreated {
    final String publicTaskId;
    final String jiraId;
    final String title;

    public TaskCreated(String publicTaskId, String jiraId, String title) {
        this.publicTaskId = publicTaskId;
        this.jiraId = jiraId;
        this.title = title;
    }
}
