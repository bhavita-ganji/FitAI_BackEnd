package com.fitai.controller;

import com.fitai.dto.AnalyticsSummaryDto;
import com.fitai.dto.ApiResponse;
import com.fitai.security.UserPrincipal;
import com.fitai.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics & Progress", description = "Endpoints for aggregate workout, nutrition, and weight progress statistics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get user performance trends, calorie adherence, weekly activity and weight progression")
    public ResponseEntity<ApiResponse<AnalyticsSummaryDto>> getDashboardAnalytics(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "7") int days) {
        AnalyticsSummaryDto summary = analyticsService.getUserAnalytics(principal.getId(), days);
        return ResponseEntity.ok(ApiResponse.ok(summary));
    }
}
