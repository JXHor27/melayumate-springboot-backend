package com.example.demo.modules.game.controller;

import com.example.demo.modules.game.dto.character.OwnedCharacterDTO;
import com.example.demo.modules.game.entity.CharacterTemplate;
import com.example.demo.modules.game.service.CharacterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/character")
public class CharacterController {

    @Autowired
    private final CharacterService characterService;

    /**
     * Retrieves character templates by unlock level.
     *
     * @param unlockLevel The unlock level to filter character templates
     * @return A ResponseEntity containing a list of CharacterTemplate objects and a 200 OK status
     */
    @GetMapping("/unlock/{unlockLevel}")
    public ResponseEntity<List<CharacterTemplate>> findAllCharacterTemplates(@PathVariable int unlockLevel) {
        List<CharacterTemplate> characterTemplates = characterService.findAllCharacterTemplates();
        return ResponseEntity.ok(characterTemplates);
    }


    /**
     * Retrieves all owned characters for a specific user.
     *
     * @param userId The ID of the user whose owned characters are to be retrieved
     * @return A ResponseEntity containing a list of OwnedCharacterDTO objects and a 200 OK status
     */
    @GetMapping("/{userId}/owned")
        public ResponseEntity<List<OwnedCharacterDTO>> getOwnedCharacters(@PathVariable String userId) {
        List<OwnedCharacterDTO> ownedCharacters = characterService.findOwnedCharacters(userId);
        return ResponseEntity.ok(ownedCharacters);
    }

    /**
     * Retrieves the primary battle character for a specific user.
     *
     * @param userId The ID of the user whose primary character is to be retrieved
     * @return A ResponseEntity containing an OwnedCharacterDTO object and a 200 OK status
     */
    @GetMapping("/{userId}/primary")
    public ResponseEntity<OwnedCharacterDTO> getPrimaryCharacter(@PathVariable String userId) {
        OwnedCharacterDTO primaryCharacter = characterService.findPrimaryCharacter(userId);
        return ResponseEntity.ok(primaryCharacter);
    }

    // --- Endpoint for Choosing/Unlocking a Character ---
    // Patch mapping because just update UserStats primary and secondary fields.
    // If primary is not null, means unlocking secondary character.
    /**
     * Acquires a character for a specific user based on the provided template ID.
     *
     * @param userId  The ID of the user acquiring the character
     * @param payload A map containing the templateId of the character to be acquired
     * @return A ResponseEntity with HTTP status OK if successful, or BAD_REQUEST if an error occurs
     */
    @PostMapping("/{userId}")
    public ResponseEntity<Void> acquireCharacter(@PathVariable String userId, @RequestBody Map<String, String> payload) {
        String templateId = payload.get("templateId");
        try {
            characterService.acquireCharacter(userId, templateId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Sets the primary battle character for a specific user.
     *
     * @param userId  The ID of the user setting the primary character
     * @param payload A map containing the characterId of the character to be set as primary
     * @return A ResponseEntity with HTTP status OK if successful, or BAD_REQUEST if an error occurs
     */
    @PatchMapping("/{userId}/primary")
    public ResponseEntity<Void> setPrimaryCharacter(@PathVariable String userId, @RequestBody Map<String, String> payload) {
        String characterId = payload.get("characterId");
        try {
            characterService.setPrimaryCharacter(userId, characterId);
            return ResponseEntity.ok().build();
        } catch(Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
