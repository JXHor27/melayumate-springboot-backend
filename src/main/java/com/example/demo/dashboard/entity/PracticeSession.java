package com.example.demo.dashboard.entity;

import com.example.demo.enums.PracticeType;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@ToString
public class PracticeSession {
    private String practiceId;
    private String userId;
    private PracticeType practiceType;
    private Instant completedAt;

}
