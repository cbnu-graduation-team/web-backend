package com.recipe.backend.domain.recipe.domain;

import com.recipe.backend.domain.ingredient.domain.Ingredient;
import com.recipe.backend.domain.rating.domain.RecipeRating;
import com.recipe.backend.domain.recipe_ingredient.domain.RecipeIngredient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="recipe_id")
    private Long id;
    @Column(length = 500)
    private String title;
    @Column(length = 1000)
    private String description;
    @Column(length = 50)
    private String servings;
    @Column(length = 50)
    private String time_required;
    @Column(length = 50)
    private String difficulty;
    @Column(length = 500)
    private String thumbnail;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeStep> steps = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeRating> ratings = new ArrayList<>();
    // Helper method to add a RecipeStep to this Recipe
    public void addStep(RecipeStep step) {
        steps.add(step);
        step.setRecipe(this);
    }

    // Helper method to add a RecipeIngredient to this Recipe
    public void addIngredient(RecipeIngredient recipeIngredient) {
        recipeIngredients.add(recipeIngredient);
        recipeIngredient.setRecipe(this);
    }
}
