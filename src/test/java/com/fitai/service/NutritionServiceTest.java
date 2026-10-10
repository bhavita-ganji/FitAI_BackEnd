package com.fitai.service;

import com.fitai.dto.DailyNutritionSummaryDto;
import com.fitai.dto.MealEntryDto;
import com.fitai.entity.FoodItem;
import com.fitai.entity.MealEntry;
import com.fitai.entity.NutritionGoal;
import com.fitai.entity.Role;
import com.fitai.entity.User;
import com.fitai.repository.FoodItemRepository;
import com.fitai.repository.MealEntryRepository;
import com.fitai.repository.NutritionGoalRepository;
import com.fitai.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NutritionServiceTest {

    @Mock
    private FoodItemRepository foodItemRepository;

    @Mock
    private MealEntryRepository mealEntryRepository;

    @Mock
    private NutritionGoalRepository nutritionGoalRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NutritionService nutritionService;

    private User user;
    private FoodItem chicken;
    private NutritionGoal goal;

    @BeforeEach
    void setUp() {
        user = new User("user@fitai.com", "hash", "Gym Goer", Role.ROLE_USER);
        user.setId(5L);

        chicken = new FoodItem("Chicken Breast", 100.0, "g", 165.0, 31.0, 0.0, 3.6, "PROTEIN");
        chicken.setId(20L);

        goal = new NutritionGoal();
        goal.setUser(user);
        goal.setDailyCaloriesTarget(2000);
        goal.setDailyProteinTarget(150);
        goal.setDailyCarbsTarget(200);
        goal.setDailyFatTarget(50);
    }

    @Test
    void testLogMealCalculatesMacrosCorrectly() {
        MealEntryDto dto = new MealEntryDto();
        dto.setFoodItemId(20L);
        dto.setMealType("LUNCH");
        dto.setEntryDate(LocalDate.now());
        dto.setServings(2.0); // 200g chicken -> 330 kcal, 62g protein

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(foodItemRepository.findById(20L)).thenReturn(Optional.of(chicken));
        when(mealEntryRepository.save(any(MealEntry.class))).thenAnswer(invocation -> {
            MealEntry m = invocation.getArgument(0);
            m.setId(99L);
            return m;
        });

        MealEntryDto logged = nutritionService.logMeal(5L, dto);

        assertNotNull(logged);
        assertEquals(330.0, logged.getCalculatedCalories());
        assertEquals(62.0, logged.getCalculatedProtein());
        assertEquals(0.0, logged.getCalculatedCarbs());
        assertEquals(7.2, logged.getCalculatedFat());
    }

    @Test
    void testGetDailySummaryAggregatesTotals() {
        LocalDate today = LocalDate.now();
        MealEntry entry = new MealEntry();
        entry.setUser(user);
        entry.setFoodItem(chicken);
        entry.setMealType("LUNCH");
        entry.setEntryDate(today);
        entry.setServings(2.0);
        entry.recalculateNutrition();

        when(mealEntryRepository.findByUserIdAndEntryDateOrderByCreatedAtAsc(5L, today))
                .thenReturn(Collections.singletonList(entry));
        when(nutritionGoalRepository.findByUserId(5L)).thenReturn(Optional.of(goal));

        DailyNutritionSummaryDto summary = nutritionService.getDailySummary(5L, today);

        assertNotNull(summary);
        assertEquals(330.0, summary.getTotalCalories());
        assertEquals(62.0, summary.getTotalProtein());
        assertEquals(2000, summary.getTargetCalories());
        assertEquals(1, summary.getMealEntries().size());
    }
}
