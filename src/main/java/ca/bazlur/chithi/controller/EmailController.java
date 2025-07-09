package ca.bazlur.chithi.controller;

import ca.bazlur.chithi.dto.EmailRequest;
import ca.bazlur.chithi.dto.EmailResponse;
import ca.bazlur.chithi.service.EmailAIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);
    private final EmailAIService emailAIService;
    
    public EmailController(EmailAIService emailAIService) {
        this.emailAIService = emailAIService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/enhance")
    public ResponseEntity<EmailResponse> enhanceEmail(@RequestBody EmailRequest request) {
        log.info("Received enhance request");
        try {
            String enhanced = emailAIService.enhanceEmail(request);
            return ResponseEntity.ok(EmailResponse.success(enhanced));
        } catch (Exception e) {
            log.error("Error enhancing email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(EmailResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<EmailResponse> generateEmail(@RequestBody EmailRequest request) {
        log.info("Received generate request");
        try {
            String generated = emailAIService.generateEmail(request);
            return ResponseEntity.ok(EmailResponse.success(generated));
        } catch (Exception e) {
            log.error("Error generating email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(EmailResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/analyze")
    public ResponseEntity<EmailResponse> analyzeEmail(@RequestBody EmailRequest request) {
        log.info("Received analyze request");
        try {
            String analysis = emailAIService.analyzeEmail(request);
            return ResponseEntity.ok(EmailResponse.success(analysis));
        } catch (Exception e) {
            log.error("Error analyzing email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(EmailResponse.error(e.getMessage()));
        }
    }
}