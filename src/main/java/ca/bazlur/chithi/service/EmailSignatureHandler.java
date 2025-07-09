package ca.bazlur.chithi.service;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmailSignatureHandler {
    
    // Common signature patterns
    private static final String[] SIGNATURE_INDICATORS = {
        "Best regards",
        "Best wishes",
        "Kind regards",
        "Regards",
        "Sincerely",
        "Thanks",
        "Thank you",
        "Cheers",
        "BR",
        "Best",
        "Warm regards",
        "Respectfully",
        "Yours truly",
        "Yours sincerely",
        "Cordially",
        "--",
        "___",
        "Sent from my"
    };
    
    /**
     * Extracts email signature from content
     * @param content The email content
     * @return An array where [0] is the body without signature and [1] is the signature (or empty string)
     */
    public String[] extractSignature(String content) {
        if (content == null || content.trim().isEmpty()) {
            return new String[]{"", ""};
        }
        
        String[] lines = content.split("\n");
        int signatureStartIndex = -1;
        
        // Look for signature indicators
        for (int i = lines.length - 1; i >= Math.max(0, lines.length - 10); i--) {
            String line = lines[i].trim();
            
            // Check if this line contains a signature indicator
            for (String indicator : SIGNATURE_INDICATORS) {
                if (line.toLowerCase().startsWith(indicator.toLowerCase())) {
                    signatureStartIndex = i;
                    break;
                }
            }
            
            // Also check for lines that start with -- or ___
            if (line.matches("^[-_]{2,}$")) {
                signatureStartIndex = i;
                break;
            }
            
            if (signatureStartIndex != -1) {
                break;
            }
        }
        
        // If we found a signature
        if (signatureStartIndex != -1) {
            StringBuilder body = new StringBuilder();
            StringBuilder signature = new StringBuilder();
            
            for (int i = 0; i < lines.length; i++) {
                if (i < signatureStartIndex) {
                    if (body.length() > 0) body.append("\n");
                    body.append(lines[i]);
                } else {
                    if (signature.length() > 0) signature.append("\n");
                    signature.append(lines[i]);
                }
            }
            
            return new String[]{body.toString().trim(), signature.toString().trim()};
        }
        
        // No signature found
        return new String[]{content.trim(), ""};
    }
    
    /**
     * Combines enhanced body with original signature
     * @param enhancedBody The enhanced email body
     * @param originalSignature The original signature
     * @return Combined email
     */
    public String combineWithSignature(String enhancedBody, String originalSignature) {
        if (originalSignature == null || originalSignature.trim().isEmpty()) {
            return enhancedBody;
        }
        
        return enhancedBody + "\n\n" + originalSignature;
    }
}