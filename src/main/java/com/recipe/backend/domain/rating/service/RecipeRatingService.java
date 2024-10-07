package com.recipe.backend.domain.rating.service;

import com.recipe.backend.domain.rating.domain.RatingRequestDTO;
import com.recipe.backend.domain.rating.domain.RecipeRating;
import com.recipe.backend.domain.rating.domain.RecipeRatingResponseDTO;
import com.recipe.backend.domain.rating.repository.RecipeRatingRepository;
import com.recipe.backend.domain.recipe.domain.Recipe;
import com.recipe.backend.domain.recipe.domain.RecipeDetailResponseDTO;
import com.recipe.backend.domain.recipe.repository.RecipeRepository;
import com.recipe.backend.domain.user.domain.User;
import com.recipe.backend.domain.user.repository.UserRepository;
import com.recipe.backend.global.response.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeRatingService {
    private final RecipeRatingRepository recipeRatingRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional
    public RecipeRating addRating(RatingRequestDTO recipeRatingDTO, String username) {
        Recipe recipe = recipeRepository.findById(recipeRatingDTO.getRecipeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid recipe ID"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));


        RecipeRating recipeRating = new RecipeRating();
        recipeRating.setRecipe(recipe);
        recipeRating.setUser(user);
        recipeRating.setRating(recipeRatingDTO.getRating());
        recipeRating.setComment(recipeRatingDTO.getComment());
        recipeRating.setRatingDate(LocalDateTime.now());

        return recipeRatingRepository.save(recipeRating);
    }

    public List<RecipeRatingResponseDTO> getRatingsByRecipe(Long recipeId) {
        List<RecipeRatingResponseDTO> answer = new ArrayList<>();
        List<RecipeRating> byRecipeId = recipeRatingRepository.findByRecipeId(recipeId);
        for(RecipeRating tmp:byRecipeId){
            RecipeRatingResponseDTO recipeRatingResponseDTO = RecipeRatingResponseDTO.builder()
                    .recipe_id(recipeId)
                    .username(tmp.getUser().getUsername())
                    .rating(tmp.getRating())
                    .comment(tmp.getComment())
                    .build();
            answer.add(recipeRatingResponseDTO);
        }
        return answer;
    }

    public List<RecipeRating> getRatingsByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));
        return recipeRatingRepository.findByUser(user);
    }

    public Page<RecipeRatingResponseDTO> getRatingsByRecipe(Long recipeId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return recipeRatingRepository.findByRecipeId(recipeId, pageRequest)
                .map(rating -> new RecipeRatingResponseDTO(
                        rating.getId(),
                        rating.getRecipe().getId(),
                        rating.getUser().getUsername(),
                        rating.getRating(),
                        rating.getComment()
                ));
    }

    @Transactional
    public void deleteRating(Long reviewId, String username) {
        RecipeRating rating = recipeRatingRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid review ID"));

        // 리뷰 작성자가 현재 로그인한 사용자와 일치하는지 확인
        if (!rating.getUser().getUsername().equals(username)) {
            throw new IllegalArgumentException("You do not have permission to delete this review.");
        }

        recipeRatingRepository.delete(rating);
    }

    public List<RecipeDetailResponseDTO> getUserReviewedRecipes(String username) {
// 사용자 정보를 가져옴
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        // 사용자가 남긴 평가 목록을 가져옴
        List<RecipeRating> userRatings = recipeRatingRepository.findByUserId(user.getId());

        // 각 평가에 대해 레시피 정보를 조회하고 DTO로 변환
        return userRatings.stream()
                .map(rating -> {
                    // 평가한 레시피 정보 가져오기
                    Recipe recipe = recipeRepository.findById(rating.getRecipe().getId()).get();

                    // Recipe와 Rating 정보를 결합하여 RecipeRatingResponseDTO 생성
                    return new RecipeDetailResponseDTO(recipe);
                })
                .collect(Collectors.toList());
    }
}
