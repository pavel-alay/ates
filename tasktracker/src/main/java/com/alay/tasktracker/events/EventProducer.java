package com.alay.tasktracker.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventProducer.class);

    private final KafkaTemplate<String, TaskCreated> taskCreatedTemplate;
    private static final String TASK_CREATED_TOPIC = "task-created.stream";

    private final KafkaTemplate<String, TaskAssigned> taskAssignedTemplate;
    private static final String TASK_ASSIGNED_TOPIC = "task-assigned";

    private final KafkaTemplate<String, TaskCompleted> taskCompletedTemplate;
    private static final String TASK_COMPLETED_TOPIC = "task-completed";

    public EventProducer(KafkaTemplate<String, TaskAssigned> taskAssignedTemplate,
                         KafkaTemplate<String, TaskCompleted> taskCompletedTemplate,
                         KafkaTemplate<String, TaskCreated> taskCreatedTemplate) {
        this.taskAssignedTemplate = taskAssignedTemplate;
        this.taskCompletedTemplate = taskCompletedTemplate;
        this.taskCreatedTemplate = taskCreatedTemplate;
    }

    @Retryable(value = Exception.class, maxAttemptsExpression = "10",
            backoff = @Backoff(delayExpression = "5000"))
    public void taskCreated(String publicTaskId, String jiraId, String title) {
        publish(taskCreatedTemplate, new TaskCreated(publicTaskId, jiraId, title), TASK_CREATED_TOPIC);
    }

    public void taskAssigned(String publicTaskId, String publicUserId) {
        publish(taskAssignedTemplate, new TaskAssigned(publicTaskId, publicUserId), TASK_ASSIGNED_TOPIC);
    }

    public void taskCompleted(String publicTaskId, String publicUserId) {
        publish(taskCompletedTemplate, new TaskCompleted(publicTaskId, publicUserId), TASK_COMPLETED_TOPIC);
    }

    private static <T> void publish(KafkaTemplate<String, T> template, T event, String topic) {
        LOGGER.info("sending payload='{}' to topic='{}'", event, topic);
        template.send(topic, event);
    }
}
