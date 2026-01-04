package com.example.demo.modules.game.controller;

import com.example.demo.modules.game.dto.character.ListedCharacterDTO;
import com.example.demo.modules.game.service.BattleLobbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lobby")
public class BattleLobbyController {

    @Autowired
    private final BattleLobbyService lobbyService;

    /**
     * Retrieve listed challengers available for matchmaking, excluding the requesting user's own character.
     *
     * @param userId the ID of the user, used to exclude their own listed character from the results
     * @return a list of lightweight {@link ListedCharacterDTO} representing other users' characters
     * @see BattleLobbyService#getListedChallengers(String)
     */
    @GetMapping("/{userId}/challengers")
    public ResponseEntity<List<ListedCharacterDTO>> getChallengers(@PathVariable String userId) {
        List<ListedCharacterDTO> availableChallengers = lobbyService.getListedChallengers(userId);
        return ResponseEntity.ok(availableChallengers);
    }

    /**
     * List the primary character of the user for battle.
     *
     * @param userId the ID of the user
     * @param payload a map containing the characterId to be listed
     * @return a ResponseEntity with HTTP status OK if successful, BAD_REQUEST otherwise
     */
    @PatchMapping("/{userId}/list")
    public ResponseEntity<Void> listCharacter(@PathVariable String userId, @RequestBody Map<String, String> payload) {
        String characterId = payload.get("characterId");
        try {
            lobbyService.listPrimaryCharacterForBattle(userId, characterId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Unlist the primary character of the user from battle.
     *
     * @param userId the ID of the user
     * @param payload a map containing the characterId to be unlisted
     * @return a ResponseEntity with HTTP status OK if successful, BAD_REQUEST otherwise
     */
    @PatchMapping("/{userId}/unlist")
    public ResponseEntity<Void> unlistCharacter(@PathVariable String userId, @RequestBody Map<String, String> payload) {
        String characterId = payload.get("characterId");

        try {
            lobbyService.unlistPrimaryCharacter(userId, characterId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}