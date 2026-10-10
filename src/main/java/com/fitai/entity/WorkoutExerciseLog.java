package com.fitai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "workout_exercise_logs")
public class WorkoutExerciseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private WorkoutSession session;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    private Integer setNumber = 1;
    private Integer reps = 10;
    private Double weightKg = 0.0;
    private boolean completed = true;

    public WorkoutExerciseLog() {}

    public WorkoutExerciseLog(WorkoutSession session, Exercise exercise, Integer setNumber, Integer reps, Double weightKg, boolean completed) {
        this.session = session;
        this.exercise = exercise;
        this.setNumber = setNumber;
        this.reps = reps;
        this.weightKg = weightKg;
        this.completed = completed;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WorkoutSession getSession() { return session; }
    public void setSession(WorkoutSession session) { this.session = session; }

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }

    public Integer getSetNumber() { return setNumber; }
    public void setSetNumber(Integer setNumber) { this.setNumber = setNumber; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
