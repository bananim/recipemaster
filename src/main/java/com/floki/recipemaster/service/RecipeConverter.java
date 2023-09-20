package com.floki.recipemaster.service;
import org.springframework.stereotype.Service;

import com.floki.recipemaster.dto.RecipeDTO;
import com.floki.recipemaster.entity.Recipe;

import java.util.stream.Collectors;

@Service
public class RecipeConverter {
    private final RecipeIngredientConverter ingredientConverter;
    private final RecipeCategoryConverter categoryConverter;

    public RecipeConverter(RecipeIngredientConverter ingredientConverter, RecipeCategoryConverter categoryConverter) {
        this.ingredientConverter = ingredientConverter;
        this.categoryConverter = categoryConverter;
    }

    public RecipeDTO recipeToDTO(Recipe recipe) {
        return new RecipeDTO(
                recipe.getRecipeId(),
                recipe.getRecipeName(),
                recipe.getInstructions(),
                recipe.getServings(),
                recipe.getIngredients()
                        .stream()
                        .map(ingredientConverter::recipeIngredientToDTO)
                        .collect(Collectors.toList()),
                categoryConverter.recipeCategoryToDTO(recipe.getCategory()));
    }
}