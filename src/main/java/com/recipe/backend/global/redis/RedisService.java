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

    public void recordRecipeView(String token, Long recipeId) {
        String username = jwtUtil.getUserNameFromJwtToken(token);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));
        String key = "user:" + user.getId() + ":views";
        String value = String.valueOf(recipeId);

        ListOperations<String, String> listOps = redisTemplate.opsForList();
        listOps.rightPush(key, value);  // 리스트의 오른쪽에 값을 추가

        // 만료 시간을 설정하려면 아래 코드를 사용
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }
}
