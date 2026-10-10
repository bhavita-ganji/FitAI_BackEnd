package com.fitai.dto;

import java.time.LocalDateTime;

public class ConversationSummaryDto {

    private Long id;
    private String title;
    private int messageCount;
    private LocalDateTime updatedAt;

    public ConversationSummaryDto() {}

    public ConversationSummaryDto(Long id, String title, int messageCount, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.messageCount = messageCount;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getMessageCount() { return messageCount; }
    public void setMessageCount(int messageCount) { this.messageCount = messageCount; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
