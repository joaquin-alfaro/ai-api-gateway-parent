package org.formentor.ai.tool;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.formentor.ai.OperationMother;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.tool.ToolCallback;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpenAPIToolCallbackTest {

    @Test
    void build_defines_toolDefinition() {
        Operation operationMock = OperationMother.fixed();
        var toolCallBack = new OpenAPIToolCallback("http://formentor.com", "/hotel/destination", operationMock);
        var toolDefinition = toolCallBack.getToolDefinition();

        assertEquals("getHotelsByDestination", toolDefinition.name());
        assertEquals("Get hotels for a given destination", toolDefinition.description());

        var inputSchema_expected = """
{
  "type" : "object",
  "properties" : {
    "destination" : {
      "type" : "string",
      "description" : "Destination where hotels are located e.g. Madrid, Paris"
    }
  },
  "required" : [ "destination" ]
}""";
        assertEquals(inputSchema_expected, toolDefinition.inputSchema());
    }

    @Disabled // Integration test. Requires API documented with OpenAPI
    @Test
    void tools_from_openapi() {
        final SwaggerParseResult result = new OpenAPIParser().readLocation("http://localhost:8080/v3/api-docs.yaml", null, null);
        OpenAPI openAPI = result.getOpenAPI();
        var baseUrl = openAPI.getServers().getFirst().getUrl();
        List<ToolCallback> tools = new ArrayList<>();
        openAPI.getPaths().forEach((path, item) -> {
            tools.add(new OpenAPIToolCallback(baseUrl, path, item.getGet()));
        });

        assertEquals("getHotels", tools.getFirst().getToolDefinition().name());
    }

    @Disabled // Integration test. Requires Ollama
    @Test
    void tool_is_identified_by_model() {
        var chatModel =  buildChatModel();
        var toolCallBack = new OpenAPIToolCallback("http://nonce", "/hotel/destination", OperationMother.fixed());
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(toolCallBack)
                .internalToolExecutionEnabled(false)
                .build();
        Prompt prompt = new Prompt("Give me hotels in London", chatOptions);

        ChatResponse chatResponse = chatModel.call(prompt);

        assertTrue(chatResponse.hasToolCalls());
        AssistantMessage.ToolCall toolCall = chatResponse.getResult().getOutput().getToolCalls().getFirst();
        assertEquals("getHotelsByDestination", toolCall.name());
        assertEquals("{\"destination\":\"London\"}", toolCall.arguments());
    }

    @Disabled // Integration test. Requires Ollama and API serving GET http://localhost:8080/hotel/destination"
    @Test
    void tool_invokes_restEndpoint_when_called() {
        var chatModel =  buildChatModel();
        ToolCallingManager toolCallingManager = ToolCallingManager.builder().build();
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(
                        new OpenAPIToolCallback("http://localhost:8080", "/hotel/destination", OperationMother.fixed())

                )
                .internalToolExecutionEnabled(false)
                .build();
        Prompt prompt = new Prompt("Give me hotels in Madrid", chatOptions);

        ChatResponse chatResponse = chatModel.call(prompt);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, chatResponse);
        prompt = new Prompt(toolExecutionResult.conversationHistory(), chatOptions);
        chatResponse = chatModel.call(prompt);

        assertNotNull(chatResponse.getResult().getOutput().getText());
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
