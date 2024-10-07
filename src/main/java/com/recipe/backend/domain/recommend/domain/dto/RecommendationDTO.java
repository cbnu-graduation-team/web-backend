package com.recipe.backend.domain.recommend.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public class RecommendationDTO {
    @JsonProperty("recipe_id")
    private Long recipeId;  // recipe_id와 일치해야 함
    private Double score;   // Flask 서버에서 반환하는 score에 대응

    // Getters and Setters
    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
