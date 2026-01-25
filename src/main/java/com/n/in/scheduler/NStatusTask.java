package com.n.in.scheduler;

import com.n.in.provider.unplash.client.UnsplashClient;
import com.n.in.model.repository.NRepository;
import com.n.in.provider.unplash.response.UnsplashPhoto;
import com.n.in.provider.unplash.response.UnsplashSearchResponse;
import com.n.in.service.WorkflowExecutionService;
import com.n.in.utils.ImageDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Optional;


@Component
public class NStatusTask {

    private static final Logger log = LoggerFactory.getLogger(NStatusTask.class);

    @Autowired
    WorkflowExecutionService workflowExecutionService;

    @Autowired
    NRepository nRepository;

    @Autowired
    UnsplashClient unsplashClient;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(fixedRate = 1000)
    public void createNs() throws Exception {
        workflowExecutionService.executeWorkflow(1L);
        log.info("N Created at {}", dateFormat.format(System.currentTimeMillis()));
    }

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