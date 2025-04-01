package org.formentor.ai.api;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@ConditionalOnProperty("formentor.ai.ollama.host")
public class ConfigChatModel {
    @Bean
    public ChatModel chatModel(Environment env) {
        var host = env.getProperty("formentor.ai.ollama.host");
        var model = env.getProperty("formentor.ai.ollama.model");
        var ollamaApi = new OllamaApi(host);
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(
                        OllamaOptions.builder()
                                .model(model)
                                .temperature(0.1)
                                .build())
                .build();
    }
}
