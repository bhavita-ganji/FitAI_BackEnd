package com.fitai.service;

import com.fitai.dto.WorkoutExerciseLogDto;
import com.fitai.dto.WorkoutPlanDto;
import com.fitai.dto.WorkoutPlanExerciseDto;
import com.fitai.dto.WorkoutSessionDto;
import com.fitai.entity.*;
import com.fitai.exception.ResourceNotFoundException;
import com.fitai.exception.UnauthorizedException;
import com.fitai.repository.ExerciseRepository;
import com.fitai.repository.UserRepository;
import com.fitai.repository.WorkoutPlanRepository;
import com.fitai.repository.WorkoutSessionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    public WorkoutService(WorkoutPlanRepository workoutPlanRepository,
                          WorkoutSessionRepository workoutSessionRepository,
                          ExerciseRepository exerciseRepository,
                          UserRepository userRepository) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.exerciseRepository = exerciseRepository;
        this.userRepository = userRepository;
    }

    // Workout Plans
    @Transactional(readOnly = true)
    public List<WorkoutPlanDto> getUserAndTemplatePlans(Long userId) {
        return workoutPlanRepository.findByUserIdOrIsTemplateTrueOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToPlanDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkoutPlanDto getPlanById(Long planId, Long userId) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout plan not found with id: " + planId));

        if (!plan.isTemplate() && (plan.getUser() == null || !plan.getUser().getId().equals(userId))) {
            throw new UnauthorizedException("You do not have permission to view this plan");
        }

        return mapToPlanDto(plan);
    }

    @Transactional
    public WorkoutPlanDto createWorkoutPlan(Long userId, WorkoutPlanDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        WorkoutPlan plan = new WorkoutPlan();
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setDifficulty(dto.getDifficulty());
        plan.setDaysPerWeek(dto.getDaysPerWeek());
        plan.setUser(user);
        plan.setTemplate(false);

        if (dto.getPlanExercises() != null) {
            int order = 0;
            for (WorkoutPlanExerciseDto peDto : dto.getPlanExercises()) {
                Exercise exercise = exerciseRepository.findById(peDto.getExerciseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + peDto.getExerciseId()));

                WorkoutPlanExercise planExercise = new WorkoutPlanExercise(
                        plan,
                        exercise,
                        peDto.getTargetSets() != null ? peDto.getTargetSets() : 3,
                        peDto.getTargetReps() != null ? peDto.getTargetReps() : 10,
                        peDto.getRestSeconds() != null ? peDto.getRestSeconds() : 60,
                        peDto.getOrderIndex() != null ? peDto.getOrderIndex() : order++
                );
                plan.addPlanExercise(planExercise);
            }
        }

        WorkoutPlan saved = workoutPlanRepository.save(plan);
        return mapToPlanDto(saved);
    }

    @Transactional
    public WorkoutPlanDto updateWorkoutPlan(Long userId, Long planId, WorkoutPlanDto dto) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout plan not found with id: " + planId));

        if (plan.getUser() == null || !plan.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this workout plan");
        }

        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setDifficulty(dto.getDifficulty());
        plan.setDaysPerWeek(dto.getDaysPerWeek());

        plan.getPlanExercises().clear();
        if (dto.getPlanExercises() != null) {
            int order = 0;
            for (WorkoutPlanExerciseDto peDto : dto.getPlanExercises()) {
                Exercise exercise = exerciseRepository.findById(peDto.getExerciseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + peDto.getExerciseId()));

                WorkoutPlanExercise planExercise = new WorkoutPlanExercise(
                        plan,
                        exercise,
                        peDto.getTargetSets() != null ? peDto.getTargetSets() : 3,
                        peDto.getTargetReps() != null ? peDto.getTargetReps() : 10,
                        peDto.getRestSeconds() != null ? peDto.getRestSeconds() : 60,
                        peDto.getOrderIndex() != null ? peDto.getOrderIndex() : order++
                );
                plan.addPlanExercise(planExercise);
            }
        }

        WorkoutPlan updated = workoutPlanRepository.save(plan);
        return mapToPlanDto(updated);
    }

    @Transactional
    public void deleteWorkoutPlan(Long userId, Long planId) {
        WorkoutPlan plan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout plan not found with id: " + planId));

        if (plan.getUser() == null || !plan.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this workout plan");
        }

        workoutPlanRepository.delete(plan);
    }

    // Workout Sessions & Logging
    @Transactional(readOnly = true)
    public Page<WorkoutSessionDto> getSessions(Long userId, Pageable pageable) {
        return workoutSessionRepository.findByUserIdOrderBySessionDateDesc(userId, pageable)
                .map(this::mapToSessionDto);
    }

    @Transactional(readOnly = true)
    public List<WorkoutSessionDto> getSessionsForDateRange(Long userId, LocalDate start, LocalDate end) {
        return workoutSessionRepository.findByUserIdAndSessionDateBetweenOrderBySessionDateAsc(userId, start, end).stream()
                .map(this::mapToSessionDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkoutSessionDto getSessionById(Long sessionId, Long userId) {
        WorkoutSession session = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout session not found with id: " + sessionId));

        if (!session.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this workout session");
        }

        return mapToSessionDto(session);
    }

    @Transactional
    public WorkoutSessionDto logSession(Long userId, WorkoutSessionDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        WorkoutSession session = new WorkoutSession();
        session.setUser(user);
        session.setSessionName(dto.getSessionName());
        session.setSessionDate(dto.getSessionDate() != null ? dto.getSessionDate() : LocalDate.now());
        session.setDurationMinutes(dto.getDurationMinutes() != null ? dto.getDurationMinutes() : 45);
        session.setCaloriesBurned(dto.getCaloriesBurned() != null ? dto.getCaloriesBurned() : (session.getDurationMinutes() * 7));
        session.setNotes(dto.getNotes());
        session.setStatus(dto.getStatus() != null ? dto.getStatus() : "COMPLETED");

        if (dto.getWorkoutPlanId() != null) {
            workoutPlanRepository.findById(dto.getWorkoutPlanId()).ifPresent(session::setWorkoutPlan);
        }

        if (dto.getExerciseLogs() != null) {
            for (WorkoutExerciseLogDto logDto : dto.getExerciseLogs()) {
                Exercise exercise = exerciseRepository.findById(logDto.getExerciseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + logDto.getExerciseId()));

                WorkoutExerciseLog log = new WorkoutExerciseLog(
                        session,
                        exercise,
                        logDto.getSetNumber() != null ? logDto.getSetNumber() : 1,
                        logDto.getReps() != null ? logDto.getReps() : 10,
                        logDto.getWeightKg() != null ? logDto.getWeightKg() : 0.0,
                        logDto.isCompleted()
                );
                session.addExerciseLog(log);
            }
        }

        WorkoutSession saved = workoutSessionRepository.save(session);
        return mapToSessionDto(saved);
    }

    @Transactional
    public void deleteSession(Long userId, Long sessionId) {
        WorkoutSession session = workoutSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Workout session not found with id: " + sessionId));

        if (!session.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this workout session");
        }

        workoutSessionRepository.delete(session);
    }

    private WorkoutPlanDto mapToPlanDto(WorkoutPlan p) {
        WorkoutPlanDto dto = new WorkoutPlanDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setDifficulty(p.getDifficulty());
        dto.setDaysPerWeek(p.getDaysPerWeek());
        dto.setTemplate(p.isTemplate());

        List<WorkoutPlanExerciseDto> exDtos = new ArrayList<>();
        if (p.getPlanExercises() != null) {
            for (WorkoutPlanExercise pe : p.getPlanExercises()) {
                WorkoutPlanExerciseDto peDto = new WorkoutPlanExerciseDto();
                peDto.setId(pe.getId());
                peDto.setExerciseId(pe.getExercise().getId());
                peDto.setExerciseName(pe.getExercise().getName());
                peDto.setMuscleGroup(pe.getExercise().getMuscleGroup());
                peDto.setEquipment(pe.getExercise().getEquipment());
                peDto.setTargetSets(pe.getTargetSets());
                peDto.setTargetReps(pe.getTargetReps());
                peDto.setRestSeconds(pe.getRestSeconds());
                peDto.setOrderIndex(pe.getOrderIndex());
                exDtos.add(peDto);
            }
        }
        dto.setPlanExercises(exDtos);
        return dto;
    }

    private WorkoutSessionDto mapToSessionDto(WorkoutSession s) {
        WorkoutSessionDto dto = new WorkoutSessionDto();
        dto.setId(s.getId());
        dto.setSessionName(s.getSessionName());
        dto.setSessionDate(s.getSessionDate());
        dto.setDurationMinutes(s.getDurationMinutes());
        dto.setCaloriesBurned(s.getCaloriesBurned());
        dto.setNotes(s.getNotes());
        dto.setStatus(s.getStatus());

        if (s.getWorkoutPlan() != null) {
            dto.setWorkoutPlanId(s.getWorkoutPlan().getId());
            dto.setWorkoutPlanName(s.getWorkoutPlan().getName());
        }

        List<WorkoutExerciseLogDto> logs = new ArrayList<>();
        if (s.getExerciseLogs() != null) {
            for (WorkoutExerciseLog log : s.getExerciseLogs()) {
                WorkoutExerciseLogDto logDto = new WorkoutExerciseLogDto();
                logDto.setId(log.getId());
                logDto.setExerciseId(log.getExercise().getId());
                logDto.setExerciseName(log.getExercise().getName());
                logDto.setMuscleGroup(log.getExercise().getMuscleGroup());
                logDto.setSetNumber(log.getSetNumber());
                logDto.setReps(log.getReps());
                logDto.setWeightKg(log.getWeightKg());
                logDto.setCompleted(log.isCompleted());
                logs.add(logDto);
            }
        }
        dto.setExerciseLogs(logs);
        return dto;
    }
}
