package com.example.demo.modules.flashcard.service;

import com.example.demo.enums.CardDefaultLanguage;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.modules.flashcard.repo.FlashcardListMapper;
import com.example.demo.modules.flashcard.entity.FlashcardList;
import com.example.demo.modules.flashcard.dto.FlashcardListCreateDTO;
import com.example.demo.service.IdGeneratorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private final IdGeneratorService idGeneratorService;

    public List<FlashcardList> getFlashcardListsByUserId(String userId) {
        logger.info("Fetching flashcard lists for userId: " + userId);
        List<FlashcardList> lists = listMapper.getFlashcardListsByUserId(userId);
        if (lists.isEmpty()) {
            logger.info("Flashcard lists not found with userId: " + userId);
            return lists;
        }
        logger.info("Fetched flashcard lists: " + lists);
        return lists;
    }

    public FlashcardList getFlashcardListById(String flashcardListId) {
        logger.info("Fetching flashcard list with id: " + flashcardListId);
        FlashcardList list = listMapper.getFlashcardListByListId(flashcardListId);
        if (list == null) {
            logger.error("FlashcardList not found with id: " + flashcardListId);
            throw new ResourceNotFoundException("FlashcardList not found with id: " + flashcardListId);
        }
        logger.info("Fetched flashcard list: " + list);
        return list;
    }

    public FlashcardList createFlashcardList(FlashcardListCreateDTO dto) {
        logger.info("Creating flashcard list: " + dto);
        FlashcardList newList = new FlashcardList();
        newList.setFlashcardListId(idGeneratorService.generateCardListId());
        newList.setUserId(dto.getUserId());
        newList.setTitle(dto.getTitle());
        newList.setDescription(dto.getDescription());
        // a new list always starts with 0 cards
        newList.setFlashcardNumber(0);
        newList.setRandom(false);
        newList.setDefaultLanguage(CardDefaultLanguage.ENGLISH);
        newList.setNextReviewDate(LocalDate.now().plusDays(getDaysForStreak(0)));
        newList.setCurrentStreak(0);
        listMapper.addFlashcardList(newList);
        logger.info("Created flashcard list: " + newList);
        return newList;
    }

    public FlashcardList editFlashcardList(String flashcardListId, FlashcardListCreateDTO dto){
        logger.info("Editing flashcard list with id: " + flashcardListId);
        FlashcardList existingList = listMapper.getFlashcardListByListId(flashcardListId);
        if (existingList == null) {
            logger.error("FlashcardList not found with id: " + flashcardListId);
            throw new ResourceNotFoundException("FlashcardList not found with id: " + flashcardListId);
        }
        boolean titleIsSame = existingList.getTitle().equals(dto.getTitle());
        // Objects.equals() to handle potential nulls for description
        boolean descriptionIsSame = Objects.equals(existingList.getDescription(), dto.getDescription());
        if (titleIsSame && descriptionIsSame) {
            logger.info("No changes detected for flashcard list with id: " + flashcardListId);
            return existingList;
        }
        existingList.setTitle(dto.getTitle());
        existingList.setDescription(dto.getDescription());
        listMapper.editFlashcardList(existingList);
        logger.info("Edited flashcard list: " + existingList);
        return existingList;
    }

    public void deleteFlashcardList(String flashcardListId) {
        logger.info("Deleting flashcard list with id: " + flashcardListId);
        listMapper.deleteFlashcardListById(flashcardListId);
        logger.info("Deleted flashcard list with id: " + flashcardListId);
    }

    public void updateOrder(String flashcardListId, boolean isRandom) {
        logger.info("Updating cards order for list: " + flashcardListId);
        listMapper.updateOrder(flashcardListId, isRandom);
        logger.info("Updated cards order for list: " + flashcardListId);
    }

    public void updateLanguage(String flashcardListId, String defaultLanguage) {
        logger.info("Updating default language for list: " + flashcardListId);
        CardDefaultLanguage cardDefaultLanguage = CardDefaultLanguage.valueOf(defaultLanguage);
        listMapper.updateDefaultLanguage(flashcardListId, cardDefaultLanguage.name());
        logger.info("Updated default language for list: " + flashcardListId);
    }

    public List<FlashcardList> getFlashcardListsWithTodayReviewDate() {
        logger.info("Fetching all flashcard lists with today's review date");
        List<FlashcardList> allLists = listMapper.getAllFlashcardLists();
        if (allLists.isEmpty()) {
            logger.info("No flashcard lists found");
            return allLists;
        }
        for (int i = allLists.size() - 1; i >= 0; i--) {
            FlashcardList list = allLists.get(i);
            if (list.getNextReviewDate().isAfter(LocalDate.now())) {
                allLists.remove(i);
            }
        }
        logger.info("Fetched flashcard lists with today's review date: " + allLists);
        return allLists;
    }

    /**
     * Schedule the next review date for a flashcard list based on user's performance.
     * This method is executed asynchronously to avoid blocking the main thread.
     *
     * @param flashcardListId The ID of the flashcard list.
     * @param correctCount    Number of correct answers in the flashcard practice session.
     * @param totalCount      Total number of cards in the deck.
     */
    @Async
    public void scheduleNextReviewDate(String flashcardListId, int correctCount, int totalCount) {
        logger.info("Scheduling next review date for flashcard list id: " + flashcardListId +
                " with correctCount: " + correctCount + " and totalCount: " + totalCount);
        FlashcardList flashcardList = listMapper.getFlashcardListByListId(flashcardListId);
        if (flashcardList == null) {
            logger.error("FlashcardList not found with id: " + flashcardListId);
            throw new ResourceNotFoundException("FlashcardList not found with id: " + flashcardListId);
        }
        int nextStreak = getNextStreak(correctCount, totalCount, flashcardList);

        // Determine the next review date based on the new streak level
        // We use a predefined streak schedule (in days).
        int daysToAdd = getDaysForStreak(nextStreak);
        LocalDate nextReviewDate = LocalDate.now().plusDays(daysToAdd);

        flashcardList.setCurrentStreak(nextStreak);
        flashcardList.setNextReviewDate(nextReviewDate);

        listMapper.updateFlashcardListReviewDate(flashcardList);

        logger.info("Scheduled next review date for flashcard list id: " + flashcardListId);
    }

    /**
     * Determine the next streak level based on user's performance.
     *
     * @param correctCount   Number of correct answers.
     * @param totalCount     Total number of questions answered.
     * @param flashcardList  The FlashcardList being reviewed.
     * @return The next streak level.
     */
    private int getNextStreak(double correctCount, int totalCount, FlashcardList flashcardList) {
        int nextStreak;
        double successRatio = correctCount / totalCount;

        if (successRatio >= 0.75) {
            // High Performance (75% or more correct)
            // The user knows the material well. Increase the streak.
            nextStreak = flashcardList.getCurrentStreak() + 1;

        } else if (successRatio < 0.40) {
            // Low Performance (less than 40% correct)
            // The user is struggling. Reset the streak.
            nextStreak = 0;

        } else {
            // Medium Performance (between 40% and 75%)
            // The user is so-so. Keep the streak as it is.
            nextStreak = flashcardList.getCurrentStreak();
        }
        return nextStreak;
    }


    /**
     * Helper method that defines spaced repetition schedule.
     * This makes the schedule easy to change and understand.
     */
    private int getDaysForStreak(int streak) {
        return switch (streak) {
            case 0 -> 1;    // Review tomorrow
            case 1 -> 3;    // Review in 3 days
            case 2 -> 7;    // Review in 1 week
            case 3 -> 14;   // Review in 2 weeks
            case 4 -> 30;   // Review in 1 month
            case 5 -> 60;   // Review in 2 months
            default -> 90;  // Cap at 3 months
        };
    }
}
