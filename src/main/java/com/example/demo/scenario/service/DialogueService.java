package com.example.demo.scenario.service;

import com.example.demo.exception.DialogueOrderConflictException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.id.IdGenerator;
import com.example.demo.scenario.dto.DialogueCreateDTO;
import com.example.demo.scenario.entity.Dialogue;
import com.example.demo.scenario.entity.Scenario;
import com.example.demo.scenario.repo.DialogueMapper;
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
    private final ScenarioService scenarioService;

    @Autowired
    private final IdGenerator idGenerator;

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

    public Dialogue createDialogue(DialogueCreateDTO dto) {
        try {
            logger.info("Creating dialogue: " + dto);
            String scenarioId = dto.getScenarioId();

            // If the scenario does not exist, throw an exception
            Scenario scenario = scenarioService.getScenarioById(scenarioId);

            Dialogue newDialogue = new Dialogue();
            newDialogue.setDialogueId(idGenerator.generateDialogueId());
            newDialogue.setScenarioId(scenarioId);
            newDialogue.setDialogueOrder(dto.getDialogueOrder());
            newDialogue.setDialogueType(dto.getDialogueType());
            newDialogue.setMalay(dto.getMalay());
            newDialogue.setEnglish(dto.getEnglish());
            newDialogue.setAudioUrl(dto.getAudioUrl());

            dialogueMapper.addDialogue(newDialogue);
            logger.info("Created dialogue: " + newDialogue);
            return newDialogue;
        }
        catch (DataIntegrityViolationException e) {
            throw new DialogueOrderConflictException(
                    "A dialogue of type '" + dto.getDialogueType() +
                            "' with order '" + dto.getDialogueOrder() + "' already exists in this scenario."
            );
        }
    }

    @Transactional
    public void deleteDialogue(String dialogueId) {
        logger.info("Deleting dialogue with id: " + dialogueId);
        // Ensure dialogue exists
        Dialogue dialogue = getDialogueById(dialogueId);

        // Delete dialogue
        dialogueMapper.deleteDialogue(dialogueId);
        logger.info("Deleted dialogue with id: " + dialogueId);
    }
}
