package com.fitai.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class MealEntryDto {

    private Long id;

    @NotNull(message = "Food item ID is required")
    private Long foodItemId;

    private String foodName;
    private Double servingSize;
    private String servingUnit;

    @NotBlank(message = "Meal type is required")
    private String mealType; // BREAKFAST, LUNCH, DINNER, SNACK

    @NotNull(message = "Entry date is required")
    private LocalDate entryDate = LocalDate.now();

    @NotNull(message = "Servings is required")
    @Min(value = 0, message = "Servings must be positive")
    private Double servings = 1.0;

    private Double calculatedCalories;
    private Double calculatedProtein;
    private Double calculatedCarbs;
    private Double calculatedFat;
    private String notes;

    public MealEntryDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFoodItemId() { return foodItemId; }
    public void setFoodItemId(Long foodItemId) { this.foodItemId = foodItemId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public Double getServingSize() { return servingSize; }
    public void setServingSize(Double servingSize) { this.servingSize = servingSize; }

    public String getServingUnit() { return servingUnit; }
    public void setServingUnit(String servingUnit) { this.servingUnit = servingUnit; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }

    public Double getServings() { return servings; }
    public void setServings(Double servings) { this.servings = servings; }

    public Double getCalculatedCalories() { return calculatedCalories; }
    public void setCalculatedCalories(Double calculatedCalories) { this.calculatedCalories = calculatedCalories; }

    public Double getCalculatedProtein() { return calculatedProtein; }
    public void setCalculatedProtein(Double calculatedProtein) { this.calculatedProtein = calculatedProtein; }

    public Double getCalculatedCarbs() { return calculatedCarbs; }
    public void setCalculatedCarbs(Double calculatedCarbs) { this.calculatedCarbs = calculatedCarbs; }

    public Double getCalculatedFat() { return calculatedFat; }
    public void setCalculatedFat(Double calculatedFat) { this.calculatedFat = calculatedFat; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
