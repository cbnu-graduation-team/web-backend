package com.recipe.backend.domain.recipe.repository;

import com.recipe.backend.domain.recipe.domain.Recipe;
import com.recipe.backend.domain.recipe.domain.RecipeAllResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Page<Recipe> findByTitleContaining(String titleKeyword, Pageable pageable);
}
