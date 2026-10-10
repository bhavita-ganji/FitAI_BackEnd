package com.fitai.controller;

import com.fitai.dto.*;
import com.fitai.entity.User;
import com.fitai.security.UserPrincipal;
import com.fitai.service.NutritionService;
import com.fitai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/nutrition")
@Tag(name = "Nutrition & Meal Planner", description = "Endpoints for food database, meal logging, and daily macronutrient tracking")
public class NutritionController {

    private final NutritionService nutritionService;
    private final UserService userService;

    public NutritionController(NutritionService nutritionService, UserService userService) {
        this.nutritionService = nutritionService;
        this.userService = userService;
    }

    // Food Database
    @GetMapping("/foods")
    @Operation(summary = "Search and filter nutritional food items with pagination")
    public ResponseEntity<ApiResponse<Page<FoodItemDto>>> getFoods(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<FoodItemDto> foods = nutritionService.getFoods(category, search, PageRequest.of(page, size, Sort.by("name").ascending()));
        return ResponseEntity.ok(ApiResponse.ok(foods));
    }

    @GetMapping("/foods/{id}")
    @Operation(summary = "Get nutritional details for a specific food item")
    public ResponseEntity<ApiResponse<FoodItemDto>> getFoodById(@PathVariable Long id) {
        FoodItemDto food = nutritionService.getFoodById(id);
        return ResponseEntity.ok(ApiResponse.ok(food));
    }

    @PostMapping("/foods")
    @Operation(summary = "Create a custom food item in database")
    public ResponseEntity<ApiResponse<FoodItemDto>> createFood(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody FoodItemDto dto) {
        User user = userService.getUserById(principal.getId());
        FoodItemDto created = nutritionService.createFood(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Food item created", created));
    }

    @PutMapping("/foods/{id}")
    @Operation(summary = "Update a food item")
    public ResponseEntity<ApiResponse<FoodItemDto>> updateFood(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody FoodItemDto dto) {
        User user = userService.getUserById(principal.getId());
        FoodItemDto updated = nutritionService.updateFood(id, dto, user);
        return ResponseEntity.ok(ApiResponse.ok("Food item updated", updated));
    }

    @DeleteMapping("/foods/{id}")
    @Operation(summary = "Delete a food item")
    public ResponseEntity<ApiResponse<Void>> deleteFood(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        User user = userService.getUserById(principal.getId());
        nutritionService.deleteFood(id, user);
        return ResponseEntity.ok(ApiResponse.ok("Food item deleted", null));
    }

    // Daily Summary & Meal Logging
    @GetMapping("/summary")
    @Operation(summary = "Get daily nutrition summary including macros and logged meals")
    public ResponseEntity<ApiResponse<DailyNutritionSummaryDto>> getDailySummary(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        DailyNutritionSummaryDto summary = nutritionService.getDailySummary(principal.getId(), date);
        return ResponseEntity.ok(ApiResponse.ok(summary));
    }

    @PostMapping("/meals")
    @Operation(summary = "Log a meal with food item and servings")
    public ResponseEntity<ApiResponse<MealEntryDto>> logMeal(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody MealEntryDto dto) {
        MealEntryDto logged = nutritionService.logMeal(principal.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Meal logged successfully", logged));
    }

    @PutMapping("/meals/{id}")
    @Operation(summary = "Update a logged meal entry")
    public ResponseEntity<ApiResponse<MealEntryDto>> updateMeal(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody MealEntryDto dto) {
        MealEntryDto updated = nutritionService.updateMeal(principal.getId(), id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Meal updated successfully", updated));
    }

    @DeleteMapping("/meals/{id}")
    @Operation(summary = "Delete a logged meal entry")
    public ResponseEntity<ApiResponse<Void>> deleteMeal(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        nutritionService.deleteMeal(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Meal deleted successfully", null));
    }

    // Targets & Goals
    @GetMapping("/goals")
    @Operation(summary = "Get user's daily macronutrient targets")
    public ResponseEntity<ApiResponse<NutritionGoalDto>> getGoals(@AuthenticationPrincipal UserPrincipal principal) {
        NutritionGoalDto goal = nutritionService.getNutritionGoal(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(goal));
    }

    @PutMapping("/goals")
    @Operation(summary = "Update user's daily macronutrient targets")
    public ResponseEntity<ApiResponse<NutritionGoalDto>> updateGoals(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody NutritionGoalDto dto) {
        NutritionGoalDto updated = nutritionService.updateNutritionGoal(principal.getId(), dto);
        return ResponseEntity.ok(ApiResponse.ok("Nutrition targets updated", updated));
    }
}
