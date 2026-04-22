package com.n.in.service;

import com.n.in.model.Execution;
import com.n.in.model.Step;
import com.n.in.model.StepExecution;
import com.n.in.model.Workflow;
import com.n.in.model.repository.ExecutionRepository;
import com.n.in.model.repository.StepExecutionRepository;
import com.n.in.model.repository.WorkflowRepository;
import com.n.in.exception.StepExecutionException;
import com.n.in.exception.WorkflowNotFoundException;
import com.n.in.utils.Constants;
import com.n.in.utils.enums.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkflowExecutionService {

    private final WorkflowRepository workflowRepository;
    private final ExecutionRepository executionRepository;
    private final StepExecutionRepository stepExecutionRepository;
    private final IAClientFactory clientFactory;
    private final InternalOperationService internalOperationService;

    @Transactional
    public void executeWorkflow(Integer workflowId, String initialOutput) {
        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new WorkflowNotFoundException(workflowId));

        Execution execution = createRunningExecution(workflow);

        String currentOutput = initialOutput;
        for (Step step : sortedSteps(workflow)) {
            StepExecution stepExecution = createRunningStepExecution(execution, step);

            try {
                currentOutput = runStep(execution.getId(), step, currentOutput);
                stepExecution.setOutput(currentOutput);
                stepExecution.setStatus(StatusEnum.DONE.getDescription());
                stepExecution.setUpdatedAt(LocalDateTime.now());
                stepExecutionRepository.save(stepExecution);

                execution.setStatus(StatusEnum.DONE.getDescription());
                execution.setUpdatedAt(LocalDateTime.now());
                executionRepository.save(execution);

            } catch (StepExecutionException e) {
                stepExecution.setOutput("ERROR: " + e.getMessage());
                stepExecution.setStatus(StatusEnum.ERROR.getDescription());
                stepExecution.setUpdatedAt(LocalDateTime.now());
                stepExecutionRepository.save(stepExecution);

                execution.setStatus(StatusEnum.ERROR.getDescription());
                execution.setUpdatedAt(LocalDateTime.now());
                executionRepository.save(execution);
                return;
            }
        }
    }

    private Execution createRunningExecution(Workflow workflow) {
        Execution execution = new Execution();
        execution.setWorkflow(workflow);
        execution.setStatus(StatusEnum.RUNNING.getDescription());
        execution.setCreatedAt(LocalDateTime.now());
        execution.setUpdatedAt(LocalDateTime.now());
        return executionRepository.save(execution);
    }

    private StepExecution createRunningStepExecution(Execution execution, Step step) {
        StepExecution stepExecution = new StepExecution();
        stepExecution.setExecution(execution);
        stepExecution.setStep(step);
        stepExecution.setStatus(StatusEnum.RUNNING.getDescription());
        stepExecution.setCreatedAt(LocalDateTime.now());
        stepExecution.setUpdatedAt(LocalDateTime.now());
        return stepExecutionRepository.save(stepExecution);
    }

    private List<Step> sortedSteps(Workflow workflow) {
        return workflow.getSteps().stream()
                .sorted(Comparator.comparing(Step::getOrderIndex))
                .toList();
    }

    private String runStep(Long executionId, Step step, String previousOutput) {
        String resolvedPrompt = resolvePrompt(step.getPrompt(), previousOutput);

        if (Constants.OPERATION_TYPE_INTERNAL.equalsIgnoreCase(step.getOperationType())) {
            return internalOperationService.saveContentFromStepOutput(executionId, step, previousOutput);
        }

        if (step.getAgent() == null) {
            throw new StepExecutionException(step.getName(), new IllegalArgumentException("Step requires an agent for operation: " + step.getOperationType()));
        }

        Step stepWithResolvedPrompt = buildStepWithResolvedPrompt(step, resolvedPrompt);
        IAClientStrategy strategy = clientFactory.getStrategy(step.getAgent());
        try {
            String result = strategy.generate(stepWithResolvedPrompt);
            return result != null ? result : "";
        } catch (RuntimeException e) {
            throw new StepExecutionException(step.getName(), e);
        }
    }

    private String resolvePrompt(String prompt, String previousOutput) {
        if (prompt != null && previousOutput != null) {
            return prompt.replace("{{previous_output}}", previousOutput);
        }
        return prompt;
    }

    private Step buildStepWithResolvedPrompt(Step original, String resolvedPrompt) {
        Step step = new Step();
        step.setPrompt(resolvedPrompt);
        step.setAgent(original.getAgent());
        step.setOperationType(original.getOperationType());
        return step;
    }
}
