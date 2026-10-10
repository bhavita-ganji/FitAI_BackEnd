package com.fitai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exercises", indexes = {
    @Index(name = "idx_exercise_muscle", columnList = "muscleGroup"),
    @Index(name = "idx_exercise_difficulty", columnList = "difficulty"),
    @Index(name = "idx_exercise_equipment", columnList = "equipment")
})
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 50)
    private String muscleGroup; // CHEST, BACK, LEGS, SHOULDERS, ARMS, CORE, FULL_BODY

    @Column(nullable = false, length = 50)
    private String equipment; // BARBELL, DUMBBELL, BODYWEIGHT, CABLE, MACHINE, KETTLEBELL, RESISTANCE_BAND

    @Column(nullable = false, length = 30)
    private String difficulty; // BEGINNER, INTERMEDIATE, ADVANCED

    @Column(nullable = false, length = 30)
    private String exerciseType; // STRENGTH, CARDIO, FLEXIBILITY, HIIT

    @Lob
    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String safetyTips;

    private boolean isCustom = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Exercise() {}

    public Exercise(String name, String description, String muscleGroup, String equipment, String difficulty, String exerciseType, String instructions, String safetyTips) {
        this.name = name;
        this.description = description;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
        this.difficulty = difficulty;
        this.exerciseType = exerciseType;
        this.instructions = instructions;
        this.safetyTips = safetyTips;
    }

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

    public User getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(User createdByUser) { this.createdByUser = createdByUser; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
