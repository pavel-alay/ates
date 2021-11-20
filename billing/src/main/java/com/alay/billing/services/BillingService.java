package com.alay.billing.services;

import com.alay.billing.entities.Task;
import com.alay.billing.entities.Transaction;
import com.alay.billing.entities.TransactionType;
import com.alay.billing.entities.User;
import com.alay.billing.repositories.TransactionRepository;
import com.alay.events.TaskAssigned;
import com.alay.events.TaskCompleted;
import com.alay.events.TransactionCreated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static com.alay.billing.KafkaConsumerConfig.TASK_ASSIGNED_TOPIC;
import static com.alay.billing.KafkaConsumerConfig.TASK_COMPLETED_TOPIC;
import static com.alay.billing.services.KafkaUtil.sendEvent;

@Slf4j
@Service
public class BillingService {

    private final TaskService taskService;
    private final UserService userService;
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, TransactionCreated> transactionCreatedTemplate;

    public BillingService(TaskService taskService, UserService userService,
                          TransactionRepository transactionRepository,
                          KafkaTemplate<String, TransactionCreated> transactionCreatedTemplate) {
        this.taskService = taskService;
        this.userService = userService;
        this.transactionRepository = transactionRepository;
        this.transactionCreatedTemplate = transactionCreatedTemplate;
    }

    @KafkaListener(topics = TASK_ASSIGNED_TOPIC, containerFactory = "taskAssignedContainerFactory")
    public void taskAssigned(TaskAssigned taskAssigned) {
        log.info("<<< {}", taskAssigned);
        User user = userService.findOrCreateUser(taskAssigned.getPublicUserId());
        Task task = taskService.findOrCreateTask(taskAssigned.getPublicTaskId());
        Transaction transaction = Transaction.builder()
                .user(user).task(task).debit(task.getFee()).type(TransactionType.TASK_ASSIGNED)
                .build();
        transactionRepository.save(transaction);
        sendEvent(transactionToEvent(transaction), transactionCreatedTemplate);
    }

    @KafkaListener(topics = TASK_COMPLETED_TOPIC, containerFactory = "taskCompletedContainerFactory")
    public void taskCompleted(TaskCompleted taskCompleted) {
        log.info("<<< {}", taskCompleted);
        User user = userService.findOrCreateUser(taskCompleted.getPublicUserId());
        Task task = taskService.findOrCreateTask(taskCompleted.getPublicTaskId());
        Transaction transaction = Transaction.builder()
                .user(user).task(task).credit(task.getReward()).type(TransactionType.TASK_COMPLETED)
                .build();
        transactionRepository.save(transaction);
        sendEvent(transactionToEvent(transaction), transactionCreatedTemplate);
    }

    private TransactionCreated transactionToEvent(Transaction transaction) {
        com.alay.events.TransactionType transactionType = switch (transaction.getType()) {
            case TASK_ASSIGNED -> com.alay.events.TransactionType.TASK_ASSIGNED;
            case TASK_COMPLETED -> com.alay.events.TransactionType.TASK_COMPLETED;
            case PAYMENT -> com.alay.events.TransactionType.PAYMENT;
        };
        return new TransactionCreated(transaction.getPublicId(), transaction.getUser().getPublicId(), transaction.getTask().getPublicId(),
                transaction.getCredit(), transaction.getDebit(), transactionType);
    }

    public Iterable<Transaction> findAll() {
        List<Transaction> transactions = transactionRepository.findAll();
        transactions.sort(Comparator.comparing(Transaction::getCreatedAt, Comparator.reverseOrder()));
        return transactions;
    }
}
