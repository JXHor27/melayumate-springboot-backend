package com.example.demo.modules.game.controller;

import com.example.demo.modules.game.dto.battle.BattleChallengeDTO;
import com.example.demo.modules.game.dto.battle.BattleResultDTO;
import com.example.demo.modules.game.entity.Battle;
import com.example.demo.modules.game.service.BattleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/battle")
public class BattleController {

    @Autowired
    private final BattleService battleService;

    /**
     * Initiates a battle challenge between characters.
     *
     * @param battleChallengeDTO The DTO containing battle challenge details.
     * @return A ResponseEntity containing the Battle result.
     */
    @PostMapping("/challenge")
    public ResponseEntity<Battle> challengeCharacter(@Valid @RequestBody BattleChallengeDTO battleChallengeDTO) {
        try {
            Battle battle = battleService.createAndSimulateBattle(battleChallengeDTO);
            return ResponseEntity.ok(battle);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves the result of a battle by its ID.
     *
     * @param battleId The ID of the battle.
     * @return A ResponseEntity containing the BattleResultDTO.
     */
    @GetMapping("/result/{battleId}")
    public ResponseEntity<BattleResultDTO> getBattleResult(@PathVariable String battleId) {
        try {
            BattleResultDTO result = battleService.getBattleResult(battleId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
