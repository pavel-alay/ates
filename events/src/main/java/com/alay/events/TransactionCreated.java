package com.alay.events;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionCreated {
    String publicUserId;
    long credit;
    long debit;
    TransactionType type;

    public TransactionCreated(String publicUserId, long credit, long debit, TransactionType type) {
        this.type = type;
        this.publicUserId = publicUserId;
        this.credit = credit;
        this.debit = debit;
    }
}
