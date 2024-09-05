package com.recipe.backend.domain.recipe.controller;

import com.recipe.backend.domain.rating.domain.RatingRequestDTO;
import com.recipe.backend.domain.rating.domain.RecipeRatingResponseDTO;
import com.recipe.backend.domain.rating.service.RecipeRatingService;
import com.recipe.backend.domain.recipe.domain.RecipeAllResponseDTO;
import com.recipe.backend.domain.recipe.domain.RecipeDetailResponseDTO;
import com.recipe.backend.domain.recipe.service.RecipeService;
import com.recipe.backend.global.config.auth.JwtUtil;
import com.recipe.backend.global.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recipes")
public class RecipeController {
    private final RecipeRatingService recipeRatingService;
    private final RecipeService recipeService;
    private final RedisService redisService;
    private final JwtUtil jwtUtil;

    @GetMapping
    public String getRecipes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size, // 한 페이지에 9개 표시
            @RequestParam(required = false) String keyword, // 검색 키워드 추가
            Model model) {
        if (page < 0) {
            page = 0;  // 페이지 번호가 0보다 작은 경우 0으로 설정
        }
        Page<RecipeAllResponseDTO> recipes = recipeService.getRecipes(page, size, keyword);
        if (recipes.getTotalPages() == 0) {
            page = 0; // 검색 결과가 없으면 페이지를 0으로 설정
        }
        model.addAttribute("recipes", recipes);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", recipes.getTotalPages());
        model.addAttribute("keyword", keyword); // 검색 키워드를 모델에 추가
        return "recipe/recipeList";  // 렌더링할 타임리프 템플릿 이름
    }


    @GetMapping("/{id}")
    public String getRecipeDetail(
            @RequestParam(value = "token", required = false) String token,
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,  // 기본값 0 설정
            Model model) {

        String username = null;

        // token이 "null" 문자열이거나 비어있으면 null로 설정
        if (token != null && !token.equals("null") && !token.isEmpty()) {
            username = jwtUtil.getUserNameFromJwtToken(token);
        }

        if (page < 0) {
            page = 0;  // 페이지 번호가 0보다 작은 경우 0으로 설정
        }

        RecipeDetailResponseDTO recipeDetail = recipeService.getRecipeById(id);
        Page<RecipeRatingResponseDTO> ratings = recipeRatingService.getRatingsByRecipe(id, page, 5);

        if (username != null) {
            redisService.recordRecipeView(token, recipeDetail.getId());
        }

        model.addAttribute("recipe", recipeDetail);
        model.addAttribute("ratings", ratings);
        model.addAttribute("username", username);
        model.addAttribute("token", token);

        return "recipe/recipeDetail";  // 렌더링할 타임리프 템플릿 이름
    }


    @PostMapping("/{id}/rate")
    public String addRating(
            @PathVariable Long id,
            @RequestParam("rating") int rating,
            @RequestParam("comment") String comment,
            @RequestParam("token") String token,
            Model model) {

        String username = ""; // 필요시 토큰에서 사용자명 추출 또는 세션에서 추출
        if (token != null && !token.isEmpty()) {
            username = jwtUtil.getUserNameFromJwtToken(token);
        }

        RatingRequestDTO ratingRequestDTO = new RatingRequestDTO();
        ratingRequestDTO.setRecipeId(id);
        ratingRequestDTO.setRating(rating);
        ratingRequestDTO.setComment(comment);

        recipeRatingService.addRating(ratingRequestDTO, username);

        // 리뷰 작성 후 성공 메시지를 추가하고, 페이지를 다시 렌더링
        model.addAttribute("successMessage", "리뷰 작성이 완료되었습니다.");

        return "redirect:/recipes/" + id+ "?token=" + token;  // 리뷰 작성 후 레시피 상세 페이지로 리다이렉트
    }

    // RecipeController.java

    @PostMapping("/{id}/deleteReview")
    public String deleteReview(
            @PathVariable Long id,
            @RequestParam("reviewId") Long reviewId,
            @RequestParam("token") String token,
            Model model) {

        String username = "";
        if (token != null && !token.equals("null") && !token.isEmpty()) {
            username = jwtUtil.getUserNameFromJwtToken(token);
        }

        recipeRatingService.deleteRating(reviewId, username);

        model.addAttribute("successMessage", "리뷰가 삭제되었습니다.");

        // 토큰을 포함한 URL로 리다이렉트
        return "redirect:/recipes/" + id + "?token=" + token;
    }


}
