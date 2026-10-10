package com.fitai.controller;

import com.fitai.dto.ApiResponse;
import com.fitai.dto.WorkoutPlanDto;
import com.fitai.dto.WorkoutSessionDto;
import com.fitai.security.UserPrincipal;
import com.fitai.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/workouts")
@Tag(name = "Workouts & Training", description = "Endpoints for managing workout plans, routines, and logging completed sessions")
public class WorkoutController {

    private final WorkoutService workoutService;

    public WorkoutController(WorkoutService workoutService) {
        this.workoutService = workoutService;
    }

    // Workout Plans
    @GetMapping("/plans")
    @Operation(summary = "Get user's personalized workout plans and system templates")
    public ResponseEntity<ApiResponse<List<WorkoutPlanDto>>> getPlans(@AuthenticationPrincipal UserPrincipal principal) {
        List<WorkoutPlanDto> plans = workoutService.getUserAndTemplatePlans(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(plans));
    }

    @GetMapping("/plans/{id}")
    @Operation(summary = "Get workout plan details by ID")
    public ResponseEntity<ApiResponse<WorkoutPlanDto>> getPlanById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        WorkoutPlanDto plan = workoutService.getPlanById(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(plan));
    }

    @PostMapping("/plans")
    @Operation(summary = "Create a custom workout plan")
    public ResponseEntity<ApiResponse<WorkoutPlanDto>> createPlan(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WorkoutPlanDto dto) {
        WorkoutPlanDto created = workoutService.createWorkoutPlan(principal.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Workout plan created", created));
    }

    @PutMapping("/plans/{id}")
    @Operation(summary = "Update an existing workout plan")
    public ResponseEntity<ApiResponse<WorkoutPlanDto>> updatePlan(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody WorkoutPlanDto dto) {
        WorkoutPlanDto updated = workoutService.updateWorkoutPlan(principal.getId(), id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Workout plan updated", updated));
    }

    @DeleteMapping("/plans/{id}")
    @Operation(summary = "Delete a personal workout plan")
    public ResponseEntity<ApiResponse<Void>> deletePlan(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        workoutService.deleteWorkoutPlan(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Workout plan deleted", null));
    }

    // Sessions & Logs
    @GetMapping("/sessions")
    @Operation(summary = "Get paginated history of logged workout sessions")
    public ResponseEntity<ApiResponse<Page<WorkoutSessionDto>>> getSessions(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Page<WorkoutSessionDto> sessions = workoutService.getSessions(principal.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.ok(sessions));
    }

    @GetMapping("/sessions/{id}")
    @Operation(summary = "Get specific workout session with completed set logs")
    public ResponseEntity<ApiResponse<WorkoutSessionDto>> getSessionById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        WorkoutSessionDto session = workoutService.getSessionById(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(session));
    }

    @PostMapping("/sessions")
    @Operation(summary = "Log a completed workout session with exercise sets, reps, and weights")
    public ResponseEntity<ApiResponse<WorkoutSessionDto>> logSession(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WorkoutSessionDto dto) {
        WorkoutSessionDto logged = workoutService.logSession(principal.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Workout session logged successfully", logged));
    }

    @DeleteMapping("/sessions/{id}")
    @Operation(summary = "Delete a workout session entry")
    public ResponseEntity<ApiResponse<Void>> deleteSession(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        workoutService.deleteSession(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Workout session deleted", null));
    }
}
