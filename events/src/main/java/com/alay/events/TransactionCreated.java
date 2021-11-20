package com.alay.events;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionCreated {
    String publicId;
    String publicUserId;
    String publicTaskId;
    long credit;
    long debit;
    TransactionType type;

    public TransactionCreated(String publicId, String publicUserId, String publicTaskId, long credit, long debit, TransactionType type) {
        this.publicId = publicId;
        this.publicUserId = publicUserId;
        this.publicTaskId = publicTaskId;
        this.credit = credit;
        this.debit = debit;
        this.type = type;
    }
}
