package com.alay.tasktracker.services;

import com.alay.tasktracker.entities.Task;
import com.alay.tasktracker.entities.TaskStatus;
import com.alay.tasktracker.entities.User;
import com.alay.tasktracker.events.EventProducer;
import com.alay.tasktracker.repositories.TaskRepository;
import com.alay.tasktracker.repositories.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    TaskRepository taskRepository;
    UserRepository userRepository;
    EventProducer producer;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, EventProducer producer) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.producer = producer;
    }

    /**
     * Reassign all open tasks to a random user.
     *
     * @throws IllegalStateException if there is no users.
     */
    public void reassignTasks() {
        List<User> users = userRepository.findRandomUsers();

        if (users.isEmpty()) {
            throw new IllegalStateException("There is no users");
        }

        int userIndex = 0;
        for (Task task : taskRepository.findRandomOpenTasks()) {
            reassignTask(task, users.get(userIndex));
            userIndex++;
            if (userIndex == users.size()) {
                userIndex = 0;
            }
        }
    }

    public void completeTask(long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found"));
        User user = userRepository.findById(task.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        task.setStatus(TaskStatus.COMPLETED);
        taskRepository.save(task);
        producer.taskCompleted(task.getPublicId(), user.getPublicId());
    }

    /**
     * Save the task to repository and assign it to a random user.
     */
    public void createTask(Task task) {
        taskRepository.save(task);
        assignTask(task);
        producer.taskCreated(task.getPublicId(), task.getJiraId(), task.getTitle());
    }

    public Iterable<Task> findAll() {
        return taskRepository.findAll(Sort.by(Sort.Direction.DESC, "status"));
    }

    /**
     * Assign a task to random user.
     *
     * @param task a task to assign
     * @throws IllegalStateException if there is no users.
     */
    protected void assignTask(Task task) {
        User user = userRepository.findRandomUser()
                .orElseThrow(() -> new IllegalStateException("There is no users"));
        reassignTask(task, user);
    }

    // TODO switch to batch operations for both db and mb.
    protected void reassignTask(Task task, User user) {
        task.setUserId(user.getId());
        taskRepository.save(task);
        producer.taskAssigned(task.getPublicId(), user.getPublicId());
    }
}
