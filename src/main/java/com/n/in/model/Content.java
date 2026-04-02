package com.n.in.model;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.*;

@Entity
@Table(name = "content")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long executionId;

    @Column(columnDefinition = "TEXT")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 20)
    private String status;

    @Column(length = 20)
    private String type;

    @Column(length = 20)
    private String category;

    @Column(name = "sub_category", length = 20)
    private String subCategory;

    @Column(columnDefinition = "TEXT")
    private String imagePrompt;

    private String imageUrl;

    @Column(length = 255)
    private String slug;

    private LocalDateTime created;
    private LocalDateTime lastUpdated;
}
