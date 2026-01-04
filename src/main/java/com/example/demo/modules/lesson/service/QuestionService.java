package com.example.demo.modules.lesson.service;

import com.example.demo.enums.QuestionType;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.file.FileStorageService;
import com.example.demo.modules.lesson.dto.request.*;
import com.example.demo.modules.lesson.dto.result.AnswerDistribution;
import com.example.demo.modules.lesson.dto.result.QuestionAnalyticsDTO;
import com.example.demo.modules.lesson.dto.result.QuestionProgressDTO;
import com.example.demo.modules.lesson.entity.Question;
import com.example.demo.modules.lesson.entity.Answer;
import com.example.demo.modules.lesson.repo.AnswerMapper;
import com.example.demo.modules.lesson.repo.QuestionMapper;
import com.example.demo.service.IdGeneratorService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private static final Log logger = LogFactory.getLog(QuestionService.class);


    @Autowired
    private final QuestionMapper questionMapper;

    @Autowired
    private final AnswerMapper answerMapper;

    @Autowired
    private final ObjectMapper objectMapper; // For converting the attributes to/from JSON string

    @Autowired
    private final IdGeneratorService idGeneratorService;

    @Autowired
    private final FileStorageService fileStorageService;

    @Value("${aws.s3.base-url}")
    private String s3BaseUrl;

    public void createQuestion(BaseQuestionRequest request, MultipartFile audioFile) throws JsonProcessingException {
        // --- Use instanceof to handle the different types ---

        if (request instanceof SentenceBuildingRequest) {
            processSentenceBuilding((SentenceBuildingRequest) request);
        } else if (request instanceof ListeningRequest) {
            // The file is passed along here
            processListening((ListeningRequest) request, audioFile);
        } else if (request instanceof MultipleChoiceRequest) {
            processMultipleChoice((MultipleChoiceRequest) request);
        } else {
            throw new IllegalArgumentException("Unsupported question type: " + request.getType());
        }
    }
    private void processSentenceBuilding(SentenceBuildingRequest request) throws JsonProcessingException {
        logger.info("Processing SENTENCE_BUILDING for lesson: " + request.getLessonId());
        // 1. Create a new Question entity
        Question question = new Question();
        question.setQuestionId(idGeneratorService.generateQuestionId());
        question.setLessonId(request.getLessonId());
        question.setQuestionType(request.getType());
        question.setPromptText(request.getPromptText());
        question.setCreatedAt(Instant.now());

        // 2. Build the JSON attributes map
        Map<String, Object> attributes = Map.of(
                "component_words", request.getWords(),
                "correct_sentence", request.getCorrectSentence()
        );
        String attributesJson = objectMapper.writeValueAsString(attributes);
        question.setAttributes(attributesJson);

        questionMapper.createQuestion(question);

        logger.info("Created SENTENCE_BUILDING question: " + question);
    }

    private void processListening(ListeningRequest request, MultipartFile audioFile) throws JsonProcessingException {
        if (audioFile == null || audioFile.isEmpty()) {
            throw new IllegalArgumentException("Audio file is required for listening questions.");
        }
        logger.info("Processing LISTENING for lesson: " + request.getLessonId());
        logger.info("Listening question audio file name: " + request);

        // 1. Upload the audioFile to S3 and get the object key
        String audioObjectKey = fileStorageService.uploadFile(audioFile);
        //String audioObjectKey = "audio/some_key.wav"; // Placeholder

        Question question = new Question();
        question.setQuestionId(idGeneratorService.generateQuestionId());
        question.setLessonId(request.getLessonId());
        question.setQuestionType(request.getType());
        question.setPromptText(request.getPromptText());
        question.setCreatedAt(Instant.now());


        // 3. Build the JSON attributes map
        Map<String, Object> attributes = Map.of(
                "reference_audio_key", audioObjectKey,
                "options", request.getOptions(),
                "correct_answer_index", request.getCorrectAnswerIndex()
        );
        String attributesJson = objectMapper.writeValueAsString(attributes);
        question.setAttributes(attributesJson);

        questionMapper.createQuestion(question);

        logger.info("Created LISTENING question: " + question);
    }

    private void processMultipleChoice(MultipleChoiceRequest request) throws JsonProcessingException {
        logger.info("Processing MULTIPLE_CHOICE for lesson: " + request.getLessonId());

        Question question = new Question();
        question.setQuestionId(idGeneratorService.generateQuestionId());
        question.setLessonId(request.getLessonId());
        question.setQuestionType(request.getType());
        question.setPromptText(request.getPromptText());
        question.setCreatedAt(Instant.now());

        Map<String, Object> attributes = Map.of(
                "options", request.getOptions(),
                "correct_answer_index", request.getCorrectAnswerIndex()
        );
        String attributesJson = objectMapper.writeValueAsString(attributes);
        question.setAttributes(attributesJson);

        questionMapper.createQuestion(question);

        logger.info("Created MULTIPLE_CHOICE question: " + question);
    }

    public List<Question> getQuestionsForLecturer(String lessonId) {
        logger.info("Fetching questions for lecturer with lesson id: " + lessonId);
        List<Question> questions = questionMapper.getQuestionsByLessonId(lessonId);
        if (questions.isEmpty()) {
            logger.info("No questions found for lessonId: " + lessonId);
            return questions;
        }
        logger.info("Fetched questions for lecturer: " + questions);
        return questions;
    }

    public List<QuestionProgressDTO> getQuestionsForStudents(String lessonId, String userId) {
        logger.info("Fetching questions for student with lesson id: " + lessonId);
        List<Question> questions = questionMapper.getQuestionsByLessonId(lessonId);
        if (questions.isEmpty()) {
            logger.info("No questions found for lessonId: " + lessonId);
            return List.of();
        }
        for (Question question : questions) {
            if (question.getQuestionType() == QuestionType.LISTENING) {
                retrieveListeningQuestion(question);
            }
        }
        List<Answer> answers = answerMapper.findMostRecentAnswersForUserInLesson(userId, lessonId);

        // Convert the list of answers into a Map for efficient, O(1) lookups
        // The key is the questionId, and the value is the Answer object itself
        Map<String, Answer> answerMap = answers.stream()
                .collect(Collectors.toMap(Answer::getQuestionId, answer -> answer));

        List<QuestionProgressDTO> questionProgressDTOS = convertToDTOList(questions, answerMap);

        logger.info("Fetched questions for students: " + questionProgressDTOS);
        return questionProgressDTOS;
    }

    public void deleteQuestion(String questionId) {
        logger.info("Deleting question with id: " + questionId);
        questionMapper.deleteQuestion(questionId);
        logger.info("Deleted question with id: " + questionId);
    }

    public Answer answerQuestion(QuestionAnswerDTO dto) {
        logger.info("Answering question request: " + dto);
        Answer answer = new Answer();
        answer.setAnswerId(idGeneratorService.generateAnswerId());
        answer.setUserId(dto.getUserId());
        answer.setQuestionId(dto.getQuestionId());
        answer.setLessonId(dto.getLessonId());
        answer.setSelectedAnswer(dto.getSelectedAnswer());
        answer.setCorrect(dto.isCorrect());
        answer.setAnsweredAt(Instant.now());
        answerMapper.answerQuestion(answer);
        logger.info("Question answered successfully: " + answer);
        return answer;
    }

    private void retrieveListeningQuestion(Question question){
        try {
            // Parse the attributes JSON to extract the audio key
            Map<String, Object> attributes = objectMapper.readValue(
                    question.getAttributes(), Map.class);
            String audioKey = (String) attributes.get("reference_audio_key");
            // Construct the public URL
            String audioUrl = s3BaseUrl + "/" + audioKey;
            // Replace the audio key with the public URL in the attributes
            attributes.put("reference_audio_url", audioUrl);
            attributes.remove("reference_audio_key");
            // Convert back to JSON string
            String updatedAttributesJson = objectMapper.writeValueAsString(attributes);
            question.setAttributes(updatedAttributesJson);
        } catch (JsonProcessingException e) {
            logger.error("Error processing question attributes for questionId: " + question.getQuestionId(), e);
        }
    }

    private List<QuestionProgressDTO> convertToDTOList(List<Question> questions,  Map<String, Answer> answerMap){
        return questions.stream().map(question -> {
            QuestionProgressDTO dto = new QuestionProgressDTO();
            dto.setQuestionId(question.getQuestionId());
            dto.setLessonId(question.getLessonId());
            dto.setQuestionType(question.getQuestionType());
            dto.setPromptText(question.getPromptText());
            dto.setAttributes(question.getAttributes());

            Answer userAnswer = answerMap.get(question.getQuestionId());
            if (userAnswer != null) {
                // An answer exists for this question.
                dto.setCompleted(true);
                dto.setSelectedAnswer(userAnswer.getSelectedAnswer());
                dto.setCorrect(userAnswer.isCorrect());

            } else {
                // No answer found for this question. It's not completed.
                dto.setCompleted(false);
                dto.setSelectedAnswer(null);
                dto.setCorrect(false);
            }

            return dto;
        }).toList();
    }

    public QuestionAnalyticsDTO getAnalyticsForQuestion(String questionId) {
        logger.info("Retrieving analytics for question id: " + questionId);
        int totalAttempts = answerMapper.countTotalAttemptsByQuestionId(questionId);

        // If there are no attempts, no need to proceed further.
        if (totalAttempts == 0) {
            return new QuestionAnalyticsDTO(); // Return an empty DTO
        }

        int correctAttempts = answerMapper.countCorrectAttemptsByQuestionId(questionId);

        QuestionAnalyticsDTO analyticsDto = new QuestionAnalyticsDTO();
        analyticsDto.setTotalAttempts(totalAttempts);
        analyticsDto.setCorrectAttempts(correctAttempts);

        Question question = questionMapper.findByQuestionId(questionId);

        if (question == null) {
            logger.error("Question not found with id: " + questionId);
            throw new ResourceNotFoundException("Question not found with id:" + questionId);
        }

        if (question.getQuestionType() == QuestionType.MULTIPLE_CHOICE ||
                question.getQuestionType() == QuestionType.LISTENING) {

            List<AnswerDistribution> distribution =
                    answerMapper.getAnswerDistributionByQuestionId(questionId);

            // For multiple choice, the 'selected_answer' is the index.
            // We should map it back to the actual option text for a user-friendly response.
            try {
                String attributesJson = question.getAttributes();
                Map<String, Object> attributesMap = objectMapper.readValue(
                        attributesJson,
                        new TypeReference<Map<String, Object>>() {}
                );
                List<String> options = (List<String>) attributesMap.get("options");

                if (options != null) {
                    List<AnswerDistribution> mappedDistribution = distribution.stream().map(dist -> {
                        try {
                            int optionIndex = Integer.parseInt(dist.getAnswerOption());
                            if (optionIndex >= 0 && optionIndex < options.size()) {
                                String optionText = options.get(optionIndex);
                                return new AnswerDistribution(optionText, dist.getCount());
                            }
                        } catch (NumberFormatException e) {
                            // Ignore if the answer isn't a valid index
                            logger.error("Invalid option index in distribution: " + dist.getAnswerOption());
                        }
                        return dist; // Fallback
                    }).collect(Collectors.toList());

                    analyticsDto.setAnswerDistribution(mappedDistribution);
                }

            } catch (JsonProcessingException e) {
                logger.error("Failed to parse attributes JSON for question ID: " + questionId);
                analyticsDto.setAnswerDistribution(null); // Or an empty list
            }
        }
        logger.info("Finished analytics for question id: " + questionId);
        return analyticsDto;
    }

}
