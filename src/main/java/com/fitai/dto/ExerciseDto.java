package com.fitai.dto;

import jakarta.validation.constraints.NotBlank;

public class ExerciseDto {

    private Long id;

    @NotBlank(message = "Exercise name is required")
    private String name;

    private String description;

    @NotBlank(message = "Muscle group is required")
    private String muscleGroup;

    @NotBlank(message = "Equipment is required")
    private String equipment;

    @NotBlank(message = "Difficulty is required")
    private String difficulty;

    @NotBlank(message = "Exercise type is required")
    private String exerciseType;

    private String instructions;
    private String safetyTips;
    private boolean isCustom;

    public ExerciseDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMuscleGroup() { return muscleGroup; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getExerciseType() { return exerciseType; }
    public void setExerciseType(String exerciseType) { this.exerciseType = exerciseType; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public String getSafetyTips() { return safetyTips; }
    public void setSafetyTips(String safetyTips) { this.safetyTips = safetyTips; }

    public boolean isCustom() { return isCustom; }
    public void setCustom(boolean custom) { isCustom = custom; }
}
