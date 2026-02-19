package com.n.in.scheduler;

import com.n.in.model.repository.ContentRepository;
import com.n.in.scrape.infobae.InfobaeTecnoService;
import com.n.in.service.WorkflowExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Set;

@Component
public class ContentStatusTask {

    private static final Logger log = LoggerFactory.getLogger(ContentStatusTask.class);

    @Autowired
    WorkflowExecutionService workflowExecutionService;

    @Autowired
    InfobaeTecnoService infobaeTecnoService;

    @Autowired
    ContentRepository contentRepository;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private static final Set<Long> WORKFLOW_IDS = Set.of(6L);

    @Scheduled(fixedRate = 1000)
    public void createContentWithAIHumanHacks() throws Exception {
        for (Long workflowId : WORKFLOW_IDS) {
            workflowExecutionService.executeWorkflow(workflowId, null);
        }
        log.info("Content created at {}", dateFormat.format(System.currentTimeMillis()));
    }

  /*  @Scheduled(fixedRate = 10000)
    public void createContentScrapedInfobaeTech() throws Exception {
        infobaeTecnoService.scrapeTecno().forEach(item ->
                workflowExecutionService.executeWorkflow(5L, item)
        );

        log.info("Content created at {}", dateFormat.format(System.currentTimeMillis()));
    }*/

    /*
    @Scheduled(fixedRate = 1000)
    public void processInitiatedNs() {
        this.nRepository.findByStatusAndImageUrlIsNull("initiated").stream().forEach((entity) -> {
            UnsplashSearchResponse response = null;

            try {
                response = this.unsplashClient.searchPhotos(entity.getImagePrompt(),"3raBUTruWk_fZV9GzBh9qgjk3xlzS-RJWE-e8IYeZZQ");
            } catch (Exception var4) {
                throw new RuntimeException(var4);
            }

            Optional.ofNullable(response.getResults()).filter((results) -> {
                return !results.isEmpty();
            }).map((results) -> {
                return ((UnsplashPhoto)results.get(0)).getUrls().getRegular();
            }).ifPresentOrElse((imageUrl) -> {
                ImageDownloader.downloadImageToTmp(imageUrl, entity.getId() + ".jpg");
                log.info("Image downloaded for entity {} from URL {}", entity.getId(), imageUrl);
                entity.setImageUrl(imageUrl);
                entity.setStatus("pending");
                this.nRepository.save(entity);
            }, () -> {
                log.info("No images found for entity {}", entity.getId());
            });
        });
    }

     */
}