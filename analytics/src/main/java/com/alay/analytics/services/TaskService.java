package com.alay.analytics.services;

import com.alay.analytics.repositories.TaskRepository;
import com.alay.events.TaskCompleted;
import com.alay.events.TaskCreated;
import com.alay.events.TaskUpdated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.alay.events.Topics.TASK_COMPLETED_TOPIC;
import static com.alay.events.Topics.TASK_CREATED_TOPIC;
import static com.alay.events.Topics.TASK_UPDATED_TOPIC;

@Slf4j
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @KafkaListener(topics = TASK_CREATED_TOPIC, containerFactory = "taskCreatedContainerFactory")
    public void taskCreated(TaskCreated taskCreated) {
        log.info("<<< {}", taskCreated);
        taskRepository.updateOrCreate(taskCreated.getPublicTaskId(), taskCreated.getTitle(), taskCreated.getJiraId());
    }

    @KafkaListener(topics = TASK_UPDATED_TOPIC, containerFactory = "taskUpdatedContainerFactory")
    public void taskUpdated(TaskUpdated taskUpdated) {
        log.info("<<< {}", taskUpdated);
        taskRepository.updateFeeAndReward(taskUpdated.getPublicTaskId(), taskUpdated.getFee(), taskUpdated.getReward());
    }

    @KafkaListener(topics = TASK_COMPLETED_TOPIC, containerFactory = "taskCompletedContainerFactory")
    public void taskCompleted(TaskCompleted taskCompleted) {
        log.info("<<< {}", taskCompleted);
        taskRepository.updateCompletedAt(taskCompleted.getPublicTaskId(), taskCompleted.getCompletedAt());
    }

}
