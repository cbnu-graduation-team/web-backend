package com.recipe.backend.domain.rating.domain;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RecipeRatingResponseDTO {
    private Long id;  // 이 필드를 추가
    private Long recipe_id;
    private String username;
    private Integer rating;
    private String comment;
}
