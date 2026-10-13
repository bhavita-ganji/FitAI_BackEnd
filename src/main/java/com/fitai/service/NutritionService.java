package com.fitai.service;

import com.fitai.dto.DailyNutritionSummaryDto;
import com.fitai.dto.FoodItemDto;
import com.fitai.dto.MealEntryDto;
import com.fitai.dto.NutritionGoalDto;
import com.fitai.entity.FoodItem;
import com.fitai.entity.MealEntry;
import com.fitai.entity.NutritionGoal;
import com.fitai.entity.Role;
import com.fitai.entity.User;
import com.fitai.exception.ResourceNotFoundException;
import com.fitai.exception.UnauthorizedException;
import com.fitai.repository.FoodItemRepository;
import com.fitai.repository.MealEntryRepository;
import com.fitai.repository.NutritionGoalRepository;
import com.fitai.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NutritionService {

    private final FoodItemRepository foodItemRepository;
    private final MealEntryRepository mealEntryRepository;
    private final NutritionGoalRepository nutritionGoalRepository;
    private final UserRepository userRepository;

    public NutritionService(FoodItemRepository foodItemRepository,
                            MealEntryRepository mealEntryRepository,
                            NutritionGoalRepository nutritionGoalRepository,
                            UserRepository userRepository) {
        this.foodItemRepository = foodItemRepository;
        this.mealEntryRepository = mealEntryRepository;
        this.nutritionGoalRepository = nutritionGoalRepository;
        this.userRepository = userRepository;
    }

    // Food Catalog
    @Transactional(readOnly = true)
    public Page<FoodItemDto> getFoods(String category, String search, Pageable pageable) {
        return foodItemRepository.findWithFilters(
                (category != null && !category.isBlank()) ? category : null,
                (search != null && !search.isBlank()) ? search : null,
                pageable
        ).map(this::mapToFoodDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "foodItems", key = "#id")
    public FoodItemDto getFoodById(Long id) {
        FoodItem food = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + id));
        return mapToFoodDto(food);
    }

    @Transactional
    @CacheEvict(value = "foodItems", allEntries = true)
    public FoodItemDto createFood(FoodItemDto dto, User user) {
        FoodItem food = new FoodItem();
        food.setName(dto.getName());
        food.setServingSize(dto.getServingSize());
        food.setServingUnit(dto.getServingUnit());
        food.setCalories(dto.getCalories());
        food.setProteinGrams(dto.getProteinGrams());
        food.setCarbsGrams(dto.getCarbsGrams());
        food.setFatGrams(dto.getFatGrams());
        food.setCategory(dto.getCategory() != null ? dto.getCategory() : "GENERAL");
        food.setCustom(true);
        food.setCreatedByUser(user);

        FoodItem saved = foodItemRepository.save(food);
        return mapToFoodDto(saved);
    }

    @Transactional
    @CacheEvict(value = "foodItems", allEntries = true)
    public FoodItemDto updateFood(Long id, FoodItemDto dto, User user) {
        FoodItem food = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + id));

        if (user.getRole() != Role.ROLE_ADMIN && (food.getCreatedByUser() == null || !food.getCreatedByUser().getId().equals(user.getId()))) {
            throw new UnauthorizedException("You do not have permission to modify this food item");
        }

        food.setName(dto.getName());
        food.setServingSize(dto.getServingSize());
        food.setServingUnit(dto.getServingUnit());
        food.setCalories(dto.getCalories());
        food.setProteinGrams(dto.getProteinGrams());
        food.setCarbsGrams(dto.getCarbsGrams());
        food.setFatGrams(dto.getFatGrams());
        food.setCategory(dto.getCategory());

        FoodItem updated = foodItemRepository.save(food);
        return mapToFoodDto(updated);
    }

    @Transactional
    @CacheEvict(value = "foodItems", allEntries = true)
    public void deleteFood(Long id, User user) {
        FoodItem food = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + id));

        if (user.getRole() != Role.ROLE_ADMIN && (food.getCreatedByUser() == null || !food.getCreatedByUser().getId().equals(user.getId()))) {
            throw new UnauthorizedException("You do not have permission to delete this food item");
        }

        foodItemRepository.delete(food);
    }

    // Daily Nutrition & Meal Entries
    @Transactional(readOnly = true)
    public DailyNutritionSummaryDto getDailySummary(Long userId, LocalDate date) {
        LocalDate queryDate = date != null ? date : LocalDate.now();
        List<MealEntry> entries = mealEntryRepository.findByUserIdAndEntryDateOrderByCreatedAtAsc(userId, queryDate);

        double totalCalories = 0.0;
        double totalProtein = 0.0;
        double totalCarbs = 0.0;
        double totalFat = 0.0;

        for (MealEntry entry : entries) {
            totalCalories += entry.getCalculatedCalories();
            totalProtein += entry.getCalculatedProtein();
            totalCarbs += entry.getCalculatedCarbs();
            totalFat += entry.getCalculatedFat();
        }

        NutritionGoal goal = nutritionGoalRepository.findByUserId(userId)
                .orElseGet(() -> {
                    NutritionGoal defaultGoal = new NutritionGoal();
                    User user = userRepository.findById(userId).orElse(null);
                    defaultGoal.setUser(user);
                    return defaultGoal;
                });

        DailyNutritionSummaryDto summary = new DailyNutritionSummaryDto();
        summary.setDate(queryDate);
        summary.setTotalCalories(Math.round(totalCalories * 10.0) / 10.0);
        summary.setTotalProtein(Math.round(totalProtein * 10.0) / 10.0);
        summary.setTotalCarbs(Math.round(totalCarbs * 10.0) / 10.0);
        summary.setTotalFat(Math.round(totalFat * 10.0) / 10.0);

        summary.setTargetCalories(goal.getDailyCaloriesTarget());
        summary.setTargetProtein(goal.getDailyProteinTarget());
        summary.setTargetCarbs(goal.getDailyCarbsTarget());
        summary.setTargetFat(goal.getDailyFatTarget());
        summary.setTargetWaterMl(goal.getWaterIntakeMlTarget());

        summary.setMealEntries(entries.stream().map(this::mapToMealDto).collect(Collectors.toList()));
        return summary;
    }

    @Transactional
    public MealEntryDto logMeal(Long userId, MealEntryDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        FoodItem food = foodItemRepository.findById(dto.getFoodItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + dto.getFoodItemId()));

        MealEntry entry = new MealEntry();
        entry.setUser(user);
        entry.setFoodItem(food);
        entry.setMealType(dto.getMealType());
        entry.setEntryDate(dto.getEntryDate() != null ? dto.getEntryDate() : LocalDate.now());
        entry.setServings(dto.getServings() != null ? dto.getServings() : 1.0);
        entry.setNotes(dto.getNotes());
        entry.recalculateNutrition();

        MealEntry saved = mealEntryRepository.save(entry);
        return mapToMealDto(saved);
    }

    @Transactional
    public MealEntryDto updateMeal(Long userId, Long mealId, MealEntryDto dto) {
        MealEntry entry = mealEntryRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal entry not found with id: " + mealId));

        if (!entry.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this meal entry");
        }

        if (dto.getFoodItemId() != null && !dto.getFoodItemId().equals(entry.getFoodItem().getId())) {
            FoodItem food = foodItemRepository.findById(dto.getFoodItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + dto.getFoodItemId()));
            entry.setFoodItem(food);
        }

        if (dto.getMealType() != null) entry.setMealType(dto.getMealType());
        if (dto.getEntryDate() != null) entry.setEntryDate(dto.getEntryDate());
        if (dto.getServings() != null) entry.setServings(dto.getServings());
        if (dto.getNotes() != null) entry.setNotes(dto.getNotes());

        entry.recalculateNutrition();
        MealEntry updated = mealEntryRepository.save(entry);
        return mapToMealDto(updated);
    }

    @Transactional
    public void deleteMeal(Long userId, Long mealId) {
        MealEntry entry = mealEntryRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal entry not found with id: " + mealId));

        if (!entry.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this meal entry");
        }

        mealEntryRepository.delete(entry);
    }

    // Nutrition Goals
    @Transactional
    public NutritionGoalDto getNutritionGoal(Long userId) {
        NutritionGoal goal = nutritionGoalRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId).orElseThrow();
                    NutritionGoal defaultGoal = new NutritionGoal();
                    defaultGoal.setUser(user);
                    return nutritionGoalRepository.save(defaultGoal);
                });

        return mapToGoalDto(goal);
    }

    @Transactional
    public NutritionGoalDto updateNutritionGoal(Long userId, NutritionGoalDto dto) {
        NutritionGoal goal = nutritionGoalRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId).orElseThrow();
                    NutritionGoal defaultGoal = new NutritionGoal();
                    defaultGoal.setUser(user);
                    return defaultGoal;
                });

        if (dto.getDailyCaloriesTarget() != null) goal.setDailyCaloriesTarget(dto.getDailyCaloriesTarget());
        if (dto.getDailyProteinTarget() != null) goal.setDailyProteinTarget(dto.getDailyProteinTarget());
        if (dto.getDailyCarbsTarget() != null) goal.setDailyCarbsTarget(dto.getDailyCarbsTarget());
        if (dto.getDailyFatTarget() != null) goal.setDailyFatTarget(dto.getDailyFatTarget());
        if (dto.getWaterIntakeMlTarget() != null) goal.setWaterIntakeMlTarget(dto.getWaterIntakeMlTarget());

        NutritionGoal updated = nutritionGoalRepository.save(goal);
        return mapToGoalDto(updated);
    }

    public FoodItemDto mapToFoodDto(FoodItem f) {
        FoodItemDto dto = new FoodItemDto();
        dto.setId(f.getId());
        dto.setName(f.getName());
        dto.setServingSize(f.getServingSize());
        dto.setServingUnit(f.getServingUnit());
        dto.setCalories(f.getCalories());
        dto.setProteinGrams(f.getProteinGrams());
        dto.setCarbsGrams(f.getCarbsGrams());
        dto.setFatGrams(f.getFatGrams());
        dto.setCategory(f.getCategory());
        dto.setCustom(f.isCustom());
        return dto;
    }

    private MealEntryDto mapToMealDto(MealEntry m) {
        MealEntryDto dto = new MealEntryDto();
        dto.setId(m.getId());
        dto.setFoodItemId(m.getFoodItem().getId());
        dto.setFoodName(m.getFoodItem().getName());
        dto.setServingSize(m.getFoodItem().getServingSize());
        dto.setServingUnit(m.getFoodItem().getServingUnit());
        dto.setMealType(m.getMealType());
        dto.setEntryDate(m.getEntryDate());
        dto.setServings(m.getServings());
        dto.setCalculatedCalories(m.getCalculatedCalories());
        dto.setCalculatedProtein(m.getCalculatedProtein());
        dto.setCalculatedCarbs(m.getCalculatedCarbs());
        dto.setCalculatedFat(m.getCalculatedFat());
        dto.setNotes(m.getNotes());
        return dto;
    }

    private NutritionGoalDto mapToGoalDto(NutritionGoal g) {
        NutritionGoalDto dto = new NutritionGoalDto();
        dto.setId(g.getId());
        dto.setDailyCaloriesTarget(g.getDailyCaloriesTarget());
        dto.setDailyProteinTarget(g.getDailyProteinTarget());
        dto.setDailyCarbsTarget(g.getDailyCarbsTarget());
        dto.setDailyFatTarget(g.getDailyFatTarget());
        dto.setWaterIntakeMlTarget(g.getWaterIntakeMlTarget());
        return dto;
    }
}

