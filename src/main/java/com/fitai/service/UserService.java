package com.fitai.service;

import com.fitai.dto.FitnessGoalDto;
import com.fitai.dto.UserProfileDto;
import com.fitai.entity.FitnessGoal;
import com.fitai.entity.User;
import com.fitai.entity.UserProfile;
import com.fitai.exception.ResourceNotFoundException;
import com.fitai.exception.UnauthorizedException;
import com.fitai.repository.FitnessGoalRepository;
import com.fitai.repository.UserProfileRepository;
import com.fitai.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final FitnessGoalRepository fitnessGoalRepository;

    public UserService(UserRepository userRepository,
                       UserProfileRepository userProfileRepository,
                       FitnessGoalRepository fitnessGoalRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.fitnessGoalRepository = fitnessGoalRepository;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Transactional(readOnly = true)
    public UserProfileDto getProfile(Long userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = getUserById(userId);
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return userProfileRepository.save(newProfile);
                });

        return mapToProfileDto(profile);
    }

    @Transactional
    public UserProfileDto updateProfile(Long userId, UserProfileDto dto) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = getUserById(userId);
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(user);
                    return newProfile;
                });

        if (dto.getAge() != null) profile.setAge(dto.getAge());
        if (dto.getGender() != null) profile.setGender(dto.getGender());
        if (dto.getHeightCm() != null) profile.setHeightCm(dto.getHeightCm());
        if (dto.getWeightKg() != null) profile.setWeightKg(dto.getWeightKg());
        if (dto.getTargetWeightKg() != null) profile.setTargetWeightKg(dto.getTargetWeightKg());
        if (dto.getActivityLevel() != null) profile.setActivityLevel(dto.getActivityLevel());
        if (dto.getExperienceLevel() != null) profile.setExperienceLevel(dto.getExperienceLevel());
        if (dto.getPreferredWorkoutDurationMinutes() != null) profile.setPreferredWorkoutDurationMinutes(dto.getPreferredWorkoutDurationMinutes());
        if (dto.getPrimaryGoal() != null) profile.setPrimaryGoal(dto.getPrimaryGoal());
        if (dto.getDietaryPreferences() != null) profile.setDietaryPreferences(dto.getDietaryPreferences());
        if (dto.getHealthNotes() != null) profile.setHealthNotes(dto.getHealthNotes());

        UserProfile updated = userProfileRepository.save(profile);
        return mapToProfileDto(updated);
    }

    @Transactional(readOnly = true)
    public List<FitnessGoalDto> getGoals(Long userId) {
        return fitnessGoalRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToGoalDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public FitnessGoalDto createGoal(Long userId, FitnessGoalDto dto) {
        User user = getUserById(userId);
        FitnessGoal goal = new FitnessGoal();
        goal.setUser(user);
        goal.setGoalType(dto.getGoalType());
        goal.setTitle(dto.getTitle());
        goal.setDescription(dto.getDescription());
        goal.setTargetValue(dto.getTargetValue());
        goal.setCurrentValue(dto.getCurrentValue());
        goal.setMetricUnit(dto.getMetricUnit());
        goal.setTargetDate(dto.getTargetDate());
        goal.setCompleted(dto.isCompleted());

        FitnessGoal saved = fitnessGoalRepository.save(goal);
        return mapToGoalDto(saved);
    }

    @Transactional
    public FitnessGoalDto updateGoal(Long userId, Long goalId, FitnessGoalDto dto) {
        FitnessGoal goal = fitnessGoalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));

        if (!goal.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this fitness goal");
        }

        if (dto.getGoalType() != null) goal.setGoalType(dto.getGoalType());
        if (dto.getTitle() != null) goal.setTitle(dto.getTitle());
        if (dto.getDescription() != null) goal.setDescription(dto.getDescription());
        if (dto.getTargetValue() != null) goal.setTargetValue(dto.getTargetValue());
        if (dto.getCurrentValue() != null) goal.setCurrentValue(dto.getCurrentValue());
        if (dto.getMetricUnit() != null) goal.setMetricUnit(dto.getMetricUnit());
        if (dto.getTargetDate() != null) goal.setTargetDate(dto.getTargetDate());
        goal.setCompleted(dto.isCompleted());

        FitnessGoal updated = fitnessGoalRepository.save(goal);
        return mapToGoalDto(updated);
    }

    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        FitnessGoal goal = fitnessGoalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));

        if (!goal.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this fitness goal");
        }

        fitnessGoalRepository.delete(goal);
    }

    private UserProfileDto mapToProfileDto(UserProfile p) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(p.getId());
        dto.setAge(p.getAge());
        dto.setGender(p.getGender());
        dto.setHeightCm(p.getHeightCm());
        dto.setWeightKg(p.getWeightKg());
        dto.setTargetWeightKg(p.getTargetWeightKg());
        dto.setActivityLevel(p.getActivityLevel());
        dto.setExperienceLevel(p.getExperienceLevel());
        dto.setPreferredWorkoutDurationMinutes(p.getPreferredWorkoutDurationMinutes());
        dto.setPrimaryGoal(p.getPrimaryGoal());
        dto.setDietaryPreferences(p.getDietaryPreferences());
        dto.setHealthNotes(p.getHealthNotes());
        return dto;
    }

    private FitnessGoalDto mapToGoalDto(FitnessGoal g) {
        FitnessGoalDto dto = new FitnessGoalDto();
        dto.setId(g.getId());
        dto.setGoalType(g.getGoalType());
        dto.setTitle(g.getTitle());
        dto.setDescription(g.getDescription());
        dto.setTargetValue(g.getTargetValue());
        dto.setCurrentValue(g.getCurrentValue());
        dto.setMetricUnit(g.getMetricUnit());
        dto.setTargetDate(g.getTargetDate());
        dto.setCompleted(g.isCompleted());
        return dto;
    }
}
