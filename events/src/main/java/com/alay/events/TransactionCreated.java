package com.alay.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionCreated {
    String publicTransactionId;
    String publicUserId;
    String publicTaskId;
    String origin;
    long credit;
    long debit;
    TransactionType type;
}
