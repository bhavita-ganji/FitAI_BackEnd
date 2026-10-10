package com.fitai.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UserProfileDto {

    private Long id;

    @Min(value = 10, message = "Age must be at least 10")
    @Max(value = 120, message = "Age must be realistic")
    private Integer age;

    private String gender;

    @Min(value = 50, message = "Height must be at least 50 cm")
    @Max(value = 280, message = "Height must be realistic")
    private Double heightCm;

    @Min(value = 20, message = "Weight must be at least 20 kg")
    @Max(value = 400, message = "Weight must be realistic")
    private Double weightKg;

    private Double targetWeightKg;

    private String activityLevel;
    private String experienceLevel;
    private Integer preferredWorkoutDurationMinutes;
    private String primaryGoal;
    private String dietaryPreferences;
    private String healthNotes;

    public UserProfileDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public Double getTargetWeightKg() { return targetWeightKg; }
    public void setTargetWeightKg(Double targetWeightKg) { this.targetWeightKg = targetWeightKg; }

    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }

    public String getExperienceLevel() { return experienceLevel; }
    public void setExperienceLevel(String experienceLevel) { this.experienceLevel = experienceLevel; }

    public Integer getPreferredWorkoutDurationMinutes() { return preferredWorkoutDurationMinutes; }
    public void setPreferredWorkoutDurationMinutes(Integer preferredWorkoutDurationMinutes) { this.preferredWorkoutDurationMinutes = preferredWorkoutDurationMinutes; }

    public String getPrimaryGoal() { return primaryGoal; }
    public void setPrimaryGoal(String primaryGoal) { this.primaryGoal = primaryGoal; }

    public String getDietaryPreferences() { return dietaryPreferences; }
    public void setDietaryPreferences(String dietaryPreferences) { this.dietaryPreferences = dietaryPreferences; }

    public String getHealthNotes() { return healthNotes; }
    public void setHealthNotes(String healthNotes) { this.healthNotes = healthNotes; }
}
