package com.fitai.service;

import com.fitai.dto.KnowledgeDocumentDto;
import com.fitai.entity.KnowledgeDocument;
import com.fitai.exception.ResourceNotFoundException;
import com.fitai.repository.KnowledgeDocumentRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KnowledgeService {

    private final KnowledgeDocumentRepository knowledgeRepository;

    public KnowledgeService(KnowledgeDocumentRepository knowledgeRepository) {
        this.knowledgeRepository = knowledgeRepository;
    }

    @Transactional(readOnly = true)
    public Page<KnowledgeDocumentDto> getDocuments(String category, String search, Pageable pageable) {
        return knowledgeRepository.findWithFilters(
                (category != null && !category.isBlank()) ? category : null,
                (search != null && !search.isBlank()) ? search : null,
                pageable
        ).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "knowledgeDocs")
    public List<KnowledgeDocument> getAllDocuments() {
        return knowledgeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public KnowledgeDocumentDto getDocumentById(Long id) {
        KnowledgeDocument doc = knowledgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Knowledge document not found with id: " + id));
        return mapToDto(doc);
    }

    @Transactional
    @CacheEvict(value = "knowledgeDocs", allEntries = true)
    public KnowledgeDocumentDto createDocument(KnowledgeDocumentDto dto) {
        KnowledgeDocument doc = new KnowledgeDocument();
        doc.setTitle(dto.getTitle());
        doc.setCategory(dto.getCategory());
        doc.setContent(dto.getContent());
        doc.setTags(dto.getTags());
        doc.setKeywords(dto.getKeywords());
        doc.setSource(dto.getSource());

        KnowledgeDocument saved = knowledgeRepository.save(doc);
        return mapToDto(saved);
    }

    @Transactional
    @CacheEvict(value = "knowledgeDocs", allEntries = true)
    public KnowledgeDocumentDto updateDocument(Long id, KnowledgeDocumentDto dto) {
        KnowledgeDocument doc = knowledgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Knowledge document not found with id: " + id));

        doc.setTitle(dto.getTitle());
        doc.setCategory(dto.getCategory());
        doc.setContent(dto.getContent());
        doc.setTags(dto.getTags());
        doc.setKeywords(dto.getKeywords());
        doc.setSource(dto.getSource());

        KnowledgeDocument updated = knowledgeRepository.save(doc);
        return mapToDto(updated);
    }

    @Transactional
    @CacheEvict(value = "knowledgeDocs", allEntries = true)
    public void deleteDocument(Long id) {
        KnowledgeDocument doc = knowledgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Knowledge document not found with id: " + id));
        knowledgeRepository.delete(doc);
    }

    public KnowledgeDocumentDto mapToDto(KnowledgeDocument k) {
        KnowledgeDocumentDto dto = new KnowledgeDocumentDto();
        dto.setId(k.getId());
        dto.setTitle(k.getTitle());
        dto.setCategory(k.getCategory());
        dto.setContent(k.getContent());
        dto.setTags(k.getTags());
        dto.setKeywords(k.getKeywords());
        dto.setSource(k.getSource());
        return dto;
    }
}
