package com.n.in.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDto {

    private Long id;
    private Long executionId;
    private String title;
    private String shortDescription;
    private String message;
    private String status;
    private String type;
    private String subType;
    private String category;
    private String subCategory;

    private String imagePrompt;
    private String imageUrl;
    private String slug;
    private LocalDateTime created;
    private LocalDateTime lastUpdated;

}
