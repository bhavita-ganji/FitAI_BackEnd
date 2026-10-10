package com.fitai.service;

import com.fitai.entity.KnowledgeDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RagService {

    private static final Logger logger = LoggerFactory.getLogger(RagService.class);
    private static final Pattern WORD_PATTERN = Pattern.compile("[^a-zA-Z0-9]+");

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but",
            "by", "can", "did", "do", "does", "doing", "down", "during", "each", "few", "for", "from",
            "further", "had", "has", "have", "having", "he", "her", "here", "hers", "herself", "him",
            "himself", "his", "how", "i", "if", "in", "into", "is", "it", "its", "itself", "just",
            "me", "more", "most", "my", "myself", "no", "nor", "not", "now", "of", "off", "on", "once",
            "only", "or", "other", "our", "ours", "ourselves", "out", "over", "own", "s", "same", "she",
            "should", "so", "some", "such", "than", "that", "the", "their", "theirs", "them", "themselves",
            "then", "there", "these", "they", "this", "those", "through", "to", "too", "under", "until",
            "up", "very", "was", "we", "were", "what", "when", "where", "which", "while", "who", "whom",
            "why", "will", "with", "would", "you", "your", "yours", "yourself", "yourselves"
    );

    private final KnowledgeService knowledgeService;

    public RagService(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    public static class ScoredDocument {
        private final KnowledgeDocument document;
        private final double score;

        public ScoredDocument(KnowledgeDocument document, double score) {
            this.document = document;
            this.score = score;
        }

        public KnowledgeDocument getDocument() { return document; }
        public double getScore() { return score; }
    }

    /**
     * Retrieves the top-K most relevant knowledge documents for the given user prompt
     * using cosine similarity over term frequency vectors.
     */
    public List<KnowledgeDocument> retrieveRelevantDocuments(String query, int topK) {
        List<KnowledgeDocument> allDocs = knowledgeService.getAllDocuments();
        if (allDocs.isEmpty() || query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        Map<String, Integer> queryTf = extractTermFrequency(query);
        if (queryTf.isEmpty()) {
            return Collections.emptyList();
        }

        List<ScoredDocument> scoredList = new ArrayList<>();

        for (KnowledgeDocument doc : allDocs) {
            // Combine title, keywords, tags, and content with weighted priority
            String docText = (doc.getTitle() + " ").repeat(3) +
                             (doc.getKeywords() != null ? (doc.getKeywords() + " ").repeat(3) : "") +
                             (doc.getTags() != null ? (doc.getTags() + " ").repeat(2) : "") +
                             doc.getContent();

            Map<String, Integer> docTf = extractTermFrequency(docText);
            double score = computeCosineSimilarity(queryTf, docTf);

            if (score > 0.05) { // Minimum relevance cutoff threshold
                scoredList.add(new ScoredDocument(doc, score));
            }
        }

        scoredList.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        List<KnowledgeDocument> topDocs = scoredList.stream()
                .limit(topK)
                .map(ScoredDocument::getDocument)
                .collect(Collectors.toList());

        logger.info("RAG retrieval for query '{}' matched {} relevant documents out of {}",
                query, topDocs.size(), allDocs.size());

        return topDocs;
    }

    private Map<String, Integer> extractTermFrequency(String text) {
        Map<String, Integer> tf = new HashMap<>();
        String[] words = WORD_PATTERN.split(text.toLowerCase());
        for (String w : words) {
            if (w.length() > 2 && !STOP_WORDS.contains(w)) {
                tf.put(w, tf.getOrDefault(w, 0) + 1);
            }
        }
        return tf;
    }

    private double computeCosineSimilarity(Map<String, Integer> vec1, Map<String, Integer> vec2) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (Map.Entry<String, Integer> entry : vec1.entrySet()) {
            String term = entry.getKey();
            int val1 = entry.getValue();
            normA += val1 * val1;
            if (vec2.containsKey(term)) {
                dotProduct += val1 * vec2.get(term);
            }
        }

        for (int val2 : vec2.values()) {
            normB += val2 * val2;
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
