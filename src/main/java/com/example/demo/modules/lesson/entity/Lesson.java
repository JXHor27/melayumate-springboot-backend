package com.example.demo.modules.lesson.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
public class Lesson {
    private String lessonId;
    private String title;
    private String description;
    private Instant createdAt;
    private boolean isAvailable;
}
