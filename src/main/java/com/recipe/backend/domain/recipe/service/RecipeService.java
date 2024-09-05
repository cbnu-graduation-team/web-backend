package com.recipe.backend.domain.recipe.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipe.backend.domain.ingredient.domain.Ingredient;
import com.recipe.backend.domain.ingredient.repository.IngredientRepository;
import com.recipe.backend.domain.recipe.domain.Recipe;
import com.recipe.backend.domain.recipe.domain.RecipeAllResponseDTO;
import com.recipe.backend.domain.recipe.domain.RecipeDetailResponseDTO;
import com.recipe.backend.domain.recipe.domain.RecipeStep;
import com.recipe.backend.domain.recipe.repository.RecipeRepository;
import com.recipe.backend.domain.recipe.repository.RecipeStepRepository;
import com.recipe.backend.domain.recipe_ingredient.domain.RecipeIngredient;
import com.recipe.backend.domain.recipe_ingredient.repository.RecipeIngredientRepository;
import com.recipe.backend.global.util.recipedata.IngredientDTO;
import com.recipe.backend.global.util.recipedata.RecipeDTO;
import com.recipe.backend.global.util.recipedata.StepDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeStepRepository recipeStepRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientRepository ingredientRepository;

    public void loadAndSaveRecipes(String jsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<com.recipe.backend.global.util.recipedata.RecipeDTO> recipes = objectMapper.readValue(new File(jsonFilePath),
                new TypeReference<List<RecipeDTO>>() {});

        for (com.recipe.backend.global.util.recipedata.RecipeDTO dto : recipes) {
            Recipe recipe = new Recipe();
            recipe.setTitle(dto.getTitle());
            System.out.println(dto.getTitle());
            recipe.setDescription(dto.getDescription());
            recipe.setServings(dto.getServings());
            recipe.setTime_required(dto.getTime_required());
            recipe.setDifficulty(dto.getDifficulty());
            recipe.setThumbnail(dto.getThumbnail());

            for (IngredientDTO ingredientDto : dto.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findByName(ingredientDto.getName())
                        .orElseGet(() -> {
                            Ingredient newIngredient = new Ingredient();
                            newIngredient.setName(ingredientDto.getName());
                            ingredientRepository.save(newIngredient);
                            return newIngredient;
                        });

                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setRecipe(recipe);
                recipeIngredient.setIngredient(ingredient);
                recipe.addIngredient(recipeIngredient);
            }

            for (StepDTO stepDto : dto.getSteps()) {
                RecipeStep step = new RecipeStep();
                step.setDescription(stepDto.getDescription());
                step.setImage(stepDto.getImage());
                recipe.addStep(step);
            }
            recipeRepository.save(recipe);
        }
    }
    public Page<RecipeAllResponseDTO> getRecipes(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);

        if (keyword == null || keyword.isEmpty()) {
            // 검색어가 없을 경우, 전체 레시피 반환
            return recipeRepository.findAll(pageable).map(this::toDto);
        } else {
            // 검색어가 있을 경우, 제목 또는 설명에 키워드가 포함된 레시피 필터링
            return recipeRepository.findByTitleContaining(keyword, pageable)
                    .map(this::toDto);
        }
    }

    private RecipeAllResponseDTO toDto(Recipe recipe) {
        return new RecipeAllResponseDTO(recipe.getId(), recipe.getTitle(), recipe.getThumbnail(), recipe.getDescription());
    }


    public RecipeDetailResponseDTO getRecipeById(Long id) {
        Optional<Recipe> recipeOptional = recipeRepository.findById(id);
        if (recipeOptional.isPresent()) {
            Recipe recipe = recipeOptional.get();
            RecipeDetailResponseDTO dto = new RecipeDetailResponseDTO(recipe);
            dto.setSteps(recipeStepRepository.findByRecipeId(id));
            dto.setIngredients(recipeIngredientRepository.findByRecipeId(id));
            return dto;
        } else {
            throw new IllegalArgumentException("존재하지 않는 레시피입니다.");
        }
    }

}

