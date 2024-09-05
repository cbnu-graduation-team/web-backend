package com.recipe.backend.domain.rating.controller;

import com.recipe.backend.domain.rating.domain.RatingRequestDTO;
import com.recipe.backend.domain.rating.domain.RecipeRating;
import com.recipe.backend.domain.rating.domain.RecipeRatingResponseDTO;
import com.recipe.backend.domain.rating.service.RecipeRatingService;
import com.recipe.backend.global.config.auth.JwtUtil;
import com.recipe.backend.global.response.ApiResponse;
import com.recipe.backend.global.response.SuccessMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {
    private final RecipeRatingService recipeRatingService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addRating(@RequestHeader("Authorization") String token, @RequestBody RatingRequestDTO ratingRequestDTO) {
        String username = jwtUtil.getUserNameFromJwtToken(token);
        recipeRatingService.addRating(ratingRequestDTO, username);
        return ApiResponse.success(SuccessMessages.CREATE_RATING_SUCCESS,"평가 작성 완료");
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<ApiResponse<List<RecipeRatingResponseDTO>>> getRatingsByRecipe(@PathVariable Long recipeId) {
        List<RecipeRatingResponseDTO> ratings = recipeRatingService.getRatingsByRecipe(recipeId);
        return ApiResponse.success(SuccessMessages.LOAD_RATINGS_SUCCESS, ratings);
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<RecipeRating>>> getRatingsByUser(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUserNameFromJwtToken(token);
        List<RecipeRating> ratings = recipeRatingService.getRatingsByUser(username);
        return ApiResponse.success(SuccessMessages.LOAD_RATINGS_SUCCESS, ratings);
    }
}
