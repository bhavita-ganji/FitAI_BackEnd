package com.fitai.repository;

import com.fitai.entity.KnowledgeDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KnowledgeDocumentRepository extends JpaRepository<KnowledgeDocument, Long> {

    List<KnowledgeDocument> findByCategoryIgnoreCase(String category);

    @Query("SELECT k FROM KnowledgeDocument k WHERE " +
           "(:category IS NULL OR LOWER(k.category) = LOWER(:category)) AND " +
           "(:search IS NULL OR LOWER(k.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(k.tags) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(k.keywords) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<KnowledgeDocument> findWithFilters(@Param("category") String category,
                                            @Param("search") String search,
                                            Pageable pageable);
}
