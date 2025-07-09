package ca.bazlur.chithi.service;

import ca.bazlur.chithi.dto.EmailPurpose;
import ca.bazlur.chithi.dto.EmailRequest;
import ca.bazlur.chithi.dto.EmailTone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailAIServiceTest {

    @Mock
    private EmailAIAssistant emailAIAssistant;

    @InjectMocks
    private EmailAIService emailAIService;

    @BeforeEach
    void setUp() {
        // Setup is handled by MockitoExtension
    }

    @Test
    void testEnhanceEmail_Success() {
        // Given
        EmailRequest request = new EmailRequest(
                "Please send me the report",
                EmailTone.PROFESSIONAL,
                EmailPurpose.REQUEST
        );
        String expectedResult = "Could you please send me the report at your earliest convenience?";

        when(emailAIAssistant.enhanceEmail(
                eq("Please send me the report"),
                eq("professional"),
                eq("request")
        )).thenReturn(expectedResult);

        // When
        String result = emailAIService.enhanceEmail(request);

        // Then
        assertThat(result).isEqualTo(expectedResult);
        verify(emailAIAssistant).enhanceEmail(
                "Please send me the report",
                "professional",
                "request"
        );
    }

    @Test
    void testEnhanceEmail_WithDefaultValues() {
        // Given
        EmailRequest request = new EmailRequest("Thanks");
        String expectedResult = "Thank you.";

        when(emailAIAssistant.enhanceEmail(
                eq("Thanks"),
                eq("professional"),
                eq("auto-detect")
        )).thenReturn(expectedResult);

        // When
        String result = emailAIService.enhanceEmail(request);

        // Then
        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void testEnhanceEmail_ExceptionHandling() {
        // Given
        EmailRequest request = new EmailRequest("Test content");
        when(emailAIAssistant.enhanceEmail(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("AI service error"));

        // When & Then
        assertThatThrownBy(() -> emailAIService.enhanceEmail(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to enhance email: AI service error");
    }

    @Test
    void testGenerateEmail_Success() {
        // Given
        EmailRequest request = new EmailRequest(
                "Meeting reminder for tomorrow",
                EmailTone.FRIENDLY,
                EmailPurpose.REMINDER
        );
        String expectedResult = "Hi there! Just a friendly reminder about our meeting tomorrow...";

        when(emailAIAssistant.generateEmail(
                eq("Meeting reminder for tomorrow"),
                eq("friendly"),
                eq("reminder")
        )).thenReturn(expectedResult);

        // When
        String result = emailAIService.generateEmail(request);

        // Then
        assertThat(result).isEqualTo(expectedResult);
        verify(emailAIAssistant).generateEmail(
                "Meeting reminder for tomorrow",
                "friendly",
                "reminder"
        );
    }

    @Test
    void testGenerateEmail_ExceptionHandling() {
        // Given
        EmailRequest request = new EmailRequest("Test idea");
        when(emailAIAssistant.generateEmail(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Generation failed"));

        // When & Then
        assertThatThrownBy(() -> emailAIService.generateEmail(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to generate email: Generation failed");
    }

    @Test
    void testAnalyzeEmail_Success() {
        // Given
        EmailRequest request = new EmailRequest(
                "Hey, can u send me that file asap? thx",
                EmailTone.CASUAL,
                EmailPurpose.REQUEST
        );
        String expectedAnalysis = "The email is too informal and contains abbreviations...";

        when(emailAIAssistant.analyzeEmail(eq("Hey, can u send me that file asap? thx")))
                .thenReturn(expectedAnalysis);

        // When
        String result = emailAIService.analyzeEmail(request);

        // Then
        assertThat(result).isEqualTo(expectedAnalysis);
        verify(emailAIAssistant).analyzeEmail("Hey, can u send me that file asap? thx");
        // Note: analyze method doesn't use tone or purpose
        verifyNoMoreInteractions(emailAIAssistant);
    }

    @Test
    void testAnalyzeEmail_ExceptionHandling() {
        // Given
        EmailRequest request = new EmailRequest("Test email");
        when(emailAIAssistant.analyzeEmail(anyString()))
                .thenThrow(new RuntimeException("Analysis error"));

        // When & Then
        assertThatThrownBy(() -> emailAIService.analyzeEmail(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to analyze email: Analysis error");
    }

    @Test
    void testAllTonesWork() {
        // Test that all tone enum values are properly converted to strings
        for (EmailTone tone : EmailTone.values()) {
            EmailRequest request = new EmailRequest("Test", tone, EmailPurpose.GENERAL);
            when(emailAIAssistant.enhanceEmail(
                    eq("Test"),
                    eq(tone.toString()),
                    eq("general")
            )).thenReturn("Enhanced");

            String result = emailAIService.enhanceEmail(request);

            assertThat(result).isEqualTo("Enhanced");
            verify(emailAIAssistant).enhanceEmail("Test", tone.toString(), "general");
        }
    }

    @Test
    void testAllPurposesWork() {
        // Test that all purpose enum values are properly converted to strings
        for (EmailPurpose purpose : EmailPurpose.values()) {
            EmailRequest request = new EmailRequest("Test", EmailTone.PROFESSIONAL, purpose);
            when(emailAIAssistant.enhanceEmail(
                    eq("Test"),
                    eq("professional"),
                    eq(purpose.toString())
            )).thenReturn("Enhanced");

            String result = emailAIService.enhanceEmail(request);

            assertThat(result).isEqualTo("Enhanced");
            verify(emailAIAssistant).enhanceEmail("Test", "professional", purpose.toString());
        }
    }
}