package org.formentor.ai.application;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InvokerTest {
    @Disabled // Integration test. Require Ollama server
    @Test
    void run() {
        ToolsRegister toolsRegister = new ToolsRegister();
        toolsRegister.register("http://localhost:8080/v3/api-docs.yaml");

        Invoker invoker = new Invoker(buildChatModel(), toolsRegister);

        var response = invoker.run("Give me hotels in London");

        assertNotNull(response);
    }

    private ChatModel buildChatModel() {
        var ollamaApi = new OllamaApi("http://localhost:11434");
        return OllamaChatModel.builder()
                .ollamaApi(ollamaApi)
                .defaultOptions(
                        OllamaOptions.builder()
                                .model("llama3.1:8b")
                                .temperature(0.1)
                                .build())
                .build();
    }
}
