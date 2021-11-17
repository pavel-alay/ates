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

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Public Id is mandatory")
    @Column(name = "public_id")
    private String publicId;

    @NotBlank(message = "Name is mandatory")
    private String username;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

    public User(String username, String publicId) {
        this.username = username;
        this.publicId = publicId;
    }
}
