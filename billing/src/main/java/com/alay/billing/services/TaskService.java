package com.alay.billing.services;

import com.alay.billing.entities.Task;
import com.alay.billing.repositories.TaskRepository;
import com.alay.events.TaskCreated;
import com.alay.events.TaskUpdated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static com.alay.events.Topics.TASK_CREATED_TOPIC;

@Slf4j
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    private final KafkaTemplate<String, TaskUpdated> taskUpdatedTemplate;
    private final KafkaService kafkaService;


    private final long minFee;
    private final long maxFee;

    private final long minReward;
    private final long maxReward;

    public TaskService(TaskRepository taskRepository,
                       KafkaTemplate<String, TaskUpdated> taskUpdatedTemplate, KafkaService kafkaService,
                       @Value("${min-fee}") long minFee, @Value("${max-fee}") long maxFee,
                       @Value("${min-reward}") long minReward, @Value("${max-reward}") long maxReward) {

        this.taskRepository = taskRepository;
        this.taskUpdatedTemplate = taskUpdatedTemplate;
        this.kafkaService = kafkaService;
        this.minFee = minFee;
        this.maxFee = maxFee;
        this.minReward = minReward;
        this.maxReward = maxReward;
    }

    @KafkaListener(topics = TASK_CREATED_TOPIC, containerFactory = "taskCreatedContainerFactory")
    public void taskCreated(TaskCreated taskCreated) throws InterruptedException {
        // Added to prevent concurrent add for task_created and task_assigned events.
        Thread.sleep(300);
        Task task = updateOrCreateTask(taskCreated.getPublicTaskId(), taskCreated.getTitle(), taskCreated.getJiraId());
        log.info("Task created {}", task);
        kafkaService.sendEvent(TaskUpdated.builder()
            .publicTaskId(task.getPublicId())
                .reward(task.getReward())
                    .fee(task.getFee())
            .build(), taskUpdatedTemplate);

    }

    @Retryable
    public Task findOrCreateTask(String publicId) {
        return taskRepository.findByPublicId(publicId)
                .orElseGet(() -> taskRepository.saveAndFlush(Task.builder(publicId, getFee(), getReward()).build()));
    }

    private Task updateOrCreateTask(String publicId, String title, String jiraId) {
        Optional<Task> task = taskRepository.findByPublicId(publicId);
        if (task.isEmpty()) {
            return taskRepository.saveAndFlush(Task.builder(publicId, getFee(), getReward())
                    .title(title).jiraId(jiraId)
                    .build());
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
}
