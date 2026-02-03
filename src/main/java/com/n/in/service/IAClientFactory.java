package com.n.in.service;

import com.n.in.model.Agent;
import com.n.in.provider.gemini.GeminiService;
import com.n.in.provider.groq.GroqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IAClientFactory {

    @Autowired
    private GroqService groqService;

    @Autowired
    private GeminiService geminiService;

    public IAClientStrategy getStrategy(Agent agent) {

        return switch (agent.getProvider().toUpperCase()) {
            case "GEMINI" -> geminiService;
            case "GROQ" -> groqService;
            default -> throw new IllegalArgumentException("Unknown provider " + agent.getProvider());
        };
    }
}