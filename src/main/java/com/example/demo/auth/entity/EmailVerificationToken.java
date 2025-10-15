package com.example.demo.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class EmailVerificationToken {
    private String tokenId;
    private String email;
    private String tokenHash;
    private Instant expiresAt;
}
