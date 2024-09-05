package com.recipe.backend.domain.recommend.controller;

import com.recipe.backend.domain.recommend.domain.dto.RecommendationDTO;
import com.recipe.backend.domain.recommend.service.RecommendService;
import com.recipe.backend.global.config.auth.JwtUtil;
import com.recipe.backend.global.response.ApiResponse;
import com.recipe.backend.global.response.SuccessMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommend")
public class RecommendController {
    private final RecommendService recommendService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<List<RecommendationDTO>>> recServerRecommendRecipes(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUserNameFromJwtToken(token);
        List<RecommendationDTO> recommendations = recommendService.recServerRecommendRecipes(username);
        return ApiResponse.success(SuccessMessages.RECOM_SUCCESS, recommendations);
    }
}
