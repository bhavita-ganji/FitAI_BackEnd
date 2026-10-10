package com.fitai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_items", indexes = {
    @Index(name = "idx_food_name", columnList = "name"),
    @Index(name = "idx_food_category", columnList = "category")
})
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false)
    private Double servingSize = 100.0;

    @Column(nullable = false, length = 30)
    private String servingUnit = "g"; // g, ml, piece, cup, oz

    @Column(nullable = false)
    private Double calories = 0.0;

    @Column(nullable = false)
    private Double proteinGrams = 0.0;

    @Column(nullable = false)
    private Double carbsGrams = 0.0;

    @Column(nullable = false)
    private Double fatGrams = 0.0;

    @Column(length = 50)
    private String category = "GENERAL"; // PROTEIN, CARBS, VEGETABLES, FRUITS, DAIRY, HEALTHY_FATS, SNACKS, BEVERAGES

    private boolean isCustom = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public FoodItem() {}

    public FoodItem(String name, Double servingSize, String servingUnit, Double calories, Double proteinGrams, Double carbsGrams, Double fatGrams, String category) {
        this.name = name;
        this.servingSize = servingSize;
        this.servingUnit = servingUnit;
        this.calories = calories;
        this.proteinGrams = proteinGrams;
        this.carbsGrams = carbsGrams;
        this.fatGrams = fatGrams;
        this.category = category;
    }

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

    public User getCreatedByUser() { return createdByUser; }
    public void setCreatedByUser(User createdByUser) { this.createdByUser = createdByUser; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
