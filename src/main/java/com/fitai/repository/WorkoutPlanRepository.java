package com.fitai.repository;

import com.fitai.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<WorkoutPlan> findByIsTemplateTrue();
    List<WorkoutPlan> findByUserIdOrIsTemplateTrueOrderByCreatedAtDesc(Long userId);
}
