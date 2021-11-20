package com.alay.billing.services;

import com.alay.billing.entities.Task;
import com.alay.billing.repositories.TaskRepository;
import com.alay.events.TaskCreated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static com.alay.billing.KafkaConsumerConfig.TASK_CREATED_TOPIC;

@Slf4j
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final long minFee;
    private final long maxFee;

    private final long minReward;
    private final long maxReward;

    public TaskService(TaskRepository taskRepository,
                       @Value("${min-fee}") long minFee, @Value("${max-fee}") long maxFee,
                       @Value("${min-reward}") long minReward, @Value("${max-reward}") long maxReward) {

        this.taskRepository = taskRepository;
        this.minFee = minFee;
        this.maxFee = maxFee;
        this.minReward = minReward;
        this.maxReward = maxReward;
    }

    @KafkaListener(topics = TASK_CREATED_TOPIC, containerFactory = "taskCreatedContainerFactory")
    public void taskCreated(TaskCreated taskCreated) {
        log.info("<<< {}", taskCreated);
        Task task = updateOrCreateTask(taskCreated.getPublicTaskId(), taskCreated.getTitle(), taskCreated.getJiraId());
        log.info("Task created {}", task);
    }

    public Task findOrCreateTask(String publicId) {
        return taskRepository.findByPublicId(publicId)
                .orElseGet(() -> tryToCreate(Task.builder(publicId, getFee(), getReward()).build(), false));
    }

    public Task updateOrCreateTask(String publicId, String title, String jiraId) {
        Optional<Task> task = taskRepository.findByPublicId(publicId);
        if (task.isEmpty()) {
            return tryToCreate(Task.builder(publicId, getFee(), getReward())
                    .title(title).jiraId(jiraId)
                    .build(), true);
        } else {
            return taskRepository.saveAndFlush(
                    task.get().setTitle(title).setJiraId(jiraId));
        }
    }

    long getFee() {
        return ThreadLocalRandom.current().nextLong(minFee, maxFee);
    }

    long getReward() {
        return ThreadLocalRandom.current().nextLong(minReward, maxReward);
    }

    // Simplify with @Retry
    private Task tryToCreate(Task task, boolean update) {
        try {
            return taskRepository.saveAndFlush(task);
        } catch (DataIntegrityViolationException e) {
            log.info("tryToCreate retry: {}", e.getMessage());
            Task newTask =  taskRepository.findByPublicId(task.getPublicId())
                    .orElseThrow(() -> new IllegalStateException("Cannot create nor update Task", e));
            if (update) {
                return taskRepository.saveAndFlush(newTask
                        .setTitle(task.getTitle())
                        .setJiraId(task.getJiraId()));
            } else {
                return newTask;
            }
        } catch (Exception e) {
            log.error("tryToCreate failed: {}", e.getMessage());
            throw e;
        }
    }
}
