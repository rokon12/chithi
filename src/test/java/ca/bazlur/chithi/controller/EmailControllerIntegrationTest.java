package ca.bazlur.chithi.controller;

import ca.bazlur.chithi.dto.EmailPurpose;
import ca.bazlur.chithi.dto.EmailRequest;
import ca.bazlur.chithi.dto.EmailTone;
import ca.bazlur.chithi.service.EmailAIAssistant;
import ca.bazlur.chithi.service.EmailAIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmailController.class)
@ActiveProfiles("test")
class EmailControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailAIService emailAIService;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
    }

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/email/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void testEnhanceEmail_Success() throws Exception {
        // Given
        EmailRequest request = new EmailRequest(
                "Hello, I wanted to check on the project status.",
                EmailTone.PROFESSIONAL,
                EmailPurpose.FOLLOW_UP
        );

        String enhancedEmail = "Dear Team,\n\nI hope this message finds you well. " +
                "I am writing to inquire about the current status of our project. " +
                "Could you please provide an update at your earliest convenience?\n\n" +
                "Best regards";

        when(emailAIService.enhanceEmail(request)).thenReturn(enhancedEmail);

        // When & Then
        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(enhancedEmail))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void testEnhanceEmail_WithDefaultValues() throws Exception {
        // Given - Only content provided, tone and purpose should default
        String requestJson = "{\"content\": \"Thanks for your help\"}";

        String enhancedEmail = "Thank you for your assistance with this matter.";

        when(emailAIService.enhanceEmail(any(EmailRequest.class))).thenReturn(enhancedEmail);

        // When & Then
        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(enhancedEmail))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testGenerateEmail_Success() throws Exception {
        // Given
        EmailRequest request = new EmailRequest(
                "Need to ask for project deadline extension",
                EmailTone.FORMAL,
                EmailPurpose.REQUEST
        );

        String generatedEmail = "Subject: Request for Project Deadline Extension\n\n" +
                "Dear [Manager's Name],\n\n" +
                "I am writing to formally request an extension for the project deadline...";

        when(emailAIService.generateEmail(request)).thenReturn(generatedEmail);

        // When & Then
        mockMvc.perform(post("/api/email/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(generatedEmail))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void testAnalyzeEmail_Success() throws Exception {
        // Given
        EmailRequest request = new EmailRequest(
                "Hey! Just wanted to check if you got my last email about the meeting tomorrow?",
                EmailTone.CASUAL,
                EmailPurpose.FOLLOW_UP
        );

        String analysis = "Analysis:\n" +
                "1. Tone: The email is very casual and informal\n" +
                "2. Structure: Brief and to the point\n" +
                "3. Suggestions: Consider adding more context and a clear call to action";

        when(emailAIService.analyzeEmail(request)).thenReturn(analysis);

        // When & Then
        mockMvc.perform(post("/api/email/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(analysis))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testEnhanceEmail_ServiceError() throws Exception {
        // Given
        EmailRequest request = new EmailRequest("Test content");

        when(emailAIService.enhanceEmail(any(EmailRequest.class)))
                .thenThrow(new RuntimeException("AI service unavailable"));

        // When & Then
        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("AI service unavailable"))
                .andExpect(jsonPath("$.result").doesNotExist());
    }

    @Test
    void testGenerateEmail_ServiceError() throws Exception {
        // Given
        EmailRequest request = new EmailRequest("Test idea");

        when(emailAIService.generateEmail(any(EmailRequest.class)))
                .thenThrow(new RuntimeException("Model timeout"));

        // When & Then
        mockMvc.perform(post("/api/email/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Model timeout"));
    }

    @Test
    void testAnalyzeEmail_ServiceError() throws Exception {
        // Given
        EmailRequest request = new EmailRequest("Test email");

        when(emailAIService.analyzeEmail(any(EmailRequest.class)))
                .thenThrow(new RuntimeException("Analysis failed"));

        // When & Then
        mockMvc.perform(post("/api/email/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Analysis failed"));
    }

    @Test
    void testInvalidEndpoint() throws Exception {
        mockMvc.perform(get("/api/email/invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testMissingRequestBody() throws Exception {
        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidJsonFormat() throws Exception {
        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDifferentToneValues() throws Exception {
        // Test with different tone values
        for (EmailTone tone : EmailTone.values()) {
            EmailRequest request = new EmailRequest("Test content", tone, EmailPurpose.GENERAL);
            
            when(emailAIService.enhanceEmail(any(EmailRequest.class)))
                    .thenReturn("Enhanced with " + tone.getDisplayName() + " tone");

            mockMvc.perform(post("/api/email/enhance")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(containsString(tone.getDisplayName())));
        }
    }

    @Test
    void testAutoDetectPurpose() throws Exception {
        // Given
        EmailRequest request = new EmailRequest(
                "I apologize for the delay in responding",
                EmailTone.PROFESSIONAL,
                EmailPurpose.AUTO_DETECT
        );

        when(emailAIService.enhanceEmail(request)).thenReturn("Enhanced apology email");

        // When & Then
        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Enhanced apology email"));
    }
}