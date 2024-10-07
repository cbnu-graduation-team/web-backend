package com.recipe.backend.domain.recommend.service;

import com.recipe.backend.domain.recipe.domain.Recipe;
import com.recipe.backend.domain.recipe.domain.RecipeDetailResponseDTO;
import com.recipe.backend.domain.recipe.repository.RecipeRepository;
import com.recipe.backend.domain.recipe.service.RecipeService;
import com.recipe.backend.domain.recommend.domain.Recommendation;
import com.recipe.backend.domain.recommend.domain.dto.RecommendationDTO;
import com.recipe.backend.domain.recommend.repository.RecommendationRepository;
import com.recipe.backend.domain.user.domain.User;
import com.recipe.backend.domain.user.repository.UserRepository;
import com.recipe.backend.domain.user.service.UserService;
import com.recipe.backend.domain.user.service.UserServiceImpl;
import com.recipe.backend.global.response.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final RecommendationRepository recommendationRepository;
    private final RecipeRepository recipeRepository;

    public List<RecommendationDTO> recServerRecommendRecipes(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));
        String url = "http://localhost:5000/recommend/" + user.getId(); // Flask 서버의 엔드포인트
        RecommendationDTO[] recommendations = restTemplate.getForObject(url, RecommendationDTO[].class);
        return Arrays.asList(recommendations);
    }

    public void saveRecommendations(String username, List<Long> recipeIds) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));
        for (Long recipeId : recipeIds) {
            Recipe recipe = recipeRepository.getRecipeById(recipeId); // 레시피 정보 조회
            if (recipe != null) {
                // 이미 추천된 레시피인지 확인
                Optional<Recommendation> existingRecommendation = recommendationRepository.findByUserAndRecipe(user, recipe);
                if (existingRecommendation.isPresent()) {
                    // 이미 추천된 경우, 저장하지 않음
                    continue;
                }
                // 추천 정보를 저장
                Recommendation recommendation = new Recommendation();
                recommendation.setUser(user);
                recommendation.setRecipe(recipe);
                recommendation.setRecommendedDate(LocalDateTime.now());
                recommendationRepository.save(recommendation);
            }
        }
    }

    public List<RecipeDetailResponseDTO> getRecommendedRecipes(String username) {
        // 사용자 정보를 가져옴
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        // 추천 테이블에서 사용자에 대한 추천 레시피 목록 가져오기
        List<Recommendation> recommendedRecipes = recommendationRepository.findByUserId(user.getId());
        return recommendedRecipes.stream()
                .map(recommendation -> {
                    // 각 추천 레시피의 ID로 레시피를 찾기
                    Recipe recipe = recipeRepository.findById(recommendation.getRecipe().getId()).get();

                    // Recipe 객체를 RecipeDetailResponseDTO로 변환
                    return new RecipeDetailResponseDTO(recipe);
                })
                .collect(Collectors.toList());
    }
}
