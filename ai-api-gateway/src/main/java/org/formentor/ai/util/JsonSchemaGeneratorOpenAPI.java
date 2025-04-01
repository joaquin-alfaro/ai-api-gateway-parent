package org.formentor.ai.util;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.SchemaVersion;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springframework.ai.util.json.JsonParser;

import java.util.ArrayList;
import java.util.List;

public class JsonSchemaGeneratorOpenAPI {
    // Get inspiration from https://github.com/spring-projects/spring-ai/blob/83294023cd4946c46b42df01b686bcc70418ea70/spring-ai-core/src/main/java/org/springframework/ai/util/json/schema/JsonSchemaGenerator.java#L119

    /**
     * Generate a JSON Schema for an Operation parameters.
     */
    public static String generateForOperation(Operation operation) {
        ObjectNode schema = JsonParser.getObjectMapper().createObjectNode();
        schema.put("$schema", SchemaVersion.DRAFT_2020_12.getIdentifier());
        schema.put("type", "object");

        ObjectNode properties = schema.putObject("properties");
        List<String> required = new ArrayList<>();

        if (operation.getParameters() != null) {
            operation.getParameters().forEach(parameter -> {
                String parameterName = parameter.getName();
                ObjectNode parameterNode = JsonParser.getObjectMapper().createObjectNode();
                parameterNode.put("type", determineParameterType(parameter));
                parameterNode.put("description", getParameterDescription( parameter));
                properties.set(parameterName, parameterNode);
                if (parameter.getRequired()) {
                    required.add(parameterName);
                }
            });
        }

        var requiredArray = schema.putArray("required");
        required.forEach(requiredArray::add);

        return schema.toPrettyString();
    }

    private static String getParameterDescription(Parameter parameter) {
        StringBuilder description = new StringBuilder(parameter.getDescription());
        if (parameter.getExample() != null) {
            description.append(String.format(" e.g. %s", parameter.getExample().toString()));
        }

        return description.toString();
    }

    private static String determineParameterType(Parameter parameter) {
        if (parameter.getSchema().getType() != null) {
            return parameter.getSchema().getType();
        }

        var types = parameter.getSchema().getTypes();
        return types.toArray()[0].toString();
    }
}
