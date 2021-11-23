package com.alay.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceUpdated {
    public enum Origin {
        TASK_ASSIGNED,
        TASK_COMPLETED,
        PAYMENT
    }

    String publicUserId;
    long balance;
    Origin type;
}
