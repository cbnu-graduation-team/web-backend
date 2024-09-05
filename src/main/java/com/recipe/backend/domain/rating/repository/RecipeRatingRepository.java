package com.recipe.backend.domain.rating.repository;

import com.recipe.backend.domain.rating.domain.RecipeRating;
import com.recipe.backend.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRatingRepository extends JpaRepository<RecipeRating, Long> {
    List<RecipeRating> findByRecipeId(Long recipeId);
    List<RecipeRating> findByUser(User user);

    Page<RecipeRating> findByRecipeId(Long recipeId, Pageable pageable);
}
