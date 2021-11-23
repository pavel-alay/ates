package com.alay.billing.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Public Id is mandatory")
    @Column(name = "public_id", unique = true)
    private String publicId;

    private String title;

    @Column(name = "jira_id")
    private String jiraId;

    private long fee;

    private long reward;

    @OneToMany(mappedBy = "task")
    @PrimaryKeyJoinColumn
    private List<Transaction> transaction;

    public static TaskBuilder builder(String publicId, long fee, long reward) {
        return new TaskBuilder()
                .publicId(publicId)
                .fee(fee)
                .reward(reward);
    }
}
