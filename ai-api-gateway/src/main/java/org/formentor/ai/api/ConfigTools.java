package org.formentor.ai.api;

import jakarta.annotation.PostConstruct;
import org.formentor.ai.application.ToolsRegister;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ConfigTools {
    private final ToolsRegister toolsRegister;
    private final List<String> services;

    public ConfigTools(Config config, ToolsRegister toolsRegister) {
        this.services = config.getServices();
        this.toolsRegister = toolsRegister;
    }

    @PostConstruct
    public void loadTools() {
        services.forEach(toolsRegister::register);
    }
}
