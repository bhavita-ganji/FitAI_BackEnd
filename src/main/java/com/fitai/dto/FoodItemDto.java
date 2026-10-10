package com.fitai.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FoodItemDto {

    private Long id;

    @NotBlank(message = "Food name is required")
    private String name;

    @NotNull(message = "Serving size is required")
    @Min(value = 1, message = "Serving size must be at least 1")
    private Double servingSize = 100.0;

    @NotBlank(message = "Serving unit is required")
    private String servingUnit = "g";

    @NotNull(message = "Calories is required")
    @Min(value = 0, message = "Calories cannot be negative")
    private Double calories = 0.0;

    @NotNull(message = "Protein is required")
    @Min(value = 0, message = "Protein cannot be negative")
    private Double proteinGrams = 0.0;

    @NotNull(message = "Carbs is required")
    @Min(value = 0, message = "Carbs cannot be negative")
    private Double carbsGrams = 0.0;

    @NotNull(message = "Fat is required")
    @Min(value = 0, message = "Fat cannot be negative")
    private Double fatGrams = 0.0;

    private String category = "GENERAL";
    private boolean isCustom;

    public FoodItemDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getServingSize() { return servingSize; }
    public void setServingSize(Double servingSize) { this.servingSize = servingSize; }

    public String getServingUnit() { return servingUnit; }
    public void setServingUnit(String servingUnit) { this.servingUnit = servingUnit; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }

    public Double getProteinGrams() { return proteinGrams; }
    public void setProteinGrams(Double proteinGrams) { this.proteinGrams = proteinGrams; }

    public Double getCarbsGrams() { return carbsGrams; }
    public void setCarbsGrams(Double carbsGrams) { this.carbsGrams = carbsGrams; }

    public Double getFatGrams() { return fatGrams; }
    public void setFatGrams(Double fatGrams) { this.fatGrams = fatGrams; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isCustom() { return isCustom; }
    public void setCustom(boolean custom) { isCustom = custom; }
}
