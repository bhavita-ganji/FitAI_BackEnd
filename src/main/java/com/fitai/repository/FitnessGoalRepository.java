package com.fitai.repository;

import com.fitai.entity.FitnessGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FitnessGoalRepository extends JpaRepository<FitnessGoal, Long> {
    List<FitnessGoal> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<FitnessGoal> findByUserIdAndCompletedFalse(Long userId);
}
