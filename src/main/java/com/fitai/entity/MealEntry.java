package com.fitai.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "meal_entries", indexes = {
    @Index(name = "idx_meal_user_date", columnList = "user_id, entryDate")
})
public class MealEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;

    @Column(nullable = false, length = 30)
    private String mealType; // BREAKFAST, LUNCH, DINNER, SNACK

    @Column(nullable = false)
    private LocalDate entryDate = LocalDate.now();

    @Column(nullable = false)
    private Double servings = 1.0;

    @Column(nullable = false)
    private Double calculatedCalories = 0.0;

    @Column(nullable = false)
    private Double calculatedProtein = 0.0;

    @Column(nullable = false)
    private Double calculatedCarbs = 0.0;

    @Column(nullable = false)
    private Double calculatedFat = 0.0;

    @Column(length = 255)
    private String notes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public MealEntry() {}

    public void recalculateNutrition() {
        if (foodItem != null && servings != null) {
            this.calculatedCalories = Math.round(foodItem.getCalories() * servings * 10.0) / 10.0;
            this.calculatedProtein = Math.round(foodItem.getProteinGrams() * servings * 10.0) / 10.0;
            this.calculatedCarbs = Math.round(foodItem.getCarbsGrams() * servings * 10.0) / 10.0;
            this.calculatedFat = Math.round(foodItem.getFatGrams() * servings * 10.0) / 10.0;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public FoodItem getFoodItem() { return foodItem; }
    public void setFoodItem(FoodItem foodItem) {
        this.foodItem = foodItem;
        recalculateNutrition();
    }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

    public LocalDate getEntryDate() { return entryDate; }
    public void setEntryDate(LocalDate entryDate) { this.entryDate = entryDate; }

    public Double getServings() { return servings; }
    public void setServings(Double servings) {
        this.servings = servings;
        recalculateNutrition();
    }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
