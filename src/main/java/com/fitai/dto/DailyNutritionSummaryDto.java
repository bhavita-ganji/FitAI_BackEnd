package com.fitai.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DailyNutritionSummaryDto {

    private LocalDate date;
    private Double totalCalories = 0.0;
    private Double totalProtein = 0.0;
    private Double totalCarbs = 0.0;
    private Double totalFat = 0.0;

    private Integer targetCalories = 2200;
    private Integer targetProtein = 140;
    private Integer targetCarbs = 250;
    private Integer targetFat = 65;
    private Integer targetWaterMl = 2500;

    private List<MealEntryDto> mealEntries = new ArrayList<>();

    public DailyNutritionSummaryDto() {}

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Double getTotalCalories() { return totalCalories; }
    public void setTotalCalories(Double totalCalories) { this.totalCalories = totalCalories; }

    public Double getTotalProtein() { return totalProtein; }
    public void setTotalProtein(Double totalProtein) { this.totalProtein = totalProtein; }

    public Double getTotalCarbs() { return totalCarbs; }
    public void setTotalCarbs(Double totalCarbs) { this.totalCarbs = totalCarbs; }

    public Double getTotalFat() { return totalFat; }
    public void setTotalFat(Double totalFat) { this.totalFat = totalFat; }

    public Integer getTargetCalories() { return targetCalories; }
    public void setTargetCalories(Integer targetCalories) { this.targetCalories = targetCalories; }

    public Integer getTargetProtein() { return targetProtein; }
    public void setTargetProtein(Integer targetProtein) { this.targetProtein = targetProtein; }

    public Integer getTargetCarbs() { return targetCarbs; }
    public void setTargetCarbs(Integer targetCarbs) { this.targetCarbs = targetCarbs; }

    public Integer getTargetFat() { return targetFat; }
    public void setTargetFat(Integer targetFat) { this.targetFat = targetFat; }

    public Integer getTargetWaterMl() { return targetWaterMl; }
    public void setTargetWaterMl(Integer targetWaterMl) { this.targetWaterMl = targetWaterMl; }

    public List<MealEntryDto> getMealEntries() { return mealEntries; }
    public void setMealEntries(List<MealEntryDto> mealEntries) { this.mealEntries = mealEntries; }
}
