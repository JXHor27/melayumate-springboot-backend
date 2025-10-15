package com.example.demo.scenario.entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Scenario {
    private String userId;
    private String scenarioId;
    private String title;
    private String description;
}
