package com.recipe.backend.domain.recommend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String thumbnail;
}

