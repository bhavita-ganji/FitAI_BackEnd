package com.fitai.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class FitnessGoalDto {

    private Long id;

    @NotBlank(message = "Goal type is required")
    private String goalType;

    private String title;
    private String description;
    private Double targetValue;
    private Double currentValue;
    private String metricUnit;
    private LocalDate targetDate;
    private boolean completed;

    public FitnessGoalDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGoalType() { return goalType; }
    public void setGoalType(String goalType) { this.goalType = goalType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getTargetValue() { return targetValue; }
    public void setTargetValue(Double targetValue) { this.targetValue = targetValue; }

    public Double getCurrentValue() { return currentValue; }
    public void setCurrentValue(Double currentValue) { this.currentValue = currentValue; }

    public String getMetricUnit() { return metricUnit; }
    public void setMetricUnit(String metricUnit) { this.metricUnit = metricUnit; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
