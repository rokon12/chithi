package ca.bazlur.chithi.config;

import dev.langchain4j.http.client.jdk.JdkHttpClientBuilderFactory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OllamaConfig {

    @Value("${langchain4j.ollama.base-url}")
    private String baseUrl;

    @Value("${langchain4j.ollama.model-name}")
    private String modelName;

    @Value("${langchain4j.ollama.timeout}")
    private Duration timeout;

    @Value("${langchain4j.ollama.temperature}")
    private Double temperature;

    @Bean(name = "ollama")
    public ChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .timeout(timeout)
                .temperature(temperature)
                .httpClientBuilder(new JdkHttpClientBuilderFactory().create())
                .build();
    }
}