package ca.bazlur.chithi.controller;

import ca.bazlur.chithi.dto.EmailPurpose;
import ca.bazlur.chithi.dto.EmailRequest;
import ca.bazlur.chithi.dto.EmailResponse;
import ca.bazlur.chithi.dto.EmailTone;
import ca.bazlur.chithi.service.EmailAIService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private EmailAIService emailAIService;
    
    @InjectMocks
    private EmailController emailController;
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(emailController).build();
        objectMapper = new ObjectMapper();
    }
    
    @Test
    void testEnhanceEmail() throws Exception {
        // Given
        EmailRequest request = new EmailRequest(
                "I need the quarterly report by end of week",
                EmailTone.PROFESSIONAL,
                EmailPurpose.REQUEST
        );
        
        String enhancedContent = "Dear Team, I am writing to formally request the quarterly report. " +
                                 "I would appreciate if you could provide it by the end of this week. Thank you.";
        
        when(emailAIService.enhanceEmail(any(EmailRequest.class)))
                .thenReturn(enhancedContent);
        
        // When & Then
        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").value(enhancedContent))
                .andExpect(jsonPath("$.error").doesNotExist());
    }
    
    @Test
    void testGenerateEmail() throws Exception {
        // Given
        EmailRequest request = new EmailRequest(
                "Need to schedule a meeting next week",
                EmailTone.PROFESSIONAL,
                EmailPurpose.REQUEST
        );
        
        String generatedEmail = "Subject: Meeting Request\n\nDear Team,\n\nI would like to schedule a meeting next week.";
        
        when(emailAIService.generateEmail(any(EmailRequest.class)))
                .thenReturn(generatedEmail);
        
        // When & Then
        mockMvc.perform(post("/api/email/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").value(generatedEmail));
    }
    
    @Test
    void testAnalyzeEmail() throws Exception {
        // Given
        EmailRequest request = new EmailRequest(
                "Thanks for your help!",
                EmailTone.PROFESSIONAL,
                EmailPurpose.AUTO_DETECT
        );
        
        String analysis = "Analysis: The email is professional in tone and appropriate for its purpose.";
        
        when(emailAIService.analyzeEmail(any(EmailRequest.class)))
                .thenReturn(analysis);
        
        // When & Then
        mockMvc.perform(post("/api/email/analyze")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").value(analysis));
    }
    
    @Test
    void testEmptyContent() throws Exception {
        // Given
        EmailRequest request = new EmailRequest(
                "",
                EmailTone.PROFESSIONAL,
                EmailPurpose.GENERAL
        );
        
        when(emailAIService.enhanceEmail(any(EmailRequest.class)))
                .thenThrow(new RuntimeException("Content cannot be empty"));
        
        // When & Then
        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }
    
    @Test
    void testCaseInsensitiveToneAndPurpose() throws Exception {
        // Given - Testing with uppercase values
        String requestJson = """
                {
                    "content": "Test content",
                    "tone": "PROFESSIONAL",
                    "purpose": "FOLLOW-UP"
                }
                """;
        
        when(emailAIService.enhanceEmail(any(EmailRequest.class)))
                .thenReturn("Enhanced test content");
        
        // When & Then
        mockMvc.perform(post("/api/email/enhance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}