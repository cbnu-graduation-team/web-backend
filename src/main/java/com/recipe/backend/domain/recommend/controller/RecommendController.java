package com.recipe.backend.domain.recommend.controller;

import com.recipe.backend.domain.recipe.domain.RecipeAllResponseDTO;
import com.recipe.backend.domain.recipe.domain.RecipeDetailResponseDTO;
import com.recipe.backend.domain.recipe.service.RecipeService;
import com.recipe.backend.domain.recommend.domain.dto.RecommendationDTO;
import com.recipe.backend.domain.recommend.service.RecommendService;
import com.recipe.backend.global.config.auth.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendController {
    private final RecommendService recommendService;
    private final JwtUtil jwtUtil;
    private final RecipeService recipeService;

    @GetMapping("/recommendations")
    public String getRecommendations(@RequestParam("token") String token, Model model) {
        String username = jwtUtil.getUserNameFromJwtToken(token);
        List<RecommendationDTO> recommendations = recommendService.recServerRecommendRecipes(username);
        List<RecipeDetailResponseDTO> recommendedRecipes = new ArrayList<>();

        for (RecommendationDTO recommendation : recommendations) {
            // 각 추천의 recipe_id로 레시피 정보를 조회
            RecipeDetailResponseDTO recipe = recipeService.getRecipeById(recommendation.getRecipeId());
            if (recipe != null) {
                recommendedRecipes.add(recipe);
            }
        }
        // 추천받은 레시피를 DB에 저장
        List<Long> recipeIds = recommendations.stream()
                .map(RecommendationDTO::getRecipeId)
                .toList();
        recommendService.saveRecommendations(username, recipeIds); // 추천 저장
        // 추천 결과를 모델에 추가
        model.addAttribute("recommendations", recommendations);
        model.addAttribute("recommendedRecipes", recommendedRecipes);

        // 타임리프 템플릿을 사용하여 페이지 렌더링
        return "recommend/recommendations";
    }


}
