package com.n.in.provider.gemini;

import com.n.in.model.Step;
import com.n.in.provider.gemini.client.GeminiClient;
import com.n.in.provider.gemini.model.request.Content;
import com.n.in.provider.gemini.model.request.GeminiRequest;
import com.n.in.provider.gemini.model.request.Part;
import com.n.in.provider.gemini.model.response.GeminiResponse;
import com.n.in.service.IAClientStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiService implements IAClientStrategy {

    private final GeminiClient geminiClient;

    @Override
    public String generate(Step step) {
        GeminiRequest request = buildRequest(step.getPrompt());
        GeminiResponse response = geminiClient.sendPrompt(request, step.getAgent().getSecret());
        return extractResponseText(response);
    }

    private GeminiRequest buildRequest(String prompt) {
        Part part = new Part();
        part.setText(prompt);

        Content content = new Content();
        content.setParts(List.of(part));

        GeminiRequest request = new GeminiRequest();
        request.setContents(List.of(content));
        return request;
    }

    private String extractResponseText(GeminiResponse response) {
        if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
            return "";
        }
        return response.getCandidates().get(0).getContent().getParts().get(0).getText();
    }
}
