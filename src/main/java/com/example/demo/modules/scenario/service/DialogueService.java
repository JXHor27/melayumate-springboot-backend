package com.example.demo.modules.scenario.service;

import com.example.demo.exception.DialogueOrderConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.file.FileStorageService;
import com.example.demo.modules.scenario.repo.ScenarioMapper;
import com.example.demo.service.IdGeneratorService;
import com.example.demo.modules.scenario.dto.DialogueCreateDTO;
import com.example.demo.modules.scenario.entity.Dialogue;
import com.example.demo.modules.scenario.repo.DialogueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing dialogues.
 */
@RequiredArgsConstructor
@Service
public class DialogueService {

    private static final Log logger = LogFactory.getLog(DialogueService.class);

    @Autowired
    private final DialogueMapper dialogueMapper;

    @Autowired
    private final ScenarioMapper scenarioMapper;

    @Autowired
    private final FileStorageService fileStorageService;

    @Autowired
    private final IdGeneratorService idGeneratorService;

    @Value("${aws.s3.base-url}")
    private String s3BaseUrl;

    public List<Dialogue> getDialogueListById(String scenarioId) {
        logger.info("Fetching dialogue list with id: " + scenarioId);
        List<Dialogue> dialogueList = dialogueMapper.getDialogueListById(scenarioId);

        for (Dialogue dialogue : dialogueList) {
            dialogue.setObjectKey(dialogue.getAudioUrl());
            if (dialogue.getAudioUrl() != null && !dialogue.getAudioUrl().isEmpty()) {
                dialogue.setAudioUrl(s3BaseUrl + "/" + dialogue.getAudioUrl());
            }
        }
        logger.info("Fetched dialogue list: " + dialogueList);
        return dialogueList;
    }

    public Dialogue getDialogueById(String dialogueId){
        logger.info("Fetching dialogue with id: " + dialogueId);
        Dialogue dialogue = dialogueMapper.getDialogueById(dialogueId);
        if (dialogue == null) {
            logger.error("Dialogue not found with id: " + dialogueId);
            throw new ResourceNotFoundException("Dialogue not found with id: " + dialogueId);
        }
        dialogue.setObjectKey(dialogue.getAudioUrl());
        if (dialogue.getAudioUrl() != null && !dialogue.getAudioUrl().isEmpty()) {
            dialogue.setAudioUrl(s3BaseUrl + "/" + dialogue.getAudioUrl());
        }
        logger.info("Fetched dialogue: " + dialogue);
        return dialogue;
    }

    /**
     * Creates a new dialogue and updates the dialogue count in the associated scenario.
     *
     * @param dto the DialogueCreateDTO containing dialogue details
     * @return the created Dialogue
     * @throws DialogueOrderConflictException if a dialogue with the same type and order already exists in the scenario
     */
    @Transactional
    public Dialogue createDialogue(DialogueCreateDTO dto) {
        try {
            logger.info("Creating dialogue: " + dto);
            String scenarioId = dto.getScenarioId();
            Dialogue newDialogue = new Dialogue();
            newDialogue.setDialogueId(idGeneratorService.generateDialogueId());
            newDialogue.setScenarioId(scenarioId);
            newDialogue.setDialogueOrder(dto.getDialogueOrder());
            newDialogue.setDialogueType(dto.getDialogueType());
            newDialogue.setMalay(dto.getMalay());
            newDialogue.setEnglish(dto.getEnglish());
            newDialogue.setAudioUrl(dto.getAudioUrl());
            dialogueMapper.addDialogue(newDialogue);

            scenarioMapper.updateDialogueNumber(scenarioId, 1);
            logger.info("Created dialogue: " + newDialogue);
            return newDialogue;
        }
        catch (DataIntegrityViolationException e) {
            // Clean up uploaded audio file if exists in case of conflict
            if (dto.getAudioUrl() != null && !dto.getAudioUrl().isEmpty()) {
                fileStorageService.deleteFile(dto.getAudioUrl());
            }
            throw new DialogueOrderConflictException(
                    "A dialogue of type '" + dto.getDialogueType() +
                            "' with order '" + dto.getDialogueOrder() + "' already exists in this scenario."
            );
        }
    }

    @Transactional
    public void deleteDialogue(String dialogueId) {
        logger.info("Deleting dialogue with id: " + dialogueId);
        Dialogue dialogue = getDialogueById(dialogueId);
        dialogueMapper.deleteDialogue(dialogueId);
        // Delete associated audio files from S3
        if (dialogue.getObjectKey() != null && !dialogue.getObjectKey().isEmpty()) {
            fileStorageService.deleteFile(dialogue.getObjectKey());
        }
        scenarioMapper.updateDialogueNumber(dialogue.getScenarioId(), -1);
        logger.info("Deleted dialogue with id: " + dialogueId);
    }
}
