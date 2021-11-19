package com.alay.billing.services;

import com.alay.billing.entities.Task;
import com.alay.billing.repositories.TaskRepository;
import com.alay.events.TaskCostUpdated;
import com.alay.events.TaskCreated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static com.alay.billing.KafkaConfig.TASK_CREATED_TOPIC;
import static com.alay.billing.services.KafkaUtil.sendEvent;

@Slf4j
@Service
public class TaskService {

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private final KafkaTemplate<String, TaskCostUpdated> taskCostUpdatedTemplate;

    private final TaskRepository taskRepository;
    private final BillingService billingService;

    public TaskService(TaskRepository taskRepository, BillingService billingService,
                       KafkaTemplate<String, TaskCostUpdated> taskCostUpdatedTemplate) {
        this.taskCostUpdatedTemplate = taskCostUpdatedTemplate;
        this.taskRepository = taskRepository;
        this.billingService = billingService;
    }

    // TODO Rely on retry or explicitly handle DataIntegrityViolationException?
    @KafkaListener(topics = TASK_CREATED_TOPIC, containerFactory = "taskCreatedContainerFactory")
    public void taskCreated(TaskCreated taskCreated) {
        log.info(Objects.toString(taskCreated));
        Task task = taskRepository
                .findByPublicId(taskCreated.getPublicTaskId())
                .orElse(new Task(taskCreated.getPublicTaskId(), taskCreated.getTitle(), taskCreated.getJiraId()));
        task.setCost(random.nextLong(20));
        taskRepository.save(task);
        sendEvent(new TaskCostUpdated(task.getPublicId(), task.getCost()), taskCostUpdatedTemplate);
    }
}
