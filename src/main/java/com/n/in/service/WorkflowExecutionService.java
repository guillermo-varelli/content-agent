package com.n.in.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.n.in.model.Execution;
import com.n.in.model.Step;
import com.n.in.model.StepExecution;
import com.n.in.model.Workflow;
import com.n.in.model.repository.ExecutionRepository;
import com.n.in.model.repository.StepExecutionRepository;
import com.n.in.model.repository.WorkflowRepository;
import com.n.in.scrape.infobae.InfobaeTecnoService;
import com.n.in.utils.enums.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class WorkflowExecutionService {

    private final WorkflowRepository workflowRepository;
    private final ExecutionRepository executionRepository;
    private final StepExecutionRepository stepExecutionRepository;

    private final IAClientFactory clientFactory;

    private final InternalOperationService internalOperationService;

    private final InfobaeTecnoService infobaeScraperService;

    @Transactional
    public Execution executeWorkflow(Long workflowId,String initialData) {

        Workflow workflow = workflowRepository.findById(workflowId)
                .orElseThrow(() -> new RuntimeException("Workflow no encontrado"));

        Execution execution = new Execution();
        execution.setWorkflow(workflow);
        execution.setStatus("RUNNING");
        execution = executionRepository.save(execution);

        String previousOutput = null;

        for (Step step : workflow.getSteps().stream()
                .sorted(Comparator.comparing(Step::getOrderIndex))
                .toList()) {

            StepExecution stepExec = new StepExecution();
            stepExec.setExecution(execution);
            stepExec.setStep(step);
            stepExec.setStatus("RUNNING");
            stepExec.setCreatedAt(LocalDateTime.now());
            stepExec.setUpdatedAt(LocalDateTime.now());
            stepExec = stepExecutionRepository.save(stepExec);
            if (nonNull(initialData)){
                previousOutput = initialData;
            }
            try {
                String output = runStep(stepExec.getExecution().getId(),step, previousOutput);
                stepExec.setOutput(output);
                previousOutput = output;

                if(stepExec.getStatus().equalsIgnoreCase(StatusEnum.ERROR.getDescription())){
                    execution.setStatus(StatusEnum.ERROR.getDescription());
                    stepExec.setUpdatedAt(LocalDateTime.now());
                    executionRepository.save(execution);
                    return null;

                }else{
                    stepExec.setStatus(StatusEnum.DONE.getDescription());
                    stepExecutionRepository.save(stepExec);
                    execution.setCreatedAt(LocalDateTime.now());
                    execution.setUpdatedAt(LocalDateTime.now());
                    execution.setStatus(StatusEnum.DONE.getDescription());
                    executionRepository.save(execution);
                }

            } catch (Exception e) {

                stepExec.setOutput("ERROR: " + e.getMessage());
                stepExec.setStatus("ERROR");
                execution.setStatus(StatusEnum.ERROR.getDescription());
                stepExec.setUpdatedAt(LocalDateTime.now());
                executionRepository.save(execution);
                return null;
            }

        }
      return null;
    }

    private String runStep(Long execution, Step step, String previousOutput) throws Exception {

        String finalPrompt = step.getPrompt();

        if (previousOutput != null && finalPrompt != null) {
            finalPrompt = finalPrompt.replace("{{previous_output}}", previousOutput);
        }

        if ("internal".equalsIgnoreCase(step.getOperationType())) {
            return internalOperationService.handleInternal(execution, step, previousOutput).toString();
        }


        if (step.getAgent() == null) {
            throw new IllegalArgumentException("El step requiere un agent para operación: " + step.getOperationType());
        }

        Step temp = new Step();
        temp.setPrompt(finalPrompt);
        temp.setAgent(step.getAgent());
        temp.setOperationType(step.getOperationType());

        IAClientStrategy strategy = clientFactory.getStrategy(step.getAgent());

        Object result = strategy.generate(temp);
        return result != null ? result.toString() : "";
    }

}