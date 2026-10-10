package com.fitai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ChatConversation conversation;

    @Column(nullable = false, length = 30)
    private String senderRole; // USER, ASSISTANT, SYSTEM

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 1000)
    private String ragSources; // JSON list of document IDs / titles cited

    private boolean isDemoResponse = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ChatMessage() {}

    public ChatMessage(ChatConversation conversation, String senderRole, String content, String ragSources, boolean isDemoResponse) {
        this.conversation = conversation;
        this.senderRole = senderRole;
        this.content = content;
        this.ragSources = ragSources;
        this.isDemoResponse = isDemoResponse;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ChatConversation getConversation() { return conversation; }
    public void setConversation(ChatConversation conversation) { this.conversation = conversation; }

    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getRagSources() { return ragSources; }
    public void setRagSources(String ragSources) { this.ragSources = ragSources; }

    public boolean isDemoResponse() { return isDemoResponse; }
    public void setDemoResponse(boolean demoResponse) { isDemoResponse = demoResponse; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
