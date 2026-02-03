package com.n.in.provider.groq;


import com.n.in.model.Agent;
import com.n.in.model.dto.NDto;
import com.n.in.model.Step;
import com.n.in.model.repository.AgentRepository;
import com.n.in.provider.groq.client.GroqClient;
import com.n.in.provider.groq.model.reponse.Message;
import com.n.in.provider.groq.model.request.GroqRequest;
import com.n.in.service.IAClientStrategy;
import com.n.in.utils.enums.ProviderEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GroqService implements IAClientStrategy {

    @Autowired
    private GroqClient groqClient;

    @Autowired
    private AgentRepository agentRepository;

    @Override
    public NDto generate(Step step) {
        GroqRequest req = new GroqRequest();
        req.setModel("llama-3.3-70b-versatile");

        Message msg = new Message();
        msg.setRole("user");
        msg.setContent("Genera un dato real verificable...");
        req.setMessages(List.of(msg));
        Optional<Agent> agent = agentRepository.findById(Long.valueOf(ProviderEnum.GEMINI.getId()));

        var res = groqClient.sendPrompt(req,agent.get().getSecret());

        return NDto.builder()
                .type("IA")
                .subType("GROQ")
                .status("initiated")
                .lastUpdated(LocalDateTime.now())
                .created(LocalDateTime.now())
                .build();
    }
}
