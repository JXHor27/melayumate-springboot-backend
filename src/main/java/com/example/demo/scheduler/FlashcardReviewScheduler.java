package com.example.demo.scheduler;

import com.example.demo.enums.NotificationType;
import com.example.demo.modules.dashboard.dto.NotificationCreateDTO;
import com.example.demo.modules.dashboard.service.NotificationService;
import com.example.demo.modules.flashcard.entity.FlashcardList;
import com.example.demo.modules.flashcard.service.FlashcardListService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class FlashcardReviewScheduler {
    private static final Log logger = LogFactory.getLog(FlashcardReviewScheduler.class);

    @Autowired
    private final FlashcardListService flashcardListService;

    @Autowired
    private final NotificationService notificationService;

    /**
     * Scheduled job that runs daily at 12:00:00 AM to check for flashcard decks
     * that have a review date set for the current day. Sends notifications
     * to users to remind them to review their decks.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkDeckReviewDate() {
        logger.info("Starting daily deck review reminder...");

        List<FlashcardList> flashcardLists = flashcardListService.getFlashcardListsWithTodayReviewDate();

        for (FlashcardList flashcardList : flashcardLists) {
            String title = flashcardList.getTitle();
            String userId = flashcardList.getUserId();
            NotificationCreateDTO notification = new NotificationCreateDTO();
            notification.setUserId(userId);
            notification.setNotificationType(NotificationType.REMINDER);
            notification.setTitle("Flashcard Reminder");
            notification.setMessage("Review your deck \"" + title + "\" today!");
            notificationService.createNotification(notification);
            logger.info("Sent deck reminder to user: " + userId);
        }
    }

}
