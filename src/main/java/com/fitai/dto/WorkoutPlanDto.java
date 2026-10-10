package com.fitai.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class WorkoutPlanDto {

    private Long id;

    @NotBlank(message = "Plan name is required")
    private String name;

    private String description;
    private String difficulty;

    @Min(value = 1, message = "Days per week must be between 1 and 7")
    private Integer daysPerWeek = 3;

    private boolean isTemplate;

    @Valid
    private List<WorkoutPlanExerciseDto> planExercises = new ArrayList<>();

    public WorkoutPlanDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getDaysPerWeek() { return daysPerWeek; }
    public void setDaysPerWeek(Integer daysPerWeek) { this.daysPerWeek = daysPerWeek; }

    public boolean isTemplate() { return isTemplate; }
    public void setTemplate(boolean template) { isTemplate = template; }

    public List<WorkoutPlanExerciseDto> getPlanExercises() { return planExercises; }
    public void setPlanExercises(List<WorkoutPlanExerciseDto> planExercises) { this.planExercises = planExercises; }
}
