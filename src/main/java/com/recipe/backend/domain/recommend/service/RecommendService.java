package com.recipe.backend.domain.recommend.service;

import com.recipe.backend.domain.recommend.domain.dto.RecommendationDTO;
import com.recipe.backend.domain.user.domain.User;
import com.recipe.backend.domain.user.repository.UserRepository;
import com.recipe.backend.global.response.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;

    public List<RecommendationDTO> recServerRecommendRecipes(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));
        String url = "http://localhost:5000/recommend/" + user.getId(); // Flask 서버의 엔드포인트
        RecommendationDTO[] recommendations = restTemplate.getForObject(url, RecommendationDTO[].class);
        return Arrays.asList(recommendations);
    }
}
