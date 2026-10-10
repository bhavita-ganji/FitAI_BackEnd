package com.fitai.service;

import com.fitai.dto.AdminStatsDto;
import com.fitai.entity.Role;
import com.fitai.entity.User;
import com.fitai.exception.BadRequestException;
import com.fitai.exception.ResourceNotFoundException;
import com.fitai.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final WorkoutSessionRepository workoutSessionRepository;
    private final MealEntryRepository mealEntryRepository;
    private final ExerciseRepository exerciseRepository;
    private final KnowledgeDocumentRepository knowledgeRepository;

    public AdminService(UserRepository userRepository,
                        WorkoutSessionRepository workoutSessionRepository,
                        MealEntryRepository mealEntryRepository,
                        ExerciseRepository exerciseRepository,
                        KnowledgeDocumentRepository knowledgeRepository) {
        this.userRepository = userRepository;
        this.workoutSessionRepository = workoutSessionRepository;
        this.mealEntryRepository = mealEntryRepository;
        this.exerciseRepository = exerciseRepository;
        this.knowledgeRepository = knowledgeRepository;
    }

    @Transactional(readOnly = true)
    public AdminStatsDto getSystemStats() {
        long usersCount = userRepository.count();
        long workoutsCount = workoutSessionRepository.countAllCompletedSessions();
        long mealsCount = mealEntryRepository.count();
        long exercisesCount = exerciseRepository.count();
        long knowledgeCount = knowledgeRepository.count();

        return new AdminStatsDto(
                usersCount,
                workoutsCount,
                mealsCount,
                exercisesCount,
                knowledgeCount,
                usersCount
        );
    }

    @Transactional(readOnly = true)
    public Page<Map<String, Object>> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(user -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("email", user.getEmail());
            map.put("fullName", user.getFullName());
            map.put("role", user.getRole().name());
            map.put("enabled", user.isEnabled());
            map.put("createdAt", user.getCreatedAt());
            map.put("updatedAt", user.getUpdatedAt());
            return map;
        });
    }

    @Transactional
    public Map<String, Object> toggleUserRole(Long userId, Long currentAdminId) {
        if (userId.equals(currentAdminId)) {
            throw new BadRequestException("You cannot change your own administrative role");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role newRole = user.getRole() == Role.ROLE_ADMIN ? Role.ROLE_USER : Role.ROLE_ADMIN;
        user.setRole(newRole);
        User updated = userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", updated.getId());
        result.put("newRole", updated.getRole().name());
        return result;
    }

    @Transactional
    public Map<String, Object> toggleUserStatus(Long userId, Long currentAdminId) {
        if (userId.equals(currentAdminId)) {
            throw new BadRequestException("You cannot disable your own administrator account");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setEnabled(!user.isEnabled());
        User updated = userRepository.save(user);

        Map<String, Object> result = new HashMap<>();
        result.put("userId", updated.getId());
        result.put("enabled", updated.isEnabled());
        return result;
    }
}
