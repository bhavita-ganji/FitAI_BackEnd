package com.fitai.controller;

import com.fitai.dto.ApiResponse;
import com.fitai.dto.FitnessGoalDto;
import com.fitai.dto.UserProfileDto;
import com.fitai.security.UserPrincipal;
import com.fitai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "Endpoints for user profile and personal fitness goals")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile details")
    public ResponseEntity<ApiResponse<UserProfileDto>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        UserProfileDto profile = userService.getProfile(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update or initialize user profile and biometric targets")
    public ResponseEntity<ApiResponse<UserProfileDto>> updateProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UserProfileDto dto) {
        UserProfileDto updated = userService.updateProfile(principal.getId(), dto);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    @GetMapping("/goals")
    @Operation(summary = "Get all fitness goals for current user")
    public ResponseEntity<ApiResponse<List<FitnessGoalDto>>> getGoals(@AuthenticationPrincipal UserPrincipal principal) {
        List<FitnessGoalDto> goals = userService.getGoals(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(goals));
    }

    @PostMapping("/goals")
    @Operation(summary = "Create a new personal fitness goal")
    public ResponseEntity<ApiResponse<FitnessGoalDto>> createGoal(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody FitnessGoalDto dto) {
        FitnessGoalDto created = userService.createGoal(principal.getId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Goal created successfully", created));
    }

    @PutMapping("/goals/{id}")
    @Operation(summary = "Update an existing fitness goal")
    public ResponseEntity<ApiResponse<FitnessGoalDto>> updateGoal(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody FitnessGoalDto dto) {
        FitnessGoalDto updated = userService.updateGoal(principal.getId(), id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Goal updated successfully", updated));
    }

    @DeleteMapping("/goals/{id}")
    @Operation(summary = "Delete a personal fitness goal")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        userService.deleteGoal(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Goal deleted successfully", null));
    }
}
