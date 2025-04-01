package org.formentor.ai.tool;

import io.swagger.v3.oas.models.Operation;
import org.formentor.ai.util.JsonSchemaGeneratorOpenAPI;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.util.ToolUtils;
import org.springframework.ai.util.json.JsonParser;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

public class OpenAPIToolCallback implements ToolCallback {
    private final String path;
    private final RestClient restClient;

    private final ToolDefinition toolDefinition;

    public OpenAPIToolCallback(String baseUrl, String path, Operation operation) {
        this.path = path;

        var name = operation.getOperationId();
        var description = operation.getDescription();
        var inputSchema = JsonSchemaGeneratorOpenAPI.generateForOperation(operation);
        toolDefinition = ToolDefinition.builder()
                .name(name)
                .description(
                        StringUtils.hasText(description) ? description : ToolUtils.getToolDescriptionFromName(description))
                .inputSchema(inputSchema)
                .build();
        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public ToolDefinition getToolDefinition() {
        return toolDefinition;
    }

    @Override
    public String call(String toolInput) {
        Map<String, String> inputParams = JsonParser.<Map<String, String>>fromJson(toolInput, Map.class);
        LinkedMultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        inputParams.forEach((key, value) -> {
            queryParams.put(key, List.of(value));
        });
        var get = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .queryParams(queryParams)
                        .build()
                    );

        return get.retrieve().body(String.class);
    }
}
