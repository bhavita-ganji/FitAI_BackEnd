package com.fitai.controller;

import com.fitai.dto.ApiResponse;
import com.fitai.dto.ChatRequestDto;
import com.fitai.dto.ChatResponseDto;
import com.fitai.dto.ConversationSummaryDto;
import com.fitai.entity.ChatMessage;
import com.fitai.security.UserPrincipal;
import com.fitai.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "FitAI Chatbot & RAG", description = "Endpoints for context-aware conversational assistant and RAG knowledge retrieval")
public class AiChatController {

    private final AiService aiService;

    public AiChatController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    @Operation(summary = "Send question to FitAI Assistant with RAG knowledge retrieval and user profile context")
    public ResponseEntity<ApiResponse<ChatResponseDto>> chat(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChatRequestDto request) {
        ChatResponseDto response = aiService.processChat(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/conversations")
    @Operation(summary = "Get list of previous chat conversations for current user")
    public ResponseEntity<ApiResponse<List<ConversationSummaryDto>>> getConversations(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<ConversationSummaryDto> conversations = aiService.getConversations(principal.getId());
        return ResponseEntity.ok(ApiResponse.ok(conversations));
    }

    @GetMapping("/conversations/{id}/messages")
    @Operation(summary = "Get all messages in a specific conversation")
    public ResponseEntity<ApiResponse<List<ChatMessage>>> getConversationMessages(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        List<ChatMessage> messages = aiService.getConversationMessages(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok(messages));
    }

    @DeleteMapping("/conversations/{id}")
    @Operation(summary = "Delete a chat conversation and its message history")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        aiService.deleteConversation(principal.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Conversation deleted", null));
    }
}
