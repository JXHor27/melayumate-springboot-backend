package com.example.demo.service;

import com.github.f4b6a3.ulid.UlidCreator;
import org.springframework.stereotype.Service;

/**
 * Service for generating unique IDs for various entities.
 * Chronologically sorted ULID (Universally Unique Lexicographically Sortable Identifier)
 * reduces index fragmentation in databases when used as primary key, unlike UUID.
 */
@Service
public class IdGeneratorService {

    public String generateUserId() {
        return "usr_" + UlidCreator.getUlid().toString();
    }

    public String generateCardListId() {
        return "crl_" + UlidCreator.getUlid().toString();
    }

    public String generateCardId() {
        return "crd_" + UlidCreator.getUlid().toString();
    }

    public String generateScenarioId() {
        return "scr_" + UlidCreator.getUlid().toString();
    }

    public String generateDialogueId() {
        return "dia_" + UlidCreator.getUlid().toString();
    }

    public String generateAudioFileId() {
        return "aud_" + UlidCreator.getUlid().toString();
    }

    public String generateResetTokenId() {
        return "tkn_" + UlidCreator.getUlid().toString();
    }

    public String generatePracticeId() {
        return "prc_" + UlidCreator.getUlid().toString();
    }

    public String generateNotificationId() {
        return "ntf_" + UlidCreator.getUlid().toString();
    }

    public String generateChatMessageId() {
        return "cht_" + UlidCreator.getUlid().toString ();
    }

    public String generateCharacterId() {
        return "chr_" + UlidCreator.getUlid().toString ();
    }

    public String generateBattleId() {
        return "bat_" + UlidCreator.getUlid().toString ();
    }

    public String generateLessonId() {
        return "les_" + UlidCreator.getUlid().toString ();
    }

    public String generateQuestionId() {
        return "que_" + UlidCreator.getUlid().toString ();
    }

    public String generateAnswerId() {
        return "ans_" + UlidCreator.getUlid().toString ();
    }

}
