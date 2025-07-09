package ca.bazlur.chithi.integration;

import ca.bazlur.chithi.service.TestEmailAIAssistant;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled("Disabled due to LangChain4j auto-configuration conflicts with test profile")
class EmailFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private TestEmailAIAssistant testEmailAIAssistant;
    
    @BeforeEach
    void setUp() {
        // Reset to default responses
        testEmailAIAssistant.setEnhanceResponse("Enhanced email content");
        testEmailAIAssistant.setGenerateResponse("Generated email content");
        testEmailAIAssistant.setAnalyzeResponse("Email analysis result");
    }

    @Test
    void testCompleteEnhanceFlow() throws Exception {
        // Given
        Map<String, Object> request = Map.of(
                "content", "I need the quarterly report by end of week",
                "tone", "professional",
                "purpose", "request"
        );
        
        testEmailAIAssistant.setEnhanceResponse("Dear Team, I am writing to formally request the quarterly report. " +
                           "I would appreciate if you could provide it by the end of this week. Thank you.");

        // When & Then
        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void testGenerateFlowWithVariousTones() throws Exception {
        // Test generation with different tones
        String[] tones = {"professional", "casual", "friendly", "formal"};
        
        testEmailAIAssistant.setGenerateResponse("Subject: Meeting Request\n\nDear Team,\n\nI would like to schedule a meeting next week.");
        
        for (String tone : tones) {
            Map<String, Object> request = Map.of(
                    "content", "Need to schedule a meeting next week",
                    "tone", tone,
                    "purpose", "request"
            );

            mockMvc.perform(post("/api/email/generate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Test
    void testAnalyzeFlowWithDifferentContent() throws Exception {
        // Test various email contents for analysis
        String[] contents = {
                "Thanks for your help!",
                "I apologize for the delay in my response.",
                "Following up on our previous conversation...",
                "Could you please provide an update on the project?"
        };
        
        testEmailAIAssistant.setAnalyzeResponse("Analysis: The email is professional in tone and appropriate for its purpose.");

        for (String content : contents) {
            Map<String, Object> request = Map.of(
                    "content", content,
                    "tone", "professional",
                    "purpose", "auto-detect"
            );

            mockMvc.perform(post("/api/email/analyze")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.result").exists());
        }
    }

    @Test
    void testAutoDetectPurposeFlow() throws Exception {
        // Test auto-detect functionality
        Map<String, Object> request = Map.of(
                "content", "I'm sorry for missing the deadline",
                "tone", "professional",
                "purpose", "auto-detect"
        );
        
        testEmailAIAssistant.setEnhanceResponse("I sincerely apologize for missing the deadline. I take full responsibility.");

        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void testCaseInsensitiveToneAndPurpose() throws Exception {
        // Test that tone and purpose are case-insensitive
        Map<String, Object> request = Map.of(
                "content", "Test content",
                "tone", "PROFESSIONAL",
                "purpose", "FOLLOW-UP"
        );
        
        testEmailAIAssistant.setEnhanceResponse("Enhanced test content with professional tone.");

        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testLongEmailContent() throws Exception {
        // Test with longer email content
        String longContent = """
                Dear Team,
                
                I hope this email finds you well. I wanted to reach out regarding the upcoming project milestone.
                As we discussed in our last meeting, there are several key deliverables that need to be completed
                before the end of the quarter.
                
                First, we need to finalize the technical specifications document. This should include all the
                requirements we gathered from stakeholders, as well as the proposed architecture.
                
                Second, the prototype needs to be tested with at least five users to gather initial feedback.
                
                Finally, we should prepare a presentation for the executive team to review our progress.
                
                Please let me know if you have any questions or concerns.
                
                Best regards,
                John
                """;

        Map<String, Object> request = Map.of(
                "content", longContent,
                "tone", "professional",
                "purpose", "update"
        );
        
        testEmailAIAssistant.setEnhanceResponse("Dear Team, I trust this message finds you in good health...");

        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").exists());
    }

    @Test
    void testSpecialCharactersInContent() throws Exception {
        // Test with special characters
        Map<String, Object> request = Map.of(
                "content", "Hello! How are you? I'd like to discuss the $1,000 budget & timeline.",
                "tone", "casual",
                "purpose", "inquiry"
        );
        
        testEmailAIAssistant.setEnhanceResponse("Hey there! Hope you're doing well. Can we chat about the $1,000 budget and timeline?");

        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testEmptyContent() throws Exception {
        // Test with empty content
        Map<String, Object> request = Map.of(
                "content", "",
                "tone", "professional",
                "purpose", "general"
        );
        
        // The TestEmailAIAssistant will throw an exception for empty content
        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }
}