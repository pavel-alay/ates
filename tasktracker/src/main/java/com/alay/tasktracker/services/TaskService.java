package com.alay.tasktracker.services;

import com.alay.events.TaskAssigned;
import com.alay.events.TaskCompleted;
import com.alay.events.TaskCreated;
import com.alay.tasktracker.entities.Task;
import com.alay.tasktracker.entities.TaskStatus;
import com.alay.tasktracker.entities.User;
import com.alay.tasktracker.repositories.TaskRepository;
import com.alay.tasktracker.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TaskService {

    private static final String TASK_CREATED_TOPIC = "task-created.stream";
    private static final String TASK_ASSIGNED_TOPIC = "task-assigned";
    private static final String TASK_COMPLETED_TOPIC = "task-completed";

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private final KafkaTemplate<String, TaskCreated> taskCreatedTemplate;
    private final KafkaTemplate<String, TaskAssigned> taskAssignedTemplate;
    private final KafkaTemplate<String, TaskCompleted> taskCompletedTemplate;


    public TaskService(TaskRepository taskRepository, UserRepository userRepository,
                       KafkaTemplate<String, TaskCreated> taskCreatedTemplate,
                       KafkaTemplate<String, TaskAssigned> taskAssignedTemplate,
                       KafkaTemplate<String, TaskCompleted> taskCompletedTemplate) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskCreatedTemplate = taskCreatedTemplate;
        this.taskAssignedTemplate = taskAssignedTemplate;
        this.taskCompletedTemplate = taskCompletedTemplate;
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
        task = taskRepository.save(task
            .setStatus(TaskStatus.COMPLETED)
            .setCompletedAt(LocalDateTime.now()));
        publishEvent(new TaskCompleted(task.getPublicId(), user.getPublicId(), task.getCompletedAt()),
            TASK_COMPLETED_TOPIC, taskCompletedTemplate);
    }

    /**
     * Save the task to repository and assign it to a random user.
     */
    public void createTask(Task task) {
        taskRepository.save(task);
        assignTask(task);
        publishEvent(new TaskCreated(task.getPublicId(), task.getJiraId(), task.getTitle()),
            TASK_CREATED_TOPIC, taskCreatedTemplate);
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
    public void assignTask(Task task) {
        User user = userRepository.findRandomUser()
            .orElseThrow(() -> new IllegalStateException("There is no users"));
        reassignTask(task, user);
    }

    // TODO switch to batch operations for both db and mb.
    public void reassignTask(Task task, User user) {
        task.setUserId(user.getId());
        taskRepository.save(task);
        publishEvent(new TaskAssigned(user.getPublicId(), task.getPublicId(), task.getTitle(), task.getJiraId()),
            TASK_ASSIGNED_TOPIC, taskAssignedTemplate);
    }

    private static <T> void publishEvent(T event, String topic, KafkaTemplate<String, T> template) {
        log.info("sending payload='{}' to topic='{}'", event, topic);
        template.send(topic, event);
    }
}
