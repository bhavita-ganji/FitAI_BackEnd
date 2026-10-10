package com.fitai.controller;

import com.fitai.dto.ApiResponse;
import com.fitai.dto.ExerciseDto;
import com.fitai.entity.User;
import com.fitai.security.UserPrincipal;
import com.fitai.service.ExerciseService;
import com.fitai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/exercises")
@Tag(name = "Exercise Library", description = "Endpoints for searching and managing exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;
    private final UserService userService;

    public ExerciseController(ExerciseService exerciseService, UserService userService) {
        this.exerciseService = exerciseService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Search and filter exercises with pagination")
    public ResponseEntity<ApiResponse<Page<ExerciseDto>>> getExercises(
            @RequestParam(required = false) String muscleGroup,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String equipment,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<ExerciseDto> result = exerciseService.getExercises(muscleGroup, difficulty, equipment, search, pageRequest);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get detailed instructions and safety tips for an exercise")
    public ResponseEntity<ApiResponse<ExerciseDto>> getExerciseById(@PathVariable Long id) {
        ExerciseDto exercise = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(ApiResponse.ok(exercise));
    }

    @PostMapping
    @Operation(summary = "Create a custom exercise")
    public ResponseEntity<ApiResponse<ExerciseDto>> createExercise(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ExerciseDto dto) {
        User user = userService.getUserById(principal.getId());
        ExerciseDto created = exerciseService.createExercise(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Exercise created", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an exercise")
    public ResponseEntity<ApiResponse<ExerciseDto>> updateExercise(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody ExerciseDto dto) {
        User user = userService.getUserById(principal.getId());
        ExerciseDto updated = exerciseService.updateExercise(id, dto, user);
        return ResponseEntity.ok(ApiResponse.ok("Exercise updated", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an exercise")
    public ResponseEntity<ApiResponse<Void>> deleteExercise(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        User user = userService.getUserById(principal.getId());
        exerciseService.deleteExercise(id, user);
        return ResponseEntity.ok(ApiResponse.ok("Exercise deleted", null));
    }
}
