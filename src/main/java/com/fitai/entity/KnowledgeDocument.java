package com.fitai.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "knowledge_documents", indexes = {
    @Index(name = "idx_doc_category", columnList = "category")
})
public class KnowledgeDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 60)
    private String category; // EXERCISE_SCIENCE, NUTRITION, RECOVERY, SUPPLEMENTATION, INJURY_PREVENTION

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(length = 255)
    private String tags; // Comma-separated: hypertrophy, progressive_overload, hydration

    @Column(length = 500)
    private String keywords; // Space-separated keywords for similarity calculation

    @Column(length = 255)
    private String source; // e.g. "American College of Sports Medicine", "ISSN Position Stand"

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public KnowledgeDocument() {}

    public KnowledgeDocument(String title, String category, String content, String tags, String keywords, String source) {
        this.title = title;
        this.category = category;
        this.content = content;
        this.tags = tags;
        this.keywords = keywords;
        this.source = source;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
