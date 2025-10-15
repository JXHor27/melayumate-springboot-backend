package com.example.demo.dashboard.controller;

import com.example.demo.dashboard.dto.PracticeCreateDTO;
import com.example.demo.dashboard.entity.PracticeCount;
import com.example.demo.dashboard.entity.PracticeSession;
import com.example.demo.dashboard.service.PracticeSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/practice")
public class PracticeSessionController {

    @Autowired
    private final PracticeSessionService practiceSessionService;

    /**
     * Get daily practice session done by user ID.
     *
     * @param userId the ID of the user
     * @return a ResponseEntity containing the practice count
     */
    @GetMapping("/{userId}")
    public ResponseEntity<PracticeCount> getUserDailyStat(@PathVariable String userId) {
        PracticeCount practiceCount = practiceSessionService.retrieveDailyStat(userId);
        return ResponseEntity.ok(practiceCount); 
    }


    /**
     * Record one completed practice session, either FLASHCARD, DIALOGUE or LESSON.
     *
     * @param practiceCreateDTO the DTO containing user ID and practice type
     * @return a ResponseEntity containing the practice session
     */
    @PostMapping("")
    public ResponseEntity<PracticeSession> recordPractice(@Valid @RequestBody PracticeCreateDTO practiceCreateDTO) {
        PracticeSession practiceSession = practiceSessionService.recordPractice(practiceCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(practiceSession);    }
}
