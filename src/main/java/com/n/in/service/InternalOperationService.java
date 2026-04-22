package com.n.in.service;

import com.n.in.model.Step;
import com.n.in.model.dto.ContentDto;
import com.n.in.model.mapper.NMapper;
import com.n.in.model.repository.ContentRepository;
import com.n.in.utils.ContentParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class InternalOperationService {

    private final ContentRepository contentRepository;
    private final NMapper nMapper;

    public String saveContentFromStepOutput(Long executionId, Step step, String previousOutput) {
        ContentDto contentDto = new ContentDto();
        ContentParser.parse(previousOutput, contentDto);
        contentDto.setExecutionId(executionId);
        contentDto.setCreated(LocalDateTime.now());
        contentDto.setLastUpdated(LocalDateTime.now());
        contentDto.setCategory(step.getWorkflow().getCategory());
        contentDto.setSubCategory(step.getWorkflow().getSubCategory());
        contentRepository.save(nMapper.toEntity(contentDto));

        return previousOutput;
    }
}
