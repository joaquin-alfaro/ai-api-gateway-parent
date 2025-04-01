package org.formentor.ai.application;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.stereotype.Service;

@Service
public class Invoker {
    private final ChatModel chatModel;
    private final ToolsRegister toolsRegister;
    private final ToolCallingManager toolCallingManager;
    public Invoker(ChatModel chatModel, ToolsRegister toolsRegister1) {
        this.chatModel = chatModel;
        this.toolsRegister = toolsRegister1;
        toolCallingManager = ToolCallingManager.builder().build();
    }

    public String run(String message) {
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(toolsRegister.getTools().stream().map(FunctionCallback.class::cast).toList())
                .internalToolExecutionEnabled(false)
                .build();
        Prompt prompt = new Prompt(message, chatOptions);

        ChatResponse chatResponse = chatModel.call(prompt);

        while (chatResponse.hasToolCalls()) {
            ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, chatResponse);

            prompt = new Prompt(toolExecutionResult.conversationHistory(), chatOptions);

            chatResponse = chatModel.call(prompt);
        }

        return chatResponse.getResult().getOutput().getText();
    }
}
