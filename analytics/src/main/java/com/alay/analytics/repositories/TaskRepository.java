package com.alay.analytics.repositories;

import com.alay.analytics.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompletedAtBetweenAndRewardIsNotNullOrderByRewardDesc(LocalDateTime from, LocalDateTime to);

    default List<Task> findCompletedForTodayOrderByRewardDesc() {
        LocalDate today = LocalDate.now();
        return findByCompletedAtBetweenAndRewardIsNotNullOrderByRewardDesc(today.atStartOfDay(), today.plusDays(1).atStartOfDay());
    }

    Optional<Task> findByPublicId(String publicId);

    default Task findOrCreate(String publicId) {
        return findByPublicId(publicId)
            .orElseGet(() -> save(Task.builder()
                .publicId(publicId).build()));
    }

    default void updateOrCreate(String publicId, String title, String jiraId) {
        findByPublicId(publicId)
            .map(value -> save(value
                .setTitle(title).setJiraId(jiraId)))
            .orElseGet(() -> save(Task.builder()
                .publicId(publicId)
                .title(title).jiraId(jiraId)
                .build()));
    }

    default void updateCompletedAt(String publicId, LocalDateTime completedAt) {
        save(
            findOrCreate(publicId)
                .setCompletedAt(completedAt));
    }

    default void updateFeeAndReward(String publicId, Long fee, Long reward) {
        save(
            findOrCreate(publicId)
                .setFee(fee)
                .setReward(reward));
    }
}
