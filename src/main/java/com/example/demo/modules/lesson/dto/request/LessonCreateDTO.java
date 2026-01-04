package com.example.demo.modules.lesson.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LessonCreateDTO {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private String description;
}
