package com.recipe.backend.domain.recommend.repository;

import com.recipe.backend.domain.recommend.domain.Recommendation;
import com.recipe.backend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    Optional<List<Recommendation>> findByUser(User user);
}
