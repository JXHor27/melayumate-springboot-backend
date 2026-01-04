package com.example.demo.modules.lesson.controller;

import com.example.demo.modules.flashcard.entity.FlashcardList;
import com.example.demo.modules.lesson.dto.request.BaseQuestionRequest;
import com.example.demo.modules.lesson.dto.request.QuestionAnswerDTO;
import com.example.demo.modules.lesson.dto.result.QuestionAnalyticsDTO;
import com.example.demo.modules.lesson.dto.result.QuestionProgressDTO;
import com.example.demo.modules.lesson.entity.Question;
import com.example.demo.modules.lesson.entity.Answer;
import com.example.demo.modules.lesson.service.QuestionService;
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/questions")
public class QuestionController {

    @Autowired
    private final QuestionService questionService;

    @Autowired
    private final ObjectMapper objectMapper;

    /**
     * A single, polymorphic endpoint to create any type of question.
     * It handles both JSON-only requests (like Sentence Building) and
     * multipart requests that include a file (like Speaking).
     */
    @PostMapping(path = "/question", consumes = { "multipart/form-data" })
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<?> createQuestion(
            // The JSON data is sent as a string part named "data"
            @RequestPart(value = "data") String questionDataString,
            // The audio file is sent as a part named "audioFile", but it's optional
            @RequestPart(value = "audioFile", required = false) MultipartFile audioFile) {

        try {
            // --- The Polymorphic Magic ---
            // 1. Use ObjectMapper to deserialize the JSON string into our DTO hierarchy.
            // Jackson will automatically read the "type" property and choose the correct subclass.
            @Valid BaseQuestionRequest requestDto = objectMapper.readValue(questionDataString, BaseQuestionRequest.class);

            // Call the service with the deserialized DTO and the optional file.
            questionService.createQuestion(requestDto, audioFile);

            return ResponseEntity.status(HttpStatus.CREATED).build();

        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid JSON data format."));
        }
    }

    @GetMapping("/{lessonId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<Question>> getQuestionsForLecturer(@PathVariable String lessonId) {
        List<Question> questionList = questionService.getQuestionsForLecturer(lessonId);
        return ResponseEntity.ok(questionList);
    }

    @GetMapping("/{userId}/{lessonId}")
    public ResponseEntity<List<QuestionProgressDTO>> getQuestionsForStudents(@PathVariable String userId, @PathVariable String lessonId) {
        List<QuestionProgressDTO> questionList = questionService.getQuestionsForStudents(lessonId, userId);
        return ResponseEntity.ok(questionList);
    }

    @PostMapping("/answer")
    public ResponseEntity<Answer> answerQuestion(@Valid @RequestBody QuestionAnswerDTO dto) {
        Answer answeredQuestion  = questionService.answerQuestion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(answeredQuestion);
    }

    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<FlashcardList> deleteQuestion(@PathVariable String questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{questionId}/analytics")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<QuestionAnalyticsDTO> getQuestionAnalytics(@PathVariable String questionId) {
        QuestionAnalyticsDTO analytics = questionService.getAnalyticsForQuestion(questionId);
        return ResponseEntity.ok(analytics);
    }
}