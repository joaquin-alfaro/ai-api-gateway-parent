package org.formentor.ai.application;

import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.formentor.ai.tool.OpenAPIToolCallback;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ToolsRegister {
    private final List<ToolCallback> tools;

    public ToolsRegister() {
        this.tools = new ArrayList<>();
    }

    public List<ToolCallback> getTools() {
        return tools;
    }

    public void register(String url) {
        SwaggerParseResult result = new OpenAPIParser().readLocation(url, null, null);
        OpenAPI openAPI = result.getOpenAPI();
        if (openAPI != null) {
            var baseUrl = openAPI.getServers().getFirst().getUrl();
            openAPI.getPaths().forEach((path, item) -> {
                tools.add(new OpenAPIToolCallback(baseUrl, path, item.getGet()));
            });
        }
    }
}
