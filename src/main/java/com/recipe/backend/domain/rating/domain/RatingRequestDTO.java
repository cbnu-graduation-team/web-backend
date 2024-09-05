package com.recipe.backend.domain.rating.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingRequestDTO {
    private Long recipeId;
    private Integer rating;
    private String comment;
}
