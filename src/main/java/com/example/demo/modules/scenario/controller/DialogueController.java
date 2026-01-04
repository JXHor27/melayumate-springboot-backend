package com.example.demo.modules.scenario.controller;

import com.example.demo.modules.scenario.dto.DialogueCreateDTO;
import com.example.demo.modules.scenario.entity.Dialogue;
import com.example.demo.modules.scenario.service.DialogueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dialogue")
public class DialogueController {

    @Autowired
    private final DialogueService dialogueService;

    /**
     * Get a list of dialogues by scenario ID.
     *
     * @param scenarioId the ID of the scenario
     * @return a ResponseEntity containing the list of dialogues
     */
    @GetMapping("/{scenarioId}")
    public ResponseEntity<List<Dialogue>> getDialogueListById(@PathVariable String scenarioId) {
        List<Dialogue> dialogueList = dialogueService.getDialogueListById(scenarioId);
        return ResponseEntity.ok(dialogueList);
    }

    /**
     * Create a new dialogue.
     *
     * @param dialogueCreateDTO the DTO containing dialogue creation data
     * @return a ResponseEntity containing the created dialogue
     */
    @PostMapping("")
    public ResponseEntity<Dialogue> createDialogue(@Valid @RequestBody DialogueCreateDTO dialogueCreateDTO) {
        Dialogue createdDialogue = dialogueService.createDialogue(dialogueCreateDTO);
        return ResponseEntity.ok(createdDialogue);
    }

    /**
     * Delete a dialogue by its ID.
     *
     * @param dialogueId the ID of the dialogue to delete
     * @return a ResponseEntity with HTTP status NO_CONTENT
     */
    @DeleteMapping("/{dialogueId}")
    public ResponseEntity<Dialogue> deleteDialogue(@PathVariable String dialogueId) {
        dialogueService.deleteDialogue(dialogueId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
