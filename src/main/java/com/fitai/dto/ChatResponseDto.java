package com.fitai.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatResponseDto {

    private Long conversationId;
    private Long messageId;
    private String answer;
    private List<String> sources = new ArrayList<>();
    private boolean isDemoMode;
    private LocalDateTime createdAt = LocalDateTime.now();

    public ChatResponseDto() {}

    public ChatResponseDto(Long conversationId, Long messageId, String answer, List<String> sources, boolean isDemoMode) {
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.answer = answer;
        this.sources = sources;
        this.isDemoMode = isDemoMode;
        this.createdAt = LocalDateTime.now();
    }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<String> getSources() { return sources; }
    public void setSources(List<String> sources) { this.sources = sources; }

    public boolean isDemoMode() { return isDemoMode; }
    public void setDemoMode(boolean demoMode) { isDemoMode = demoMode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
