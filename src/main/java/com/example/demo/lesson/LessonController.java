package com.example.demo.lesson;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lesson")
public class LessonController {

    // AN ENDPOINT ONLY A LECTURER CAN ACCESS
    @PostMapping
    @PreAuthorize("hasRole('LECTURER')") // or hasRole('LECTURER') if using 'ROLE_' prefix
    public ResponseEntity<?> createLesson() {
        // ... logic to create a lesson ...
        // If a normal user tries to access this, they will get a 403 Forbidden error.
        return null;
    }

    // AN ENDPOINT ANY AUTHENTICATED USER CAN ACCESS
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()") // Both USER and LECTURER can access this
    public ResponseEntity<?> getLessonById(@PathVariable Long id) {
        // ... logic to fetch a lesson ...
        return null;
    }
}