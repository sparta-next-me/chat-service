package org.nextme.chat_server.infrastructure.ai;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class GptConfig {

    @Value("${spring.ai.openai.api-key}")
    private String secretKey;

    @Value("${spring.ai.openai.model}")
    private String model;

    @Value("${spring.ai.openai.embedding.api-key}")
    private String embeddingApiKey;

    @Value("${spring.ai.openai.embedding.model}")
    private String embeddingModel;

    @Bean
    public String getModel() {
        return model;
    }

    @Bean
    public String embeddingModelName() {
        return embeddingModel;
    }

    @Bean
    public String openAiSecretKey() {
        return secretKey;
    }
}
