package com.alay.billing.services;

import com.alay.events.TransactionCreated;
import com.alay.events.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.alay.billing.services.KafkaUtil.sendEvent;

@Slf4j
@Service
public class BillingService {

    private final KafkaTemplate<String, TransactionCreated> transactionCreatedTemplate;

    public BillingService(KafkaTemplate<String, TransactionCreated> transactionCreatedTemplate) {
        this.transactionCreatedTemplate = transactionCreatedTemplate;
    }

    public void transactionCreated(String publicUserId, long credit, long debit, TransactionType type) {
        sendEvent(new TransactionCreated(publicUserId, credit, debit, type),
                transactionCreatedTemplate);
    }

}
