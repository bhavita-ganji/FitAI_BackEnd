package com.fitai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nutrition_goals")
public class NutritionGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private Integer dailyCaloriesTarget = 2200;
    private Integer dailyProteinTarget = 140; // in grams
    private Integer dailyCarbsTarget = 250;   // in grams
    private Integer dailyFatTarget = 65;      // in grams
    private Integer waterIntakeMlTarget = 2500;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public NutritionGoal() {}

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Integer getDailyCaloriesTarget() { return dailyCaloriesTarget; }
    public void setDailyCaloriesTarget(Integer dailyCaloriesTarget) { this.dailyCaloriesTarget = dailyCaloriesTarget; }

    public Integer getDailyProteinTarget() { return dailyProteinTarget; }
    public void setDailyProteinTarget(Integer dailyProteinTarget) { this.dailyProteinTarget = dailyProteinTarget; }

    public Integer getDailyCarbsTarget() { return dailyCarbsTarget; }
    public void setDailyCarbsTarget(Integer dailyCarbsTarget) { this.dailyCarbsTarget = dailyCarbsTarget; }

    public Integer getDailyFatTarget() { return dailyFatTarget; }
    public void setDailyFatTarget(Integer dailyFatTarget) { this.dailyFatTarget = dailyFatTarget; }

    public Integer getWaterIntakeMlTarget() { return waterIntakeMlTarget; }
    public void setWaterIntakeMlTarget(Integer waterIntakeMlTarget) { this.waterIntakeMlTarget = waterIntakeMlTarget; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
