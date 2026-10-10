package com.fitai.service;

import com.fitai.entity.KnowledgeDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RagServiceTest {

    @Mock
    private KnowledgeService knowledgeService;

    @InjectMocks
    private RagService ragService;

    private KnowledgeDocument docHypertrophy;
    private KnowledgeDocument docSleep;
    private KnowledgeDocument docHydration;

    @BeforeEach
    void setUp() {
        docHypertrophy = new KnowledgeDocument(
                "Progressive Overload Principle",
                "EXERCISE_SCIENCE",
                "Progressive overload involves systematically increasing weight, reps, or volume to maximize muscle growth.",
                "hypertrophy, overload",
                "hypertrophy overload muscle sets reps weight",
                "NSCA"
        );

        docSleep = new KnowledgeDocument(
                "Sleep Architecture and Recovery",
                "RECOVERY",
                "Deep slow-wave sleep is essential for human growth hormone release and nervous system recovery.",
                "sleep, recovery",
                "sleep recovery rest fatigue hormones",
                "ACSM"
        );

        docHydration = new KnowledgeDocument(
                "Hydration and Electrolyte Balance",
                "NUTRITION",
                "Maintaining electrolyte homeostasis and water intake prevents cramps and performance loss.",
                "hydration, water",
                "hydration water electrolytes sweat sodium",
                "GSSI"
        );
    }

    @Test
    void testRetrieveRelevantDocumentsReturnsTopMatches() {
        when(knowledgeService.getAllDocuments()).thenReturn(Arrays.asList(docHypertrophy, docSleep, docHydration));

        List<KnowledgeDocument> results = ragService.retrieveRelevantDocuments("How much should I sleep for muscle recovery?", 2);

        assertFalse(results.isEmpty());
        // Sleep recovery document should rank #1 for this query
        assertEquals("Sleep Architecture and Recovery", results.get(0).getTitle());
    }

    @Test
    void testRetrieveWithBlankQueryReturnsEmptyList() {
        List<KnowledgeDocument> results = ragService.retrieveRelevantDocuments("", 3);
        assertTrue(results.isEmpty());
    }
}
