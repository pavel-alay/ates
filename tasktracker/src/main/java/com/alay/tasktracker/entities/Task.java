package com.alay.tasktracker.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    @Column(name = "public_id")
    private String publicId = UUID.randomUUID().toString();

    @Column(name = "user_id")
    private Long userId;

    @NotBlank(message = "Title is mandatory")
    @Pattern(regexp = "[^\\[\\]]*", message = "Title must not contain [ or ]")
    private String title;

    @NotBlank(message = "JIRA id is mandatory")
    @Column(name = "jira_id")
    private String jiraId;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.OPEN;
}
