package com.example.demo.flashcard.controller;

import com.example.demo.flashcard.entity.FlashcardList;
import com.example.demo.flashcard.dto.FlashcardListCreateDTO;
import com.example.demo.flashcard.service.FlashcardListService;
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
@RequestMapping("/api/v1/lists")
public class FlashcardListController {

    @Autowired
    private final FlashcardListService listService;

    /**
     * Retrieves all flashcard lists for a specific user.
     *
     * @param userId The ID of the user whose flashcard lists are to be retrieved
     * @return A ResponseEntity containing a list of FlashcardList objects and a 200 OK status
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<FlashcardList>> getListsByUserId(@PathVariable String userId) {
        List<FlashcardList> flashcardLists =  listService.getFlashcardListsByUserId(userId);
        return ResponseEntity.ok(flashcardLists);
    }

    /**
     * Retrieves a specific flashcard list by its ID.
     *
     * @param flashcardListId The ID of the flashcard list to retrieve
     * @return A ResponseEntity containing the FlashcardList object and a 200 OK status
     */
    @GetMapping("/list/{flashcardListId}")
    public ResponseEntity<FlashcardList> getListById(@PathVariable String flashcardListId) {
        FlashcardList flashcardList = listService.getFlashcardListById(flashcardListId);
        return ResponseEntity.ok(flashcardList);
    }

    /**
     * Creates a new flashcard list.
     *
     * @param flashcardListCreateDTO The DTO containing the userId, title, and description from the request body
     * @return A ResponseEntity containing the created FlashcardList object and a 201 Created status
     */
    @PostMapping("/list")
    public ResponseEntity<FlashcardList> createList(@Valid @RequestBody FlashcardListCreateDTO flashcardListCreateDTO){
        FlashcardList createdList = listService.createFlashcardList(flashcardListCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdList);
    }

    /**
     * Updates an existing flashcard list.
     *
     * @param flashcardListId  The ID of the list to update
     * @param flashcardListCreateDTO The DTO containing the new title and description from the request body
     * @return A ResponseEntity containing the updated FlashcardList object and a 200 OK status
     */
    @PatchMapping("/list/{flashcardListId}")
    public ResponseEntity<FlashcardList> editList(@PathVariable String flashcardListId, @Valid @RequestBody FlashcardListCreateDTO flashcardListCreateDTO) {
        FlashcardList updatedList = listService.editFlashcardList(flashcardListId, flashcardListCreateDTO);
        return ResponseEntity.ok(updatedList);
    }

    /**
     * Deletes a flashcard list by its ID.
     *
     * @param flashcardListId The ID of the flashcard list to delete
     * @return A ResponseEntity with a 204 No Content status indicating successful deletion
     */
    @DeleteMapping("/list/{flashcardListId}")
    public ResponseEntity<FlashcardList> deleteFlashcardList(@PathVariable String flashcardListId) {
        listService.deleteFlashcardList(flashcardListId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
