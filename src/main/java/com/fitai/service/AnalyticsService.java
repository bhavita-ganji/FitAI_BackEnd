package com.fitai.service;

import com.fitai.dto.AnalyticsSummaryDto;
import com.fitai.entity.MealEntry;
import com.fitai.entity.NutritionGoal;
import com.fitai.entity.UserProfile;
import com.fitai.entity.WorkoutSession;
import com.fitai.repository.MealEntryRepository;
import com.fitai.repository.NutritionGoalRepository;
import com.fitai.repository.UserProfileRepository;
import com.fitai.repository.WorkoutSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final WorkoutSessionRepository workoutSessionRepository;
    private final MealEntryRepository mealEntryRepository;
    private final NutritionGoalRepository nutritionGoalRepository;
    private final UserProfileRepository userProfileRepository;

    public AnalyticsService(WorkoutSessionRepository workoutSessionRepository,
                            MealEntryRepository mealEntryRepository,
                            NutritionGoalRepository nutritionGoalRepository,
                            UserProfileRepository userProfileRepository) {
        this.workoutSessionRepository = workoutSessionRepository;
        this.mealEntryRepository = mealEntryRepository;
        this.nutritionGoalRepository = nutritionGoalRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional(readOnly = true)
    public AnalyticsSummaryDto getUserAnalytics(Long userId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(Math.max(days - 1, 6));

        List<WorkoutSession> sessions = workoutSessionRepository
                .findByUserIdAndSessionDateBetweenOrderBySessionDateAsc(userId, startDate, endDate);

        List<MealEntry> meals = mealEntryRepository
                .findByUserIdAndEntryDateBetweenOrderByEntryDateAsc(userId, startDate, endDate);

        NutritionGoal goal = nutritionGoalRepository.findByUserId(userId)
                .orElse(new NutritionGoal());

        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(null);

        AnalyticsSummaryDto summary = new AnalyticsSummaryDto();

        // 1. Workout metrics
        int totalCompleted = 0;
        int totalMinutes = 0;
        int totalCaloriesBurned = 0;

        for (WorkoutSession s : sessions) {
            if ("COMPLETED".equalsIgnoreCase(s.getStatus())) {
                totalCompleted++;
                totalMinutes += (s.getDurationMinutes() != null ? s.getDurationMinutes() : 0);
                totalCaloriesBurned += (s.getCaloriesBurned() != null ? s.getCaloriesBurned() : 0);
            }
        }

        summary.setTotalWorkoutsCompleted(totalCompleted);
        summary.setTotalMinutesExercised(totalMinutes);
        summary.setTotalCaloriesBurned(totalCaloriesBurned);

        // Consistency: percentage of expected target workout days (assuming 4 workouts/week target)
        double expectedWorkouts = Math.max(1, (days / 7.0) * 4.0);
        double consistency = Math.min(100.0, Math.round((totalCompleted / expectedWorkouts) * 1000.0) / 10.0);
        summary.setWorkoutConsistencyRate(consistency);

        // 2. Nutrition trends
        Map<LocalDate, Double> dailyCaloriesMap = new HashMap<>();
        double totalProtein = 0.0;
        double totalCarbs = 0.0;
        double totalFat = 0.0;

        for (MealEntry m : meals) {
            dailyCaloriesMap.put(
                    m.getEntryDate(),
                    dailyCaloriesMap.getOrDefault(m.getEntryDate(), 0.0) + m.getCalculatedCalories()
            );
            totalProtein += m.getCalculatedProtein();
            totalCarbs += m.getCalculatedCarbs();
            totalFat += m.getCalculatedFat();
        }

        int daysWithMeals = Math.max(1, dailyCaloriesMap.size());
        summary.setAverageDailyCalories(Math.round((dailyCaloriesMap.values().stream().mapToDouble(Double::doubleValue).sum() / daysWithMeals) * 10.0) / 10.0);
        summary.setAverageDailyProtein(Math.round((totalProtein / daysWithMeals) * 10.0) / 10.0);
        summary.setAverageDailyCarbs(Math.round((totalCarbs / daysWithMeals) * 10.0) / 10.0);
        summary.setAverageDailyFat(Math.round((totalFat / daysWithMeals) * 10.0) / 10.0);

        List<AnalyticsSummaryDto.DailyCalorieStat> calorieTrends = new ArrayList<>();
        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            double cals = dailyCaloriesMap.getOrDefault(d, 0.0);
            calorieTrends.add(new AnalyticsSummaryDto.DailyCalorieStat(d, cals, goal.getDailyCaloriesTarget()));
        }
        summary.setCalorieTrends(calorieTrends);

        // 3. Weekly activity trends (last 7 days)
        List<AnalyticsSummaryDto.WeeklyActivityStat> activityTrends = new ArrayList<>();
        LocalDate last7Start = endDate.minusDays(6);
        for (LocalDate d = last7Start; !d.isAfter(endDate); d = d.plusDays(1)) {
            final LocalDate currentDay = d;
            List<WorkoutSession> daySessions = sessions.stream()
                    .filter(s -> currentDay.equals(s.getSessionDate()))
                    .collect(Collectors.toList());

            int mins = daySessions.stream().mapToInt(s -> s.getDurationMinutes() != null ? s.getDurationMinutes() : 0).sum();
            int cals = daySessions.stream().mapToInt(s -> s.getCaloriesBurned() != null ? s.getCaloriesBurned() : 0).sum();
            boolean comp = daySessions.stream().anyMatch(s -> "COMPLETED".equalsIgnoreCase(s.getStatus()));

            String dayName = d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            activityTrends.add(new AnalyticsSummaryDto.WeeklyActivityStat(dayName, mins, cals, comp));
        }
        summary.setActivityTrends(activityTrends);

        // 4. Weight history (simulated trend from user profile and target)
        List<AnalyticsSummaryDto.WeightProgressStat> weightHistory = new ArrayList<>();
        if (profile != null && profile.getWeightKg() != null) {
            double currentWeight = profile.getWeightKg();
            double targetWeight = profile.getTargetWeightKg() != null ? profile.getTargetWeightKg() : currentWeight;
            double step = (currentWeight - targetWeight) / Math.max(1, days);

            for (int i = 0; i < Math.min(days, 14); i += 2) {
                LocalDate date = endDate.minusDays(i);
                double weight = Math.round((currentWeight + (step * (i / 10.0))) * 10.0) / 10.0;
                weightHistory.add(0, new AnalyticsSummaryDto.WeightProgressStat(date, weight));
            }
        }
        summary.setWeightHistory(weightHistory);

        return summary;
    }
}
