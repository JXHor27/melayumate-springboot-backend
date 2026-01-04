package com.example.demo.modules.scenario.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.file.FileStorageService;
import com.example.demo.service.IdGeneratorService;
import com.example.demo.modules.scenario.dto.ScenarioCreateDTO;
import com.example.demo.modules.scenario.entity.Dialogue;
import com.example.demo.modules.scenario.entity.Scenario;
import com.example.demo.modules.scenario.repo.DialogueMapper;
import com.example.demo.modules.scenario.repo.ScenarioMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing scenarios.
 */
@RequiredArgsConstructor
@Service
public class ScenarioService {

    private static final Log logger = LogFactory.getLog(ScenarioService.class);

    @Autowired
    private final ScenarioMapper scenarioMapper;

    @Autowired
    private final DialogueMapper dialogueMapper;

    @Autowired
    private final FileStorageService fileStorageService;

    @Autowired
    private final IdGeneratorService idGeneratorService;

    public List<Scenario> getScenariosByUserId(String userId) {
        logger.info("Fetching scenarios for userId: " + userId);
        List<Scenario> scenarioList = scenarioMapper.getScenariosByUserId(userId);
        if (scenarioList.isEmpty()) {
            logger.info("Scenario list not found with userId: " + userId);
            return scenarioList;
        }
        logger.info("Fetched scenarios: " + scenarioList);
        return scenarioList;
    }

    public Scenario getScenarioById(String scenarioId) {
        logger.info("Fetching scenario with id: " + scenarioId);
        Scenario scenario = scenarioMapper.getScenarioById(scenarioId);
        if (scenario == null){
            logger.error("Scenario not found with id: " + scenarioId);
            throw new ResourceNotFoundException("Scenario not found with id: " + scenarioId);
        }
        logger.info("Fetched scenario: " + scenario);
        return scenario;
    }

    public Scenario createScenario(ScenarioCreateDTO dto) {
        logger.info("Creating scenario: " + dto);

        Scenario newScenario = new Scenario();
        newScenario.setScenarioId(idGeneratorService.generateScenarioId());
        newScenario.setUserId(dto.getUserId());
        newScenario.setTitle(dto.getTitle());
        newScenario.setDescription(dto.getDescription());
        // a new scenario starts with 0 dialogue
        newScenario.setDialogueNumber(0);
        scenarioMapper.addScenario(newScenario);
        logger.info("Created scenario: " + newScenario);
        return newScenario;
    }

    public Scenario editScenario(String scenarioId, ScenarioCreateDTO dto) {
        logger.info("Editing scenario with id: " + scenarioId);
        // Ensure scenario exists
        Scenario existingScenario = getScenarioById(scenarioId);

        // Update fields from DTO
        existingScenario.setTitle(dto.getTitle());
        existingScenario.setDescription(dto.getDescription());

        scenarioMapper.editScenario(existingScenario);
        logger.info("Edited scenario: " + existingScenario);
        return existingScenario;
    }

    @Transactional
    public void deleteScenario(String scenarioId) {
        logger.info("Deleting scenario with id: " + scenarioId);
        // Delete associated audio files from S3
        List<Dialogue> dialogueList = dialogueMapper.getDialogueListById(scenarioId);
        for(Dialogue dialogue : dialogueList){
            if (dialogue.getAudioUrl() != null && !dialogue.getAudioUrl().isEmpty()) {
                fileStorageService.deleteFile(dialogue.getAudioUrl());
            }
        }
        scenarioMapper.deleteScenario(scenarioId);
        logger.info("Deleted scenario with id: " + scenarioId);
    }

    public void updateDialogueNumber(String scenarioId, int number){
        scenarioMapper.updateDialogueNumber(scenarioId, number);
    }
}
