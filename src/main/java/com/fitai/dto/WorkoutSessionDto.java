package com.fitai.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkoutSessionDto {

    private Long id;
    private Long workoutPlanId;
    private String workoutPlanName;

    @NotBlank(message = "Session name is required")
    private String sessionName;

    @NotNull(message = "Session date is required")
    private LocalDate sessionDate = LocalDate.now();

    private Integer durationMinutes = 45;
    private Integer caloriesBurned;
    private String notes;
    private String status = "COMPLETED";

    @Valid
    private List<WorkoutExerciseLogDto> exerciseLogs = new ArrayList<>();

    public WorkoutSessionDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getWorkoutPlanId() { return workoutPlanId; }
    public void setWorkoutPlanId(Long workoutPlanId) { this.workoutPlanId = workoutPlanId; }

    public String getWorkoutPlanName() { return workoutPlanName; }
    public void setWorkoutPlanName(String workoutPlanName) { this.workoutPlanName = workoutPlanName; }

    public String getSessionName() { return sessionName; }
    public void setSessionName(String sessionName) { this.sessionName = sessionName; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<WorkoutExerciseLogDto> getExerciseLogs() { return exerciseLogs; }
    public void setExerciseLogs(List<WorkoutExerciseLogDto> exerciseLogs) { this.exerciseLogs = exerciseLogs; }
}
