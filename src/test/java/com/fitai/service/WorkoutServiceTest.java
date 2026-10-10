package com.fitai.service;

import com.fitai.dto.WorkoutPlanDto;
import com.fitai.entity.Role;
import com.fitai.entity.User;
import com.fitai.entity.WorkoutPlan;
import com.fitai.exception.UnauthorizedException;
import com.fitai.repository.ExerciseRepository;
import com.fitai.repository.UserRepository;
import com.fitai.repository.WorkoutPlanRepository;
import com.fitai.repository.WorkoutSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutServiceTest {

    @Mock
    private WorkoutPlanRepository workoutPlanRepository;

    @Mock
    private WorkoutSessionRepository workoutSessionRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WorkoutService workoutService;

    private User userA;
    private User userB;
    private WorkoutPlan planA;

    @BeforeEach
    void setUp() {
        userA = new User("alice@fitai.com", "hash", "Alice", Role.ROLE_USER);
        userA.setId(1L);

        userB = new User("bob@fitai.com", "hash", "Bob", Role.ROLE_USER);
        userB.setId(2L);

        planA = new WorkoutPlan();
        planA.setId(100L);
        planA.setName("Alice's Push Day");
        planA.setUser(userA);
        planA.setTemplate(false);
    }

    @Test
    void testCreateWorkoutPlanSuccess() {
        WorkoutPlanDto dto = new WorkoutPlanDto();
        dto.setName("Leg Hypertrophy");
        dto.setDaysPerWeek(2);
        dto.setDifficulty("INTERMEDIATE");

        when(userRepository.findById(1L)).thenReturn(Optional.of(userA));
        when(workoutPlanRepository.save(any(WorkoutPlan.class))).thenAnswer(invocation -> {
            WorkoutPlan p = invocation.getArgument(0);
            p.setId(101L);
            return p;
        });

        WorkoutPlanDto created = workoutService.createWorkoutPlan(1L, dto);

        assertNotNull(created);
        assertEquals("Leg Hypertrophy", created.getName());
        verify(workoutPlanRepository, times(1)).save(any(WorkoutPlan.class));
    }

    @Test
    void testUserCannotDeleteAnotherUsersPlanThrowsUnauthorized() {
        when(workoutPlanRepository.findById(100L)).thenReturn(Optional.of(planA));

        // User B tries to delete User A's plan
        assertThrows(UnauthorizedException.class, () -> workoutService.deleteWorkoutPlan(2L, 100L));
        verify(workoutPlanRepository, never()).delete(any());
    }

    @Test
    void testOwnerCanDeleteOwnPlan() {
        when(workoutPlanRepository.findById(100L)).thenReturn(Optional.of(planA));

        workoutService.deleteWorkoutPlan(1L, 100L);
        verify(workoutPlanRepository, times(1)).delete(planA);
    }
}
