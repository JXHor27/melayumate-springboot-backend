package com.example.demo.flashcard.controller;

import com.example.demo.flashcard.service.FlashcardService;
import com.example.demo.flashcard.entity.Flashcard;
import com.example.demo.flashcard.dto.FlashcardCreateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards")
public class FlashcardController {

    @Autowired
    private final FlashcardService flashcardService;

    /**
     * Get all flashcards by flashcard list ID.
     *
     * @param flashcardListId the ID of the flashcard list
     * @return List of Flashcards
     */
    @GetMapping("/{flashcardListId}")
    public List<Flashcard> getCardsByListId(@PathVariable String flashcardListId ) {
        return flashcardService.getFlashcardsByListId(flashcardListId);
    }

    /**
     * Create a new flashcard
     *
     * @param flashcardCreateDTO the DTO containing flashcard details
     * @return A ResponseEntity containing the created Flashcard object and a 201 Created status
     */
    @PostMapping("/card")
    public ResponseEntity<Flashcard> createFlashcard(@Valid @RequestBody FlashcardCreateDTO flashcardCreateDTO) {
        Flashcard createdFlashcard = flashcardService.addFlashcard(flashcardCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFlashcard);
    }

    /**
     * Edit an existing flashcard.
     *
     * @param flashcardId the ID of the flashcard to edit
     * @param flashcardCreateDTO the DTO containing updated flashcard details
     * @return A ResponseEntity containing the updated Flashcard object and a 200 OK status
     */
    @PatchMapping("/card/{flashcardId}")
    public ResponseEntity<Flashcard> editFlashcard(@PathVariable String flashcardId, @Valid @RequestBody FlashcardCreateDTO flashcardCreateDTO) {
        Flashcard updatedCard = flashcardService.editFlashcard(flashcardId, flashcardCreateDTO);
        return ResponseEntity.ok(updatedCard);
    }

    /**
     * Delete a flashcard by ID.
     *
     * @param flashcardId the ID of the flashcard to delete
     * @return A ResponseEntity with a 204 No Content status indicating successful deletion
     */
    @DeleteMapping("/card/{flashcardId}")
    public ResponseEntity<Flashcard> deleteFlashcard(@PathVariable String flashcardId) {
        flashcardService.deleteFlashcard(flashcardId);
        // 204 No Content for successful DELETE
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
