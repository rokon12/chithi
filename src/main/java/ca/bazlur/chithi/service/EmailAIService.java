package ca.bazlur.chithi.service;

import ca.bazlur.chithi.dto.EmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailAIService {

    private static final Logger log = LoggerFactory.getLogger(EmailAIService.class);
    private final EmailAIAssistant emailAIAssistant;
    private final EmailSignatureHandler signatureHandler;
    private final EmailContentCleaner contentCleaner;
    
    public EmailAIService(EmailAIAssistant emailAIAssistant, 
                         EmailSignatureHandler signatureHandler,
                         EmailContentCleaner contentCleaner) {
        this.emailAIAssistant = emailAIAssistant;
        this.signatureHandler = signatureHandler;
        this.contentCleaner = contentCleaner;
    }

    public String enhanceEmail(EmailRequest request) {
        log.info("Enhancing email with tone: {} and purpose: {}", request.tone(), request.purpose());
        
        try {
            // Extract signature from original content
            String[] parts = signatureHandler.extractSignature(request.content());
            String bodyWithoutSignature = parts[0];
            String originalSignature = parts[1];
            
            // Enhance only the body
            String enhancedBody = emailAIAssistant.enhanceEmail(
                bodyWithoutSignature, 
                request.tone().toString(), 
                request.purpose().toString()
            );
            
            // Clean the enhanced content to remove any unwanted headers or explanations
            enhancedBody = contentCleaner.cleanEmailContent(enhancedBody);
            
            // Combine enhanced body with original signature
            return signatureHandler.combineWithSignature(enhancedBody, originalSignature);
        } catch (Exception e) {
            log.error("Error enhancing email", e);
            throw new RuntimeException("Failed to enhance email: " + e.getMessage());
        }
    }

    public String generateEmail(EmailRequest request) {
        log.info("Generating email with tone: {} and purpose: {}", request.tone(), request.purpose());
        
        try {
            String generated = emailAIAssistant.generateEmail(
                request.content(), 
                request.tone().toString(), 
                request.purpose().toString()
            );
            
            // Clean the generated content to remove any unwanted headers
            return contentCleaner.cleanEmailContent(generated);
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