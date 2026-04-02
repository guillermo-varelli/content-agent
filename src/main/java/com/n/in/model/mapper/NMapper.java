package com.n.in.model.mapper;


import com.n.in.model.dto.ContentDto;
import com.n.in.model.Content;
import org.springframework.stereotype.Component;

@Component
public class NMapper {

    public ContentDto toDto(Content e) {
        if (e == null) return null;
        return ContentDto.builder()
                .id(e.getId())
                .shortDescription(e.getShortDescription())
                .status(e.getStatus())
                .message(e.getMessage())
                .type(e.getType())
                .category(e.getCategory())
                .subCategory(e.getSubCategory())
                .imageUrl(e.getImageUrl())
                .imagePrompt(e.getImagePrompt())
                .slug(e.getSlug())
                .created(e.getCreated())
                .lastUpdated(e.getLastUpdated())
                .build();
    }

    public Content toEntity(ContentDto d) {
        if (d == null) return null;
        return Content.builder()
                .id(d.getId())
                .executionId(d.getExecutionId())
                .status(d.getStatus())
                .title(d.getTitle())
                .shortDescription(d.getShortDescription())
                .message(d.getMessage())
                .type(d.getType())
                .category(d.getCategory())
                .subCategory(d.getSubCategory())
                .imagePrompt(d.getImagePrompt())
                .imageUrl(d.getImageUrl())
                .slug(d.getSlug())
                .created(d.getCreated())
                .lastUpdated(d.getLastUpdated())
                .build();
    }
}
