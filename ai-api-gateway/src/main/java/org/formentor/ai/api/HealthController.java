package org.formentor.ai.api;

import org.formentor.ai.application.ToolsRegister;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("health")
public class HealthController {
    private final ToolsRegister toolsRegister;

    public HealthController(ToolsRegister toolsRegister) {
        this.toolsRegister = toolsRegister;
    }

    @GetMapping
    public String health() {
        return "{status: UP}";
    }

    @GetMapping("/tools")
    public List<ToolCallback> getTools() {
        return toolsRegister.getTools();
    }
}
