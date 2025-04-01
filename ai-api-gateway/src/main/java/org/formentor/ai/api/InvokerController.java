package org.formentor.ai.api;

import org.formentor.ai.application.Invoker;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("invoke")
public class InvokerController {
    private final Invoker invoker;

    public InvokerController(Invoker invoker) {
        this.invoker = invoker;
    }

    @GetMapping
    public String invoke(@RequestParam("m") String message) {
        return invoker.run(message);
    }
}
