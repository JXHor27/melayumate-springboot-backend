package com.example.demo.dashboard.controller;

import com.example.demo.dashboard.dto.GoalCreateDTO;
import com.example.demo.dashboard.entity.UserStats;
import com.example.demo.dashboard.service.StatsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stats")
public class StatsController {

    @Autowired
    private final StatsService statsService;

    /**
     * Retrieves the user's statistics.
     *
     * @param userId The ID of the user to retrieve
     * @return a ResponseEntity containing the user stats object
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserStats> getUserStats(@PathVariable String userId) {
        UserStats userStats = statsService.getUserStats(userId);
        return ResponseEntity.ok(userStats);
    }

    /**
     * Create a new daily learning goal.
     *
     * @param goalCreateDTO the DTO containing user ID and daily goal
     * @return a ResponseEntity containing user stats with newly created daily learning goal
     */
    @PatchMapping("")
    public ResponseEntity<UserStats> createGoal(@Valid @RequestBody GoalCreateDTO goalCreateDTO) {
        UserStats userStats = statsService.updateGoal(goalCreateDTO);
        return ResponseEntity.ok(userStats);
    }
}
