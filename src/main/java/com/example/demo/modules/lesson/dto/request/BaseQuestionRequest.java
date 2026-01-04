package com.example.demo.modules.lesson.dto.request;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.example.demo.enums.QuestionType;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, // Use the "type" property name to decide the subclass
        include = JsonTypeInfo.As.PROPERTY, // The type info is a property in the JSON
        property = "type", // The name of the property is "type"
        visible = true // Make the "type" property available for deserialization
)
@JsonSubTypes({
        // This is the mapping. It tells Jackson:
        // If the "type" property is "SENTENCE_BUILDING", use the SentenceBuildingRequest class.
        @JsonSubTypes.Type(value = SentenceBuildingRequest.class, name = "SENTENCE_BUILDING"),
        // If the "type" property is "SPEAKING", use the SpeakingRequest class.
        @JsonSubTypes.Type(value = ListeningRequest.class, name = "LISTENING"),
        @JsonSubTypes.Type(value = MultipleChoiceRequest.class, name = "MULTIPLE_CHOICE"),
        // Add more types here as you create them...
})

@Getter
@Setter
@ToString
public class BaseQuestionRequest {

    @NotNull
    private String lessonId;

    @NotNull
    private QuestionType type; // The "type" property from the JSON will be mapped here

    @NotNull
    private String promptText;

}
