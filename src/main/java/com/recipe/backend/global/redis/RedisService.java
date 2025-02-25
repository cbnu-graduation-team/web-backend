package com.recipe.backend.global.redis;

import com.recipe.backend.domain.user.domain.User;
import com.recipe.backend.domain.user.repository.UserRepository;
import com.recipe.backend.global.config.auth.JwtUtil;
import com.recipe.backend.global.response.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // RedisService.java
    public void recordRecipeView(String token, Long recipeId) {
        String username = jwtUtil.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));
        String userKey = "user:" + user.getId() + ":views";
        String value = String.valueOf(recipeId);

        ListOperations<String, String> listOps = redisTemplate.opsForList();
        listOps.rightPush(userKey, value);  // 사용자별 조회 기록 추가

        // 사용자별 조회 기록의 만료 시간 설정
        redisTemplate.expire(userKey, 30, TimeUnit.DAYS);

        // 전역 조회수 Sorted Set 업데이트
        redisTemplate.opsForZSet().incrementScore("recipes:global_views", String.valueOf(recipeId), 1);
    }

}
