package com.example.demo.modules.lesson.controller;

import com.example.demo.modules.lesson.dto.result.LessonProgressDTO;
import com.example.demo.modules.lesson.entity.Lesson;
import com.example.demo.modules.lesson.dto.request.LessonCreateDTO;
import com.example.demo.modules.lesson.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lessons")
public class LessonController {

    @Autowired
    private final LessonService lessonService;


    @PostMapping("")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Lesson> createLesson(@Valid @RequestBody LessonCreateDTO dto) {
        Lesson createdLesson  = lessonService.createLesson(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLesson);
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<Lesson>> getAllLessons() {
        List<Lesson> lessons = lessonService.getAllLessons();
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/available/{userId}")
    public ResponseEntity<List<LessonProgressDTO>> getAvailableLessons(@PathVariable String userId) {
        List<LessonProgressDTO> lessons = lessonService.getAvailableLessonsWithProgress(userId);
        return ResponseEntity.ok(lessons);
    }


    @GetMapping("/{lessonId}")
    public ResponseEntity<?> getLessonById(@PathVariable String id) {
        // ... logic to fetch a lesson ...
        return null;
    }

    @DeleteMapping("/{lessonId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> deleteLesson(@PathVariable String lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{lessonId}/available")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<Lesson> updateLessonAvailability(@PathVariable String lessonId, @RequestBody Map<String, Boolean> payload) {
        boolean isAvailable = payload.get("isAvailable");
        lessonService.updateLessonAvailability(lessonId, isAvailable);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}