package com.n.in.model.mapper;

import com.n.in.model.Content;
import com.n.in.model.dto.ContentDto;
import org.springframework.stereotype.Component;

@Component
public class ContentMapper {

    public ContentDto toDto(Content content) {
        if (content == null) {
            throw new IllegalArgumentException("Content must not be null");
        }
        return ContentDto.builder()
                .id(content.getId())
                .executionId(content.getExecutionId())
                .title(content.getTitle())
                .shortDescription(content.getShortDescription())
                .status(content.getStatus())
                .message(content.getMessage())
                .category(content.getCategory())
                .subCategory(content.getSubCategory())
                .imageUrl(content.getImageUrl())
                .imagePrompt(content.getImagePrompt())
                .slug(content.getSlug())
                .created(content.getCreated())
                .lastUpdated(content.getLastUpdated())
                .build();
    }

    public Content toEntity(ContentDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("ContentDto must not be null");
        }
        return Content.builder()
                .id(dto.getId())
                .executionId(dto.getExecutionId())
                .status(dto.getStatus())
                .title(dto.getTitle())
                .shortDescription(dto.getShortDescription())
                .message(dto.getMessage())
                .category(dto.getCategory())
                .subCategory(dto.getSubCategory())
                .imagePrompt(dto.getImagePrompt())
                .imageUrl(dto.getImageUrl())
                .slug(dto.getSlug())
                .created(dto.getCreated())
                .lastUpdated(dto.getLastUpdated())
                .build();
    }
}
