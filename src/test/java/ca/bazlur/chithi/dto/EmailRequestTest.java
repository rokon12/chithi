package ca.bazlur.chithi.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRecordCreation() {
        // Given
        String content = "Test email content";
        EmailTone tone = EmailTone.PROFESSIONAL;
        EmailPurpose purpose = EmailPurpose.REQUEST;

        // When
        EmailRequest request = new EmailRequest(content, tone, purpose);

        // Then
        assertThat(request.content()).isEqualTo(content);
        assertThat(request.tone()).isEqualTo(tone);
        assertThat(request.purpose()).isEqualTo(purpose);
    }

    @Test
    void testDefaultValues() {
        // When
        EmailRequest request = new EmailRequest("Test content");

        // Then
        assertThat(request.content()).isEqualTo("Test content");
        assertThat(request.tone()).isEqualTo(EmailTone.PROFESSIONAL);
        assertThat(request.purpose()).isEqualTo(EmailPurpose.AUTO_DETECT);
    }

    @Test
    void testNullHandling() {
        // When
        EmailRequest request = new EmailRequest("Content", null, null);

        // Then
        assertThat(request.content()).isEqualTo("Content");
        assertThat(request.tone()).isEqualTo(EmailTone.PROFESSIONAL);
        assertThat(request.purpose()).isEqualTo(EmailPurpose.AUTO_DETECT);
    }

    @Test
    void testJsonDeserialization() throws Exception {
        // Given
        String json = """
            {
                "content": "Hello world",
                "tone": "casual",
                "purpose": "follow-up"
            }
            """;

        // When
        EmailRequest request = objectMapper.readValue(json, EmailRequest.class);

        // Then
        assertThat(request.content()).isEqualTo("Hello world");
        assertThat(request.tone()).isEqualTo(EmailTone.CASUAL);
        assertThat(request.purpose()).isEqualTo(EmailPurpose.FOLLOW_UP);
    }

    @Test
    void testJsonDeserializationWithSpaces() throws Exception {
        // Given
        String json = """
            {
                "content": "Test",
                "tone": "business casual",
                "purpose": "thank you"
            }
            """;

        // When
        EmailRequest request = objectMapper.readValue(json, EmailRequest.class);

        // Then
        assertThat(request.tone()).isEqualTo(EmailTone.BUSINESS_CASUAL);
        assertThat(request.purpose()).isEqualTo(EmailPurpose.THANK_YOU);
    }

    @Test
    void testJsonDeserializationWithHyphens() throws Exception {
        // Given
        String json = """
            {
                "content": "Test",
                "tone": "business-casual",
                "purpose": "auto-detect"
            }
            """;

        // When
        EmailRequest request = objectMapper.readValue(json, EmailRequest.class);

        // Then
        assertThat(request.tone()).isEqualTo(EmailTone.BUSINESS_CASUAL);
        assertThat(request.purpose()).isEqualTo(EmailPurpose.AUTO_DETECT);
    }

    @Test
    void testJsonDeserializationWithInvalidValues() throws Exception {
        // Given
        String json = """
            {
                "content": "Test",
                "tone": "invalid-tone",
                "purpose": "invalid-purpose"
            }
            """;

        // When
        EmailRequest request = objectMapper.readValue(json, EmailRequest.class);

        // Then - Should fall back to defaults
        assertThat(request.tone()).isEqualTo(EmailTone.PROFESSIONAL);
        assertThat(request.purpose()).isEqualTo(EmailPurpose.AUTO_DETECT);
    }

    @Test
    void testJsonDeserializationWithMissingFields() throws Exception {
        // Given
        String json = """
            {
                "content": "Just content"
            }
            """;

        // When
        EmailRequest request = objectMapper.readValue(json, EmailRequest.class);

        // Then
        assertThat(request.content()).isEqualTo("Just content");
        assertThat(request.tone()).isEqualTo(EmailTone.PROFESSIONAL);
        assertThat(request.purpose()).isEqualTo(EmailPurpose.AUTO_DETECT);
    }

    @Test
    void testRecordEquality() {
        // Given
        EmailRequest request1 = new EmailRequest("Content", EmailTone.CASUAL, EmailPurpose.REQUEST);
        EmailRequest request2 = new EmailRequest("Content", EmailTone.CASUAL, EmailPurpose.REQUEST);
        EmailRequest request3 = new EmailRequest("Different", EmailTone.CASUAL, EmailPurpose.REQUEST);

        // Then
        assertThat(request1).isEqualTo(request2);
        assertThat(request1).isNotEqualTo(request3);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        EmailRequest request = new EmailRequest("Test", EmailTone.FORMAL, EmailPurpose.APOLOGY);

        // When
        String toString = request.toString();

        // Then
        assertThat(toString).contains("Test");
        assertThat(toString).contains("formal");  // Display name is used in toString
        assertThat(toString).contains("apology"); // Display name is used in toString
    }
}