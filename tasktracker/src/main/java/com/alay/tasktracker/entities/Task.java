package com.alay.tasktracker.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Public Id is mandatory")
    @Column(name = "public_id")
    private String publicId = UUID.randomUUID().toString();

    @Column(name = "public_user_id")
    private String publicUserId;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.Open;
}
