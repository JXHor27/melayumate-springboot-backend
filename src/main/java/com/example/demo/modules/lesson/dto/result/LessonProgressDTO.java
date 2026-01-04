package com.example.demo.modules.lesson.dto.result;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LessonProgressDTO {
    private String lessonId;
    private String title;
    private String description;
    private int questionCount; // Total questions in the lesson
    private int completedQuestions; // How many the current user has completed
}
