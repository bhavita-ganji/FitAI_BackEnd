package com.fitai.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChatRequestDto {

    private Long conversationId;

    @NotBlank(message = "Message content cannot be blank")
    @Size(max = 2000, message = "Message is too long (max 2000 characters)")
    private String message;

    public ChatRequestDto() {}

    public ChatRequestDto(Long conversationId, String message) {
        this.conversationId = conversationId;
        this.message = message;
    }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
