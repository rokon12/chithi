package ca.bazlur.chithi.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSuccessResponse() {
        // When
        EmailResponse response = EmailResponse.success("Enhanced email content");

        // Then
        assertThat(response.result()).isEqualTo("Enhanced email content");
        assertThat(response.success()).isTrue();
        assertThat(response.error()).isNull();
    }

    @Test
    void testErrorResponse() {
        // When
        EmailResponse response = EmailResponse.error("Something went wrong");

        // Then
        assertThat(response.result()).isNull();
        assertThat(response.success()).isFalse();
        assertThat(response.error()).isEqualTo("Something went wrong");
    }

    @Test
    void testDirectConstruction() {
        // When
        EmailResponse response = new EmailResponse("Result", true, null);

        // Then
        assertThat(response.result()).isEqualTo("Result");
        assertThat(response.success()).isTrue();
        assertThat(response.error()).isNull();
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Given
        EmailResponse response = EmailResponse.success("Test result");

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then
        assertThat(json).contains("\"result\":\"Test result\"");
        assertThat(json).contains("\"success\":true");
        assertThat(json).contains("\"error\":null");
    }

    @Test
    void testJsonSerializationError() throws Exception {
        // Given
        EmailResponse response = EmailResponse.error("Error message");

        // When
        String json = objectMapper.writeValueAsString(response);

        // Then
        assertThat(json).contains("\"result\":null");
        assertThat(json).contains("\"success\":false");
        assertThat(json).contains("\"error\":\"Error message\"");
    }

    @Test
    void testJsonDeserialization() throws Exception {
        // Given
        String json = """
            {
                "result": "Deserialized result",
                "success": true,
                "error": null
            }
            """;

        // When
        EmailResponse response = objectMapper.readValue(json, EmailResponse.class);

        // Then
        assertThat(response.result()).isEqualTo("Deserialized result");
        assertThat(response.success()).isTrue();
        assertThat(response.error()).isNull();
    }

    @Test
    void testRecordEquality() {
        // Given
        EmailResponse response1 = EmailResponse.success("Same result");
        EmailResponse response2 = EmailResponse.success("Same result");
        EmailResponse response3 = EmailResponse.error("Error");

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1).isNotEqualTo(response3);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        EmailResponse successResponse = EmailResponse.success("Success");
        EmailResponse errorResponse = EmailResponse.error("Error");

        // When
        String successString = successResponse.toString();
        String errorString = errorResponse.toString();

        // Then
        assertThat(successString).contains("Success");
        assertThat(successString).contains("true");
        assertThat(errorString).contains("Error");
        assertThat(errorString).contains("false");
    }
}