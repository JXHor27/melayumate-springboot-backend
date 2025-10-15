package com.example.demo.scenario.controller;

import com.example.demo.scenario.dto.ScenarioCreateDTO;
import com.example.demo.scenario.service.ScenarioService;
import com.example.demo.scenario.entity.Scenario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/scenario")
public class ScenarioController {
    @Autowired
    private final ScenarioService scenarioService;

    /**
     * Get a list of scenarios by user ID.
     *
     * @param userId the ID of the user
     * @return a ResponseEntity containing the list of scenarios
     */
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<Scenario>> getScenariosByUserId(@PathVariable String userId) {
        List<Scenario> scenarioList =  scenarioService.getScenariosByUserId(userId);
        return ResponseEntity.ok(scenarioList);
    }

    /**
     * Get a scenario by its ID.
     *
     * @param scenarioId the ID of the scenario
     * @return a ResponseEntity containing the scenario
     */
    @GetMapping("/{scenarioId}")
    public ResponseEntity<Scenario> getScenarioById(@PathVariable String scenarioId) {
        Scenario scenario = scenarioService.getScenarioById(scenarioId);
        return ResponseEntity.ok(scenario);
    }

    /**
     * Create a new scenario.
     *
     * @param scenarioCreateDTO the DTO containing scenario creation data
     * @return a ResponseEntity containing the created scenario
     */
    @PostMapping("")
    public ResponseEntity<Scenario> createScenario(@Valid @RequestBody ScenarioCreateDTO scenarioCreateDTO){
        Scenario createdScenario = scenarioService.createScenario(scenarioCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdScenario);
    }

    /**
     * Edit an existing scenario.
     *
     * @param scenarioId the ID of the scenario to edit
     * @param scenarioCreateDTO the DTO containing updated scenario data
     * @return a ResponseEntity containing the updated scenario
     */
    @PatchMapping("/{scenarioId}")
    public ResponseEntity<Scenario> editScenario(@PathVariable String scenarioId, @Valid @RequestBody ScenarioCreateDTO scenarioCreateDTO) {
        Scenario updatedScenario = scenarioService.editScenario(scenarioId, scenarioCreateDTO);
        return ResponseEntity.ok(updatedScenario);
    }

    /**
     * Delete a scenario by its ID.
     *
     * @param scenarioId the ID of the scenario to delete
     * @return a ResponseEntity with HTTP status NO_CONTENT
     */
    @DeleteMapping("/{scenarioId}")
    public ResponseEntity<Scenario> deleteScenario(@PathVariable String scenarioId) {
        scenarioService.deleteScenario(scenarioId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
