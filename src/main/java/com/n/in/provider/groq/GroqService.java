package com.n.in.provider.groq;

import com.n.in.model.Step;
import com.n.in.provider.groq.client.GroqClient;
import com.n.in.provider.groq.model.reponse.GroqResponse;
import com.n.in.provider.groq.model.reponse.Message;
import com.n.in.provider.groq.model.request.GroqRequest;
import com.n.in.service.IAClientStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroqService implements IAClientStrategy {

    private static final String DEFAULT_MODEL = "llama-3.3-70b-versatile";

    private final GroqClient groqClient;

    @Override
    public String generate(Step step) {
        GroqRequest request = buildRequest(step.getPrompt());
        GroqResponse response = groqClient.sendPrompt(request, step.getAgent().getSecret());
        return extractResponseText(response);
    }

    private GroqRequest buildRequest(String prompt) {
        Message message = new Message();
        message.setRole("user");
        message.setContent(prompt);

        GroqRequest request = new GroqRequest();
        request.setModel(DEFAULT_MODEL);
        request.setMessages(List.of(message));
        return request;
    }

    private String extractResponseText(GroqResponse response) {
        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "";
        }
        return response.getChoices().get(0).getMessage().getContent();
    }
}
