package com.alay.tasktracker.events;

import com.alay.tasktracker.entities.Task;
import lombok.Data;

@Data
public class TaskCreated {
    final String publicTaskId;
    final String jiraId;
    final String title;

    public TaskCreated(Task task) {
        this.publicTaskId = task.getPublicId();
        this.jiraId = task.getJiraId();
        this.title = task.getTitle();
    }
}
