package com.fitai.repository;

import com.fitai.entity.FoodItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    @Query("SELECT f FROM FoodItem f WHERE " +
           "(:category IS NULL OR LOWER(f.category) = LOWER(:category)) AND " +
           "(:search IS NULL OR LOWER(f.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<FoodItem> findWithFilters(@Param("category") String category,
                                   @Param("search") String search,
                                   Pageable pageable);

    List<FoodItem> findByNameContainingIgnoreCase(String name);

    List<FoodItem> findByCategoryIgnoreCase(String category);
}
