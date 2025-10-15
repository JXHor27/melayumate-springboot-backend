package com.example.demo.game.controller;

import com.example.demo.game.dto.BattleResultDTO;
import com.example.demo.game.entity.Battle;
import com.example.demo.game.service.BattleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battle")
public class BattleController {

    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping("/challenge/{defenderId}")
    public ResponseEntity<String> challengeCharacter(@PathVariable String defenderId) {
        // Assume you can get the current user's character ID from the security context
        String challengerId = ""; // Placeholder for logged-in user's character ID
        try {
            Battle battle = battleService.createAndSimulateBattle(challengerId, defenderId);
            return ResponseEntity.ok(battle.getBattleId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    // THIS IS THE ENDPOINT YOU ASKED FOR
    @GetMapping("/result/{battleId}")
    public ResponseEntity<BattleResultDTO> getBattleResult(@PathVariable Long battleId) {
        // Assume you get the current user ID from security context
        Long currentUserId = 1L; // Placeholder for logged-in user ID
        try {
            BattleResultDTO result = battleService.getBattleResult(battleId, currentUserId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Add other endpoints for listing characters, etc.
}
