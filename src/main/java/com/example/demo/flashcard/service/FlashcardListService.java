package com.example.demo.flashcard.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.flashcard.repo.FlashcardListMapper;
import com.example.demo.flashcard.entity.FlashcardList;
import com.example.demo.flashcard.dto.FlashcardListCreateDTO;
import com.example.demo.flashcard.repo.FlashcardMapper;
import com.example.demo.id.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Service class for managing flashcard lists.
 */
@RequiredArgsConstructor
@Service
public class FlashcardListService {

    private static final Log logger = LogFactory.getLog(FlashcardListService.class);

    @Autowired
    private final FlashcardListMapper listMapper;

    @Autowired
    private final IdGenerator idGenerator;

    public List<FlashcardList> getFlashcardListsByUserId(String userId) {
        logger.info("Fetching flashcard lists for userId: " + userId);
        List<FlashcardList> lists = listMapper.getFlashcardListsByUserId(userId);
        if (lists == null) {
            logger.error("Flashcard lists not found with userId: " + userId);
            throw new ResourceNotFoundException("Flashcard lists not found with userId: " + userId);
        }
        logger.info("Fetched flashcard lists: " + lists);
        return lists;
    }

    public FlashcardList getFlashcardListById(String listId) {
        logger.info("Fetching flashcard list with id: " + listId);
        FlashcardList list = listMapper.getFlashcardListByListId(listId);
        if (list == null) {
            logger.error("FlashcardList not found with id: " + listId);
            throw new ResourceNotFoundException("FlashcardList not found with id: " + listId);
        }
        logger.info("Fetched flashcard list: " + list);
        return list;
    }

    public FlashcardList createFlashcardList(FlashcardListCreateDTO dto) {
        logger.info("Creating flashcard list: " + dto);

        FlashcardList newList = new FlashcardList();
        newList.setFlashcardListId(idGenerator.generateCardListId());
        newList.setUserId(dto.getUserId());
        newList.setTitle(dto.getTitle());
        newList.setDescription(dto.getDescription());
        newList.setFlashcardNumber(0); // a new list always starts with 0 cards

        listMapper.addFlashcardList(newList);
        logger.info("Created flashcard list: " + newList);
        return newList;
    }

    public FlashcardList editFlashcardList(String listId, FlashcardListCreateDTO dto){
        logger.info("Editing flashcard list with id: " + listId);

        FlashcardList existingList = getFlashcardListById(listId);

        // Objects.equals() to handle potential nulls for description
        boolean titleIsSame = existingList.getTitle().equals(dto.getTitle());
        boolean descriptionIsSame = Objects.equals(existingList.getDescription(), dto.getDescription());

        if (titleIsSame && descriptionIsSame) {
            logger.info("No changes detected for flashcard list with id: " + listId);
            return existingList; // Success, but no database action taken.
        }

        existingList.setTitle(dto.getTitle());
        existingList.setDescription(dto.getDescription());

        listMapper.editFlashcardList(existingList);
        logger.info("Edited flashcard list: " + existingList);
        return existingList;
    }

    public void deleteFlashcardList(String listId) {
        logger.info("Deleting flashcard list with id: " + listId);
        // Ensure list exists
        FlashcardList existingList = getFlashcardListById(listId);

        // Delete whole list
        listMapper.deleteFlashcardListById(listId);
        logger.info("Deleted flashcard list with id: " + listId);
    }

    public void updateFlashcardNumber(String listId, int number){
        listMapper.updateFlashcardNumber(listId, number);
    }
}
