package com.alay.tasktracker.events;

import com.alay.tasktracker.entities.Task;
import com.alay.tasktracker.entities.User;
import lombok.Data;

@Data
public class TaskCompleted {
    final String publicTaskId;
    final String publicUserId;

    public TaskCompleted(Task task, User user) {
        this.publicTaskId = task.getPublicId();
        this.publicUserId = user.getPublicId();
    }
}
