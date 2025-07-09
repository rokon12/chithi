package ca.bazlur.chithi.service;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailContentCleaner {
    
    // Patterns to remove unwanted content
    private static final Pattern SUBJECT_PATTERN = Pattern.compile(
        "^(Subject:|SUBJECT:|subject:).*$", 
        Pattern.MULTILINE | Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern ENHANCED_EMAIL_HEADER = Pattern.compile(
        "^(Enhanced Email:|Enhanced Version:|Here's the enhanced email:|Here is the enhanced email:).*$",
        Pattern.MULTILINE | Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern EXPLANATION_PATTERN = Pattern.compile(
        "^(Changes made:|I've made the following changes:|Explanation:|Note:).*$",
        Pattern.MULTILINE | Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Cleans the AI-generated content to ensure only the email body is returned
     */
    public String cleanEmailContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return content;
        }
        
        // Remove subject lines
        content = SUBJECT_PATTERN.matcher(content).replaceAll("");
        
        // Remove "Enhanced Email:" headers
        content = ENHANCED_EMAIL_HEADER.matcher(content).replaceAll("");
        
        // Remove explanation sections
        content = removeExplanationSections(content);
        
        // Trim extra whitespace
        content = content.trim();
        
        // Remove multiple consecutive newlines
        content = content.replaceAll("\n{3,}", "\n\n");
        
        return content;
    }
    
    private String removeExplanationSections(String content) {
        String[] lines = content.split("\n");
        StringBuilder cleaned = new StringBuilder();
        boolean inExplanationSection = false;
        
        for (String line : lines) {
            // Check if this line starts an explanation section
            if (EXPLANATION_PATTERN.matcher(line).find()) {
                inExplanationSection = true;
                continue;
            }
            
            // If we're in an explanation section and hit an empty line followed by 
            // what looks like email content, we're probably out of the explanation
            if (inExplanationSection && line.trim().isEmpty()) {
                // Look ahead to see if the next content looks like email body
                inExplanationSection = false;
            }
            
            if (!inExplanationSection) {
                if (cleaned.length() > 0) {
                    cleaned.append("\n");
                }
                cleaned.append(line);
            }
        }
        
        return cleaned.toString();
    }
}