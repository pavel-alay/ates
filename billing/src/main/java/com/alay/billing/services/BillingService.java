package com.alay.billing.services;

import com.alay.billing.entities.BillingCycle;
import com.alay.billing.entities.Payment;
import com.alay.billing.entities.Task;
import com.alay.billing.entities.Transaction;
import com.alay.billing.entities.TransactionType;
import com.alay.billing.entities.User;
import com.alay.billing.repositories.BillingCycleRepository;
import com.alay.billing.repositories.PaymentRepository;
import com.alay.billing.repositories.TransactionRepository;
import com.alay.billing.repositories.UserRepository;
import com.alay.events.BalanceUpdated;
import com.alay.events.TaskAssigned;
import com.alay.events.TaskCompleted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

import static com.alay.events.Topics.TASK_ASSIGNED_TOPIC;
import static com.alay.events.Topics.TASK_COMPLETED_TOPIC;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
public class BillingService {

    private final TaskService taskService;


    private final TransactionRepository transactionRepository;
    private final BillingCycleRepository billingCycleRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    private final TransactionTemplate transactionTemplate;

    private final KafkaTemplate<String, BalanceUpdated> balanceUpdatedTemplate;
    private final KafkaService kafkaService;

    public BillingService(TaskService taskService,
                          TransactionRepository transactionRepository, BillingCycleRepository billingCycleRepository,
                          PaymentRepository paymentRepository,
                          UserRepository userRepository, TransactionTemplate transactionTemplate,
                          KafkaTemplate<String, BalanceUpdated> balanceUpdatedTemplate, KafkaService kafkaService) {
        this.taskService = taskService;
        this.transactionRepository = transactionRepository;
        this.billingCycleRepository = billingCycleRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.transactionTemplate = transactionTemplate;
        this.balanceUpdatedTemplate = balanceUpdatedTemplate;
        this.kafkaService = kafkaService;
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Transactional
    @KafkaListener(topics = TASK_ASSIGNED_TOPIC, containerFactory = "taskAssignedContainerFactory")
    public void taskAssigned(TaskAssigned taskAssigned) {
        log.info("<<< {}", taskAssigned);
        User user = userRepository.findOrCreateUser(taskAssigned.getPublicUserId());
        Task task = taskService.findOrCreateTask(taskAssigned.getPublicTaskId());
        Transaction transaction = Transaction.builder()
            .user(user).task(task).debit(task.getFee()).type(TransactionType.TASK_ASSIGNED)
            .build();
        transactionRepository.save(transaction);
        user = userRepository.debitBalance(user, task.getFee());
        sendBalanceUpdatedEvent(user, BalanceUpdated.Origin.TASK_ASSIGNED);
    }

    @Transactional
    @KafkaListener(topics = TASK_COMPLETED_TOPIC, containerFactory = "taskCompletedContainerFactory")
    public void taskCompleted(TaskCompleted taskCompleted) {
        log.info("<<< {}", taskCompleted);
        User user = userRepository.findOrCreateUser(taskCompleted.getPublicUserId());
        Task task = taskService.findOrCreateTask(taskCompleted.getPublicTaskId());
        Transaction transaction = Transaction.builder()
            .user(user).task(task).credit(task.getReward()).type(TransactionType.TASK_COMPLETED)
            .build();
        transactionRepository.save(transaction);
        user = userRepository.creditBalance(user, task.getReward());
        sendBalanceUpdatedEvent(user, BalanceUpdated.Origin.TASK_COMPLETED);
    }

    /**
     * Transactional annotation skipped explicitly.
     * We need more gradual transaction control for each user, to avoid rollback after sending an event.
     * <br/><br/>
     * Note, Transactional won't work for {@link #closeBillingCycle}, because it must be called from another Bean,
     * proxy method won't be created for the method call within the same class.
     */
    public void createPayments() {
        transactionRepository.getOpenTaskTransactions().stream()
            .collect(groupingBy(Transaction::getUser, toSet()))
            .forEach(this::closeBillingCycle);
    }

    protected void closeBillingCycle(User user, Set<Transaction> transactions) {
        long amount = transactions.stream()
            .mapToLong(t -> t.getCredit() - t.getDebit())
            .sum();
        if (amount > 0) {
            log.warn("closing billing cycle for user {}", user);
            transactionTemplate.executeWithoutResult(status -> {
                BillingCycle billingCycle = BillingCycle.builder()
                    .user(user).transaction(transactions).build();
                billingCycle = billingCycleRepository.save(billingCycle);
                Payment payment = Payment.builder().billingCycle(billingCycle).amount(amount).build();
                payment = paymentRepository.save(payment);
                Transaction paymentTransaction = Transaction.builder()
                    .user(user).payment(payment).type(TransactionType.PAYMENT).debit(amount)
                    .createdAt(payment.getCreatedAt())
                    .build();
                transactionRepository.save(paymentTransaction);
                User updatedUser = userRepository.debitBalance(user, amount);
                sendBalanceUpdatedEvent(updatedUser, BalanceUpdated.Origin.PAYMENT);
            });
        }
    }

    private void sendBalanceUpdatedEvent(User user, BalanceUpdated.Origin origin) {
        kafkaService.sendEvent(
            BalanceUpdated.builder()
                .publicUserId(user.getPublicId())
                .balance(user.getBalance())
                .type(origin)
                .build(),
            balanceUpdatedTemplate);
    }
}
