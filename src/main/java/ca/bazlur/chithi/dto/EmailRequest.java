package ca.bazlur.chithi.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record EmailRequest(
    String content,
    EmailTone tone,
    EmailPurpose purpose
) {
    public EmailRequest {
        // Set defaults if null
        if (tone == null) {
            tone = EmailTone.PROFESSIONAL;
        }
        if (purpose == null) {
            purpose = EmailPurpose.AUTO_DETECT;
        }
    }
    
    // Convenience constructor with just content
    public EmailRequest(String content) {
        this(content, EmailTone.PROFESSIONAL, EmailPurpose.AUTO_DETECT);
    }
    
    // JSON deserialization support for string values
    @JsonCreator
    public static EmailRequest create(
            @JsonProperty("content") String content,
            @JsonProperty("tone") String toneStr,
            @JsonProperty("purpose") String purposeStr) {
        
        EmailTone tone = EmailTone.PROFESSIONAL;
        EmailPurpose purpose = EmailPurpose.AUTO_DETECT;
        
        if (toneStr != null) {
            try {
                tone = EmailTone.valueOf(toneStr.toUpperCase().replace(" ", "_").replace("-", "_"));
            } catch (IllegalArgumentException e) {
                // Default to professional if invalid
            }
        }
        
        if (purposeStr != null) {
            try {
                purpose = EmailPurpose.valueOf(purposeStr.toUpperCase().replace(" ", "_").replace("-", "_"));
            } catch (IllegalArgumentException e) {
                // Default to auto-detect if invalid
            }
        }
        
        return new EmailRequest(content, tone, purpose);
    }
}