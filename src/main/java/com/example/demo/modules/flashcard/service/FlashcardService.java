package com.example.demo.modules.flashcard.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.modules.flashcard.dto.FlashcardListDetailResultDTO;
import com.example.demo.modules.flashcard.entity.FlashcardList;
import com.example.demo.modules.flashcard.repo.FlashcardListMapper;
import com.example.demo.modules.flashcard.repo.FlashcardMapper;
import com.example.demo.modules.flashcard.entity.Flashcard;
import com.example.demo.modules.flashcard.dto.FlashcardCreateDTO;
import com.example.demo.service.IdGeneratorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    private final FlashcardListMapper listMapper;

    @Autowired
    private final IdGeneratorService idGeneratorService;

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
        List<Flashcard> flashcards = cardMapper.getFlashcardsByListId(listId);
        if (flashcards.isEmpty()){
            logger.info("No flashcard found with list id: " + listId);
            return flashcards;
        }
        logger.info("Fetched flashcards: " + flashcards);
        return flashcards;
    }

    @Transactional
    public Flashcard addFlashcard(FlashcardCreateDTO dto) {
        logger.info("Creating flashcard: " + dto);
        String listId = dto.getFlashcardListId();
        Flashcard newFlashcard = new Flashcard();
        newFlashcard.setFlashcardId(idGeneratorService.generateCardId());
        newFlashcard.setFlashcardListId(dto.getFlashcardListId());
        newFlashcard.setEnglishWord(dto.getEnglishWord());
        newFlashcard.setMalayWord(dto.getMalayWord());
        cardMapper.insertFlashcard(newFlashcard);
        listMapper.updateFlashcardNumber(listId, 1);
        logger.info("Created flashcard: " + newFlashcard);
        return newFlashcard;
    }

    @Transactional
    public void deleteFlashcard(String flashcardId) {
        logger.info("Deleting flashcard with id: " + flashcardId);

        Flashcard flashcard = getFlashcardById(flashcardId);
        cardMapper.deleteFlashcardById(flashcardId);
        listMapper.updateFlashcardNumber(flashcard.getFlashcardListId(), -1);

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

    public FlashcardListDetailResultDTO getFlashcardListForPractice(String flashcardListId) {
        logger.info("Fetching list and card detail in one shot: " + flashcardListId);
        FlashcardListDetailResultDTO resultDTO = new FlashcardListDetailResultDTO();
        FlashcardList existingList = listMapper.getFlashcardListByListId(flashcardListId);
        if (existingList == null) {
            logger.error("FlashcardList not found with id: " + flashcardListId);
            throw new ResourceNotFoundException("FlashcardList not found with id: " + flashcardListId);
        }
        List<Flashcard> flashcards = cardMapper.getFlashcardsByListId(flashcardListId);
        if (flashcards.isEmpty()) {
            logger.info("No flashcard found with list id: " + flashcardListId);
        }
        // logic for either shuffle cards order, else in the order they are inserted
        if (existingList.isRandom()) {
            Collections.shuffle(flashcards);
        }
        resultDTO.setUserId(existingList.getUserId());
        resultDTO.setFlashcardListId(existingList.getFlashcardListId());
        resultDTO.setTitle(existingList.getTitle());
        resultDTO.setDescription(existingList.getDescription());
        resultDTO.setFlashcardNumber(existingList.getFlashcardNumber());
        resultDTO.setRandom(existingList.isRandom());
        resultDTO.setDefaultLanguage(existingList.getDefaultLanguage());
        resultDTO.setCards(flashcards);
        return resultDTO;
    }
}
