package com.example.demo.auth.entity;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class PasswordResetToken {
    private String tokenId;
    private String userId;
    private String tokenHash;
    private Instant expiresAt;
}
