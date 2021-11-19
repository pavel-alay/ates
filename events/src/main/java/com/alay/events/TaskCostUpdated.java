package com.alay.events;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskCostUpdated {
    String publicTaskId;
    long cost;

    public TaskCostUpdated(String publicTaskId, long cost) {
        this.publicTaskId = publicTaskId;
        this.cost = cost;
    }
}
