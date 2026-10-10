package com.fitai.repository;

import com.fitai.entity.MealEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {

    List<MealEntry> findByUserIdAndEntryDateOrderByCreatedAtAsc(Long userId, LocalDate entryDate);

    List<MealEntry> findByUserIdAndEntryDateBetweenOrderByEntryDateAsc(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(m) FROM MealEntry m WHERE m.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
}
