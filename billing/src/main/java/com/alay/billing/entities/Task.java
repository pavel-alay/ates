package com.alay.billing.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Public Id is mandatory")
    @Column(name = "public_id", unique = true)
    private String publicId = UUID.randomUUID().toString();

    @NotBlank(message = "Title is mandatory")
    @Pattern(regexp = "[^\\[\\]]*", message = "Title must not contain [ or ]")
    private String title;

    @NotBlank(message = "JIRA id is mandatory")
    @Column(name = "jira_id")
    private String jiraId;

    private Long cost;

    @OneToOne(mappedBy = "task")
    @PrimaryKeyJoinColumn
    private Transaction transaction;

    public Task(String publicId, String title, String jiraId) {
        this.publicId = publicId;
        this.title = title;
        this.jiraId = jiraId;
    }
}
