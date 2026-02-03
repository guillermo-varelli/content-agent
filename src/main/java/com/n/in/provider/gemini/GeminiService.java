package com.n.in.provider.gemini;


import com.n.in.model.Step;
import com.n.in.provider.gemini.client.GeminiClient;
import com.n.in.provider.gemini.model.request.Content;
import com.n.in.provider.gemini.model.request.GeminiRequest;
import com.n.in.provider.gemini.model.request.Part;
import com.n.in.provider.gemini.model.response.GeminiResponse;
import com.n.in.service.IAClientStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeminiService implements IAClientStrategy {
    @Autowired
    private GeminiClient geminiClient;

    @Override
    public Object generate(Step step) {
        GeminiRequest req = new GeminiRequest();
        Content c = new Content();
        Part p = new Part();
        p.setText(step.getPrompt());
        c.setParts(List.of(p));
        req.setContents(List.of(c));
        GeminiResponse response = geminiClient.sendPrompt(req,step.getAgent().getSecret());
        Object result =response.getCandidates().get(0).getContent().getParts().get(0).getText();
        return result;
    }
}