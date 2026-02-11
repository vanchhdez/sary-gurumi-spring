package com.sary_gurumi.auth_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class User {
    @Id
    private String email;
    private String password;
    private String role; // "ADMIN" or "CLIENT"

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    private int failedLoginAttempts = 0;
    private LocalDateTime lastFailedLogin;
}