package com.fitai.service;

import com.fitai.dto.ExerciseDto;
import com.fitai.entity.Exercise;
import com.fitai.entity.Role;
import com.fitai.entity.User;
import com.fitai.exception.ResourceNotFoundException;
import com.fitai.exception.UnauthorizedException;
import com.fitai.repository.ExerciseRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public ExerciseService(ExerciseRepository exerciseRepository) {
        this.exerciseRepository = exerciseRepository;
    }

    @Transactional(readOnly = true)
    public Page<ExerciseDto> getExercises(String muscleGroup, String difficulty, String equipment, String search, Pageable pageable) {
        return exerciseRepository.findWithFilters(
                (muscleGroup != null && !muscleGroup.isBlank()) ? muscleGroup : null,
                (difficulty != null && !difficulty.isBlank()) ? difficulty : null,
                (equipment != null && !equipment.isBlank()) ? equipment : null,
                (search != null && !search.isBlank()) ? search : null,
                pageable
        ).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "exercises", key = "#id")
    public ExerciseDto getExerciseById(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + id));
        return mapToDto(exercise);
    }

    @Transactional
    @CacheEvict(value = "exercises", allEntries = true)
    public ExerciseDto createExercise(ExerciseDto dto, User user) {
        Exercise exercise = new Exercise();
        exercise.setName(dto.getName());
        exercise.setDescription(dto.getDescription());
        exercise.setMuscleGroup(dto.getMuscleGroup());
        exercise.setEquipment(dto.getEquipment());
        exercise.setDifficulty(dto.getDifficulty());
        exercise.setExerciseType(dto.getExerciseType());
        exercise.setInstructions(dto.getInstructions());
        exercise.setSafetyTips(dto.getSafetyTips());
        exercise.setCustom(true);
        exercise.setCreatedByUser(user);

        Exercise saved = exerciseRepository.save(exercise);
        return mapToDto(saved);
    }

    @Transactional
    @CacheEvict(value = "exercises", allEntries = true)
    public ExerciseDto updateExercise(Long id, ExerciseDto dto, User user) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + id));

        // Enforce ownership: only ADMIN or creator can edit
        if (user.getRole() != Role.ROLE_ADMIN && (exercise.getCreatedByUser() == null || !exercise.getCreatedByUser().getId().equals(user.getId()))) {
            throw new UnauthorizedException("You do not have permission to modify this exercise");
        }

        exercise.setName(dto.getName());
        exercise.setDescription(dto.getDescription());
        exercise.setMuscleGroup(dto.getMuscleGroup());
        exercise.setEquipment(dto.getEquipment());
        exercise.setDifficulty(dto.getDifficulty());
        exercise.setExerciseType(dto.getExerciseType());
        exercise.setInstructions(dto.getInstructions());
        exercise.setSafetyTips(dto.getSafetyTips());

        Exercise updated = exerciseRepository.save(exercise);
        return mapToDto(updated);
    }

    @Transactional
    @CacheEvict(value = "exercises", allEntries = true)
    public void deleteExercise(Long id, User user) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + id));

        if (user.getRole() != Role.ROLE_ADMIN && (exercise.getCreatedByUser() == null || !exercise.getCreatedByUser().getId().equals(user.getId()))) {
            throw new UnauthorizedException("You do not have permission to delete this exercise");
        }

        exerciseRepository.delete(exercise);
    }

    public ExerciseDto mapToDto(Exercise e) {
        ExerciseDto dto = new ExerciseDto();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setDescription(e.getDescription());
        dto.setMuscleGroup(e.getMuscleGroup());
        dto.setEquipment(e.getEquipment());
        dto.setDifficulty(e.getDifficulty());
        dto.setExerciseType(e.getExerciseType());
        dto.setInstructions(e.getInstructions());
        dto.setSafetyTips(e.getSafetyTips());
        dto.setCustom(e.isCustom());
        return dto;
    }
}
