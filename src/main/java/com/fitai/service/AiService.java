package com.fitai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitai.dto.ChatRequestDto;
import com.fitai.dto.ChatResponseDto;
import com.fitai.dto.ConversationSummaryDto;
import com.fitai.entity.*;
import com.fitai.exception.ResourceNotFoundException;
import com.fitai.exception.UnauthorizedException;
import com.fitai.repository.ChatConversationRepository;
import com.fitai.repository.ChatMessageRepository;
import com.fitai.repository.UserProfileRepository;
import com.fitai.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AiService {

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final RagService ragService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.ai.provider:demo}")
    private String aiProvider;

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.model:gpt-4o-mini}")
    private String modelName;

    public AiService(ChatConversationRepository conversationRepository,
                     ChatMessageRepository messageRepository,
                     UserProfileRepository userProfileRepository,
                     UserRepository userRepository,
                     RagService ragService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userProfileRepository = userProfileRepository;
        this.userRepository = userRepository;
        this.ragService = ragService;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Transactional
    public ChatResponseDto processChat(Long userId, ChatRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 1. Get or create conversation
        ChatConversation conversation;
        if (request.getConversationId() != null) {
            conversation = conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));
            if (!conversation.getUser().getId().equals(userId)) {
                throw new UnauthorizedException("You do not own this conversation");
            }
        } else {
            String title = request.getMessage().length() > 35
                    ? request.getMessage().substring(0, 32) + "..."
                    : request.getMessage();
            conversation = new ChatConversation(user, title);
            conversation = conversationRepository.save(conversation);
        }

        // 2. Persist user message
        ChatMessage userMsg = new ChatMessage(conversation, "USER", request.getMessage(), null, false);
        messageRepository.save(userMsg);

        // 3. RAG: Retrieve relevant knowledge base documents
        List<KnowledgeDocument> ragDocs = ragService.retrieveRelevantDocuments(request.getMessage(), 3);
        List<String> sourceTitles = ragDocs.stream().map(KnowledgeDocument::getTitle).collect(Collectors.toList());
        String sourcesJson = String.join(", ", sourceTitles);

        // 4. Retrieve user profile context
        UserProfile profile = userProfileRepository.findByUserId(userId).orElse(null);

        // 5. Generate Response (Live LLM or Deterministic Demo Mode)
        boolean isDemo = true;
        String answer;

        if (apiKey != null && !apiKey.isBlank() && !"demo".equalsIgnoreCase(aiProvider)) {
            try {
                answer = callLlmProvider(request.getMessage(), profile, ragDocs);
                isDemo = false;
            } catch (Exception ex) {
                logger.warn("Live LLM call failed, falling back to FitAI domain knowledge engine: {}", ex.getMessage());
                answer = generateContextualDemoResponse(request.getMessage(), profile, ragDocs);
            }
        } else {
            answer = generateContextualDemoResponse(request.getMessage(), profile, ragDocs);
        }

        // 6. Persist assistant message
        ChatMessage assistantMsg = new ChatMessage(conversation, "ASSISTANT", answer, sourcesJson, isDemo);
        ChatMessage savedAssistantMsg = messageRepository.save(assistantMsg);

        return new ChatResponseDto(
                conversation.getId(),
                savedAssistantMsg.getId(),
                answer,
                sourceTitles,
                isDemo
        );
    }

    @Transactional(readOnly = true)
    public List<ConversationSummaryDto> getConversations(Long userId) {
        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(c -> new ConversationSummaryDto(
                        c.getId(),
                        c.getTitle(),
                        c.getMessages().size(),
                        c.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getConversationMessages(Long userId, Long conversationId) {
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        if (!conversation.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this conversation");
        }

        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
    }

    @Transactional
    public void deleteConversation(Long userId, Long conversationId) {
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        if (!conversation.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You do not own this conversation");
        }

        conversationRepository.delete(conversation);
    }

    private String callLlmProvider(String query, UserProfile profile, List<KnowledgeDocument> ragDocs) {
        // OpenAI-compatible Chat Completions API
        String endpoint = "https://api.openai.com/v1/chat/completions";

        StringBuilder systemPrompt = new StringBuilder();
        systemPrompt.append("You are FitAI, an elite certified fitness coach and clinical sports nutritionist. ");
        systemPrompt.append("Provide concise, scientifically backed, empathetic advice formatted in clean GitHub Markdown.\n\n");

        if (profile != null) {
            systemPrompt.append("User Context:\n");
            systemPrompt.append(String.format("- Primary Goal: %s\n", profile.getPrimaryGoal()));
            if (profile.getAge() != null) systemPrompt.append(String.format("- Age: %d years\n", profile.getAge()));
            if (profile.getHeightCm() != null) systemPrompt.append(String.format("- Height: %.1f cm\n", profile.getHeightCm()));
            if (profile.getWeightKg() != null) systemPrompt.append(String.format("- Weight: %.1f kg\n", profile.getWeightKg()));
            if (profile.getActivityLevel() != null) systemPrompt.append(String.format("- Activity Level: %s\n", profile.getActivityLevel()));
            if (profile.getDietaryPreferences() != null) systemPrompt.append(String.format("- Dietary Preferences: %s\n", profile.getDietaryPreferences()));
        }

        if (!ragDocs.isEmpty()) {
            systemPrompt.append("\nRetrieved Scientific Knowledge Base Sources:\n");
            for (KnowledgeDocument doc : ragDocs) {
                systemPrompt.append(String.format("### %s (%s)\n%s\n\n", doc.getTitle(), doc.getSource(), doc.getContent()));
            }
            systemPrompt.append("Cite these principles when applicable.\n");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", modelName);
        body.put("temperature", 0.7);
        body.put("max_tokens", 800);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt.toString()));
        messages.add(Map.of("role", "user", "content", query));
        body.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.path("choices").get(0).path("message").path("content").asText();
            } catch (Exception e) {
                logger.error("Failed to parse LLM response JSON", e);
            }
        }

        throw new RuntimeException("LLM returned non-200 or empty response");
    }

    private String generateContextualDemoResponse(String query, UserProfile profile, List<KnowledgeDocument> ragDocs) {
        String lowerQuery = query.toLowerCase();
        String goal = (profile != null && profile.getPrimaryGoal() != null) ? profile.getPrimaryGoal() : "GENERAL_FITNESS";
        double weight = (profile != null && profile.getWeightKg() != null) ? profile.getWeightKg() : 70.0;
        double proteinGrams = Math.round(weight * 1.8);

        StringBuilder sb = new StringBuilder();

        if (lowerQuery.contains("protein") || lowerQuery.contains("macro") || lowerQuery.contains("diet") || lowerQuery.contains("nutrition") || lowerQuery.contains("eat")) {
            sb.append("### 🥗 Personalized Nutrition & Macronutrient Strategy\n\n");
            sb.append(String.format("Based on your primary goal of **%s** and bodyweight (%.1f kg), here are your recommended daily macronutrient guidelines:\n\n", goal, weight));
            sb.append(String.format("- **Daily Protein Target**: ~**%.0f - %.0f grams/day** (1.6 - 2.2g per kg of bodyweight)\n", weight * 1.6, weight * 2.2));
            sb.append("- **Carbohydrates**: Fuel training sessions with complex carbs like oats, sweet potatoes, and brown rice.\n");
            sb.append("- **Healthy Fats**: 0.8g per kg from avocados, extra virgin olive oil, nuts, and salmon to support hormonal health.\n");
            sb.append("- **Hydration**: Minimum 2.5 - 3.5 Liters of water daily, increasing on workout days.\n\n");
        } else if (lowerQuery.contains("workout") || lowerQuery.contains("plan") || lowerQuery.contains("exercise") || lowerQuery.contains("routine") || lowerQuery.contains("leg") || lowerQuery.contains("chest") || lowerQuery.contains("split")) {
            sb.append("### 🏋️ Personalized Workout Architecture\n\n");
            sb.append(String.format("To maximize adaptations for **%s**, we apply the **Progressive Overload** principle across structured weekly splits:\n\n", goal));
            sb.append("#### Recommended Push / Pull / Legs Framework:\n");
            sb.append("1. **Compound Foundation**: Start each session with compound lifts (Squat, Deadlift, Bench Press, Overhead Press) for 3-4 sets of 6-8 reps.\n");
            sb.append("2. **Hypertrophy & Accessory Work**: Follow with targeted dumbbell or cable movements for 3 sets of 10-12 reps.\n");
            sb.append("3. **Rest Intervals**: 2-3 minutes for heavy compound movements; 60-90 seconds for isolation sets.\n");
            sb.append("4. **Weekly Frequency**: 3 to 5 training days with at least 1-2 dedicated recovery days.\n\n");
        } else if (lowerQuery.contains("creatine") || lowerQuery.contains("supplement")) {
            sb.append("### 💊 Evidence-Based Supplement Protocol\n\n");
            sb.append("- **Creatine Monohydrate**: 3-5g daily. No loading phase is required. Helps regenerate ATP for explosive muscular contractions.\n");
            sb.append("- **Whey / Plant Protein**: Fast, convenient post-workout recovery to meet your daily target.\n");
            sb.append("- **Vitamin D3 & Omega-3**: Crucial for bone density, joint mobility, and systemic recovery.\n\n");
        } else if (lowerQuery.contains("recovery") || lowerQuery.contains("sore") || lowerQuery.contains("sleep")) {
            sb.append("### 🛌 Optimal Recovery & Supercompensation\n\n");
            sb.append("- **Sleep Optimization**: 7 to 9 hours of quality sleep nightly. Muscle protein synthesis and growth hormone release peak during deep slow-wave sleep.\n");
            sb.append("- **Active Recovery**: Light 20-30 min walks, mobility flows, and proper post-workout stretching.\n");
            sb.append("- **Deload Weeks**: Every 6-8 weeks, reduce training volume by 40-50% to prevent central nervous system fatigue.\n\n");
        } else {
            sb.append("### 💡 FitAI Personalized Coaching Guidance\n\n");
            sb.append(String.format("Hello! Based on your profile aiming for **%s**:\n\n", goal));
            sb.append("1. **Consistency**: Prioritize adherence to your weekly workout plans and balanced daily nutrition targets.\n");
            sb.append(String.format("2. **Macronutrient Balance**: Ensure you hit around **%.0f grams of protein daily** spread across 3-4 meals.\n", proteinGrams));
            sb.append("3. **Tracking**: Log your workout sets and daily food entries in FitAI to observe meaningful analytics trends over time.\n\n");
        }

        if (!ragDocs.isEmpty()) {
            sb.append("#### 📚 Retrieved Knowledge Base Insights:\n");
            for (KnowledgeDocument doc : ragDocs) {
                sb.append(String.format("> **%s** *(Source: %s)*\n", doc.getTitle(), doc.getSource()));
                String snippet = doc.getContent().length() > 220 ? doc.getContent().substring(0, 215) + "..." : doc.getContent();
                sb.append(String.format("> %s\n\n", snippet));
            }
        }

        sb.append("\n*Note: FitAI provides informational fitness and nutrition guidance. Consult a medical professional for personalized clinical diagnoses.*");

        return sb.toString();
    }
}
