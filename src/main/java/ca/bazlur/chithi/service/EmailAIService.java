package ca.bazlur.chithi.service;

import ca.bazlur.chithi.dto.EmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailAIService {

    private static final Logger log = LoggerFactory.getLogger(EmailAIService.class);
    private final EmailAIAssistant emailAIAssistant;
    
    public EmailAIService(EmailAIAssistant emailAIAssistant) {
        this.emailAIAssistant = emailAIAssistant;
    }

    public String enhanceEmail(EmailRequest request) {
        log.info("Enhancing email with tone: {} and purpose: {}", request.tone(), request.purpose());
        
        try {
            return emailAIAssistant.enhanceEmail(
                request.content(), 
                request.tone().toString(), 
                request.purpose().toString()
            );
        } catch (Exception e) {
            log.error("Error enhancing email", e);
            throw new RuntimeException("Failed to enhance email: " + e.getMessage());
        }
    }

    public String generateEmail(EmailRequest request) {
        log.info("Generating email with tone: {} and purpose: {}", request.tone(), request.purpose());
        
        try {
            return emailAIAssistant.generateEmail(
                request.content(), 
                request.tone().toString(), 
                request.purpose().toString()
            );
        } catch (Exception e) {
            log.error("Error generating email", e);
            throw new RuntimeException("Failed to generate email: " + e.getMessage());
        }
    }

    public String analyzeEmail(EmailRequest request) {
        log.info("Analyzing email content");
        
        try {
            return emailAIAssistant.analyzeEmail(request.content());
        } catch (Exception e) {
            log.error("Error analyzing email", e);
            throw new RuntimeException("Failed to analyze email: " + e.getMessage());
        }
    }
}