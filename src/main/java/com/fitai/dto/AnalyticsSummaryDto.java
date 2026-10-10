package com.fitai.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyticsSummaryDto {

    private int totalWorkoutsCompleted;
    private int totalMinutesExercised;
    private int totalCaloriesBurned;
    private double workoutConsistencyRate; // Percentage e.g. 85.5%

    private double averageDailyCalories;
    private double averageDailyProtein;
    private double averageDailyCarbs;
    private double averageDailyFat;

    private List<DailyCalorieStat> calorieTrends = new ArrayList<>();
    private List<WeeklyActivityStat> activityTrends = new ArrayList<>();
    private List<WeightProgressStat> weightHistory = new ArrayList<>();

    public AnalyticsSummaryDto() {}

    public static class DailyCalorieStat {
        private LocalDate date;
        private double caloriesConsumed;
        private int targetCalories;

        public DailyCalorieStat() {}
        public DailyCalorieStat(LocalDate date, double caloriesConsumed, int targetCalories) {
            this.date = date;
            this.caloriesConsumed = caloriesConsumed;
            this.targetCalories = targetCalories;
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public double getCaloriesConsumed() { return caloriesConsumed; }
        public void setCaloriesConsumed(double caloriesConsumed) { this.caloriesConsumed = caloriesConsumed; }
        public int getTargetCalories() { return targetCalories; }
        public void setTargetCalories(int targetCalories) { this.targetCalories = targetCalories; }
    }

    public static class WeeklyActivityStat {
        private String day;
        private int minutes;
        private int caloriesBurned;
        private boolean completed;

        public WeeklyActivityStat() {}
        public WeeklyActivityStat(String day, int minutes, int caloriesBurned, boolean completed) {
            this.day = day;
            this.minutes = minutes;
            this.caloriesBurned = caloriesBurned;
            this.completed = completed;
        }

        public String getDay() { return day; }
        public void setDay(String day) { this.day = day; }
        public int getMinutes() { return minutes; }
        public void setMinutes(int minutes) { this.minutes = minutes; }
        public int getCaloriesBurned() { return caloriesBurned; }
        public void setCaloriesBurned(int caloriesBurned) { this.caloriesBurned = caloriesBurned; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    public static class WeightProgressStat {
        private LocalDate date;
        private double weightKg;

        public WeightProgressStat() {}
        public WeightProgressStat(LocalDate date, double weightKg) {
            this.date = date;
            this.weightKg = weightKg;
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public double getWeightKg() { return weightKg; }
        public void setWeightKg(double weightKg) { this.weightKg = weightKg; }
    }

    public int getTotalWorkoutsCompleted() { return totalWorkoutsCompleted; }
    public void setTotalWorkoutsCompleted(int totalWorkoutsCompleted) { this.totalWorkoutsCompleted = totalWorkoutsCompleted; }

    public int getTotalMinutesExercised() { return totalMinutesExercised; }
    public void setTotalMinutesExercised(int totalMinutesExercised) { this.totalMinutesExercised = totalMinutesExercised; }

    public int getTotalCaloriesBurned() { return totalCaloriesBurned; }
    public void setTotalCaloriesBurned(int totalCaloriesBurned) { this.totalCaloriesBurned = totalCaloriesBurned; }

    public double getWorkoutConsistencyRate() { return workoutConsistencyRate; }
    public void setWorkoutConsistencyRate(double workoutConsistencyRate) { this.workoutConsistencyRate = workoutConsistencyRate; }

    public double getAverageDailyCalories() { return averageDailyCalories; }
    public void setAverageDailyCalories(double averageDailyCalories) { this.averageDailyCalories = averageDailyCalories; }

    public double getAverageDailyProtein() { return averageDailyProtein; }
    public void setAverageDailyProtein(double averageDailyProtein) { this.averageDailyProtein = averageDailyProtein; }

    public double getAverageDailyCarbs() { return averageDailyCarbs; }
    public void setAverageDailyCarbs(double averageDailyCarbs) { this.averageDailyCarbs = averageDailyCarbs; }

    public double getAverageDailyFat() { return averageDailyFat; }
    public void setAverageDailyFat(double averageDailyFat) { this.averageDailyFat = averageDailyFat; }

    public List<DailyCalorieStat> getCalorieTrends() { return calorieTrends; }
    public void setCalorieTrends(List<DailyCalorieStat> calorieTrends) { this.calorieTrends = calorieTrends; }

    public List<WeeklyActivityStat> getActivityTrends() { return activityTrends; }
    public void setActivityTrends(List<WeeklyActivityStat> activityTrends) { this.activityTrends = activityTrends; }

    public List<WeightProgressStat> getWeightHistory() { return weightHistory; }
    public void setWeightHistory(List<WeightProgressStat> weightHistory) { this.weightHistory = weightHistory; }
}
