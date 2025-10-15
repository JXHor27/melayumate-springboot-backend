package com.example.demo.flashcard.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.flashcard.entity.FlashcardList;
import com.example.demo.flashcard.repo.FlashcardMapper;
import com.example.demo.flashcard.entity.Flashcard;
import com.example.demo.flashcard.dto.FlashcardCreateDTO;
import com.example.demo.id.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing flashcards.
 */
@RequiredArgsConstructor
@Service
public class FlashcardService {

    private static final Log logger = LogFactory.getLog(FlashcardService.class);

    @Autowired
    private final FlashcardMapper cardMapper;

    @Autowired
    private final FlashcardListService listService;

    @Autowired
    private final IdGenerator idGenerator;

    private Flashcard getFlashcardById(String flashcardId) {
        logger.info("Fetching flashcard with id: " + flashcardId);
        Flashcard flashcard = cardMapper.getFlashcardById(flashcardId);
        if (flashcard == null) {
            logger.error("Flashcard not found with id: " + flashcardId);
            throw new ResourceNotFoundException("Flashcard not found with id: " + flashcardId);
        }
        logger.info("Fetched flashcard: " + flashcard);
        return flashcard;
    }

    public List<Flashcard> getFlashcardsByListId(String listId){
        logger.info("Fetching flashcards with id: " + listId);

        // If the list does not exist, throw an exception
        FlashcardList flashcardList = listService.getFlashcardListById(listId);

        List<Flashcard> flashcards = cardMapper.getFlashcardsByListId(listId);

        logger.info("Fetched flashcards: " + flashcards);
        return flashcards;
    }

    @Transactional
    public Flashcard addFlashcard(FlashcardCreateDTO dto) {
        logger.info("Creating flashcard: " + dto);
        String listId = dto.getFlashcardListId();

        // If the list does not exist, throw an exception
        FlashcardList flashcardList = listService.getFlashcardListById(listId);

        Flashcard newFlashcard = new Flashcard();
        newFlashcard.setFlashcardId(idGenerator.generateCardId());
        newFlashcard.setFlashcardListId(dto.getFlashcardListId());
        newFlashcard.setEnglishWord(dto.getEnglishWord());
        newFlashcard.setMalayWord(dto.getMalayWord());

        cardMapper.insertFlashcard(newFlashcard);
        listService.updateFlashcardNumber(listId, 1);

        logger.info("Created flashcard: " + newFlashcard);
        return newFlashcard;
    }

    @Transactional
    public void deleteFlashcard(String flashcardId) {
        logger.info("Deleting flashcard with id: " + flashcardId);

        Flashcard flashcard = getFlashcardById(flashcardId);
        cardMapper.deleteFlashcardById(flashcardId);
        listService.updateFlashcardNumber(flashcard.getFlashcardListId(), -1);

        logger.info("Deleted flashcard with id: " + flashcardId);
    }

    public Flashcard editFlashcard(String flashcardId, FlashcardCreateDTO dto){
        logger.info("Editing flashcard with id: " + flashcardId);

        Flashcard existingCard = getFlashcardById(flashcardId);
        existingCard.setEnglishWord(dto.getEnglishWord());
        existingCard.setMalayWord(dto.getMalayWord());
        cardMapper.editFlashcard(existingCard);

        logger.info("Edited flashcard: " + existingCard);
        return existingCard;
    }




}
