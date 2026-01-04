package com.example.demo.modules.scenario.entity;
import com.example.demo.enums.DialogueType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Dialogue {
    private String dialogueId;
    private String scenarioId;
    private DialogueType dialogueType;       // Question(Sender)/Answer(Receiver)
    private String english;
    private String malay;
    private int dialogueOrder;   // order displayed in dialogue
    private String audioUrl;
    private String objectKey;  // S3 object key



}
