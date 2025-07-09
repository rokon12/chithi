package ca.bazlur.chithi.config;

import ca.bazlur.chithi.service.EmailAIAssistant;
import dev.langchain4j.model.chat.ChatModel;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@TestConfiguration
@Profile("test")
public class MockAIConfiguration {

    @Bean
    @Primary
    public ChatModel mockChatModel() {
        return Mockito.mock(ChatModel.class);
    }

}