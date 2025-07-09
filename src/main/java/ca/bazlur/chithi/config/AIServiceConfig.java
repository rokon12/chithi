package ca.bazlur.chithi.config;

import dev.langchain4j.service.spring.AiServiceScannerProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

@Configuration
@ComponentScan(basePackages = "ca.bazlur.chithi")
public class AIServiceConfig {
    
    @Bean
    public AiServiceScannerProcessor aiServiceScannerProcessor() {
        return new AiServiceScannerProcessor();
    }
}