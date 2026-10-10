package com.fitai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping({"/", "/health", "/api/v1/health"})
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        response.put("service", "FitAI Backend Service");
        response.put("version", "1.0.0");
        response.put("timestamp", Instant.now().toString());
        response.put("docs", "/swagger-ui.html");
        response.put("actuator", "/actuator/health");
        return ResponseEntity.ok(response);
    }
}
