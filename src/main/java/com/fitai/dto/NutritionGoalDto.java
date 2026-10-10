package com.fitai.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class NutritionGoalDto {

    private Long id;

    @NotNull(message = "Daily calories target is required")
    @Min(value = 500, message = "Daily calories target must be at least 500")
    private Integer dailyCaloriesTarget = 2200;

    @NotNull(message = "Daily protein target is required")
    @Min(value = 10, message = "Daily protein target must be at least 10g")
    private Integer dailyProteinTarget = 140;

    @NotNull(message = "Daily carbs target is required")
    @Min(value = 10, message = "Daily carbs target must be at least 10g")
    private Integer dailyCarbsTarget = 250;

    @NotNull(message = "Daily fat target is required")
    @Min(value = 10, message = "Daily fat target must be at least 10g")
    private Integer dailyFatTarget = 65;

    private Integer waterIntakeMlTarget = 2500;

    public NutritionGoalDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
}
