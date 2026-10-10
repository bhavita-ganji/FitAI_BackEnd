package com.fitai.controller;

import com.fitai.dto.AdminStatsDto;
import com.fitai.dto.ApiResponse;
import com.fitai.dto.KnowledgeDocumentDto;
import com.fitai.security.UserPrincipal;
import com.fitai.service.AdminService;
import com.fitai.service.KnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Management", description = "Endpoints restricted to users with ROLE_ADMIN for user management, system metrics and RAG knowledge base")
public class AdminController {

    private final AdminService adminService;
    private final KnowledgeService knowledgeService;

    public AdminController(AdminService adminService, KnowledgeService knowledgeService) {
        this.adminService = adminService;
        this.knowledgeService = knowledgeService;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get system-wide metrics and usage counts")
    public ResponseEntity<ApiResponse<AdminStatsDto>> getSystemStats() {
        AdminStatsDto stats = adminService.getSystemStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/users")
    @Operation(summary = "List all registered users with pagination")
    public ResponseEntity<ApiResponse<Page<Map<String, Object>>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Page<Map<String, Object>> users = adminService.getAllUsers(PageRequest.of(page, size, Sort.by("id").descending()));
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @PatchMapping("/users/{id}/role")
    @Operation(summary = "Toggle user role between USER and ADMIN")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleUserRole(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        Map<String, Object> result = adminService.toggleUserRole(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("User role updated successfully", result));
    }

    @PatchMapping("/users/{id}/status")
    @Operation(summary = "Enable or disable user account access")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleUserStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        Map<String, Object> result = adminService.toggleUserStatus(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("User account status updated", result));
    }

    // Knowledge Base Documents
    @GetMapping("/knowledge")
    @Operation(summary = "List and search RAG knowledge base documents")
    public ResponseEntity<ApiResponse<Page<KnowledgeDocumentDto>>> getKnowledgeDocuments(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Page<KnowledgeDocumentDto> docs = knowledgeService.getDocuments(category, search, PageRequest.of(page, size, Sort.by("id").descending()));
        return ResponseEntity.ok(ApiResponse.ok(docs));
    }

    @PostMapping("/knowledge")
    @Operation(summary = "Create a new scientific knowledge document for RAG retrieval")
    public ResponseEntity<ApiResponse<KnowledgeDocumentDto>> createKnowledgeDocument(
            @Valid @RequestBody KnowledgeDocumentDto dto) {
        KnowledgeDocumentDto created = knowledgeService.createDocument(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Document created successfully", created));
    }

    @PutMapping("/knowledge/{id}")
    @Operation(summary = "Update an existing knowledge document")
    public ResponseEntity<ApiResponse<KnowledgeDocumentDto>> updateKnowledgeDocument(
            @PathVariable Long id,
            @Valid @RequestBody KnowledgeDocumentDto dto) {
        KnowledgeDocumentDto updated = knowledgeService.updateDocument(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("Document updated successfully", updated));
    }

    @DeleteMapping("/knowledge/{id}")
    @Operation(summary = "Delete a knowledge document from RAG database")
    public ResponseEntity<ApiResponse<Void>> deleteKnowledgeDocument(@PathVariable Long id) {
        knowledgeService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponse.ok("Document deleted successfully", null));
    }
}
