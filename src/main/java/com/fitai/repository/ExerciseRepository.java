package com.fitai.repository;

import com.fitai.entity.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    @Query("SELECT e FROM Exercise e WHERE " +
           "(:muscleGroup IS NULL OR LOWER(e.muscleGroup) = LOWER(:muscleGroup)) AND " +
           "(:difficulty IS NULL OR LOWER(e.difficulty) = LOWER(:difficulty)) AND " +
           "(:equipment IS NULL OR LOWER(e.equipment) = LOWER(:equipment)) AND " +
           "(:search IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Exercise> findWithFilters(@Param("muscleGroup") String muscleGroup,
                                   @Param("difficulty") String difficulty,
                                   @Param("equipment") String equipment,
                                   @Param("search") String search,
                                   Pageable pageable);

    List<Exercise> findByMuscleGroupIgnoreCase(String muscleGroup);

    List<Exercise> findByNameContainingIgnoreCase(String name);
}
