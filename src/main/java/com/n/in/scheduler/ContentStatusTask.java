package com.n.in.scheduler;

import com.n.in.model.repository.WorkflowRepository;
import com.n.in.scrape.infobae.InfobaeTecnoService;
import com.n.in.service.WorkflowExecutionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ContentStatusTask {

    private static final Logger log = LoggerFactory.getLogger(ContentStatusTask.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int EXCLUDED_WORKFLOW_ID = 5;

    private final WorkflowExecutionService workflowExecutionService;
    private final WorkflowRepository workflowRepository;

    @Autowired
    InfobaeTecnoService infobaeTecnoService;

    @Scheduled(fixedDelay = 1000)
    public void executeEnabledWorkflows() {
        List<Integer> enabledWorkflowIds = workflowRepository.findIdsByEnabledTrue();
        for (Integer workflowId : enabledWorkflowIds) {
            if (workflowId.equals(EXCLUDED_WORKFLOW_ID)) {
                continue;
            }
            workflowExecutionService.executeWorkflow(workflowId, null);
        }
        log.info("Workflows executed at {}", LocalTime.now().format(TIME_FORMATTER));
    }

    @Scheduled(fixedRate = 1000)
    public void createContentScrapedInfobaeTech() throws Exception {
        infobaeTecnoService.scrapeTecno().forEach(item ->
                workflowExecutionService.executeWorkflow(5, item)
        );

        log.info("Content created from infobae {}", LocalTime.now().format(TIME_FORMATTER));
    }
}
