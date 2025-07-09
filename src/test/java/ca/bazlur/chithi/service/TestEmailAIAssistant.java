package ca.bazlur.chithi.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestEmailAIAssistant implements EmailAIAssistant {
    
    private String enhanceResponse = "Enhanced email content";
    private String generateResponse = "Generated email content";
    private String analyzeResponse = "Email analysis result";
    
    @Override
    public String enhanceEmail(String content, String tone, String purpose) {
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("Content cannot be empty");
        }
        return enhanceResponse;
    }
    
    @Override
    public String generateEmail(String idea, String tone, String purpose) {
        return generateResponse;
    }
    
    @Override
    public String analyzeEmail(String content) {
        return analyzeResponse;
    }
    
    // Methods to set custom responses for testing
    public void setEnhanceResponse(String response) {
        this.enhanceResponse = response;
    }
    
    public void setGenerateResponse(String response) {
        this.generateResponse = response;
    }
    
    public void setAnalyzeResponse(String response) {
        this.analyzeResponse = response;
    }
}