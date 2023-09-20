package com.floki.recipemaster.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.floki.recipemaster.dto.RecipeIngredientDTO;
import com.floki.recipemaster.entity.Ingredient;
import com.floki.recipemaster.entity.Recipe;
import com.floki.recipemaster.entity.RecipeIngredient;
import com.floki.recipemaster.repository.IngredientRepository;
@Service
public class RecipeIngredientConverter {
    private final IngredientRepository repository;

    @Autowired
    public RecipeIngredientConverter(IngredientRepository repository) {
        this.repository = repository;
    }

    public RecipeIngredient dtoToRecipeIngredient(RecipeIngredientDTO dto, Recipe recipe) {
        return new RecipeIngredient(dto.getAmount(), dto.getMeasurement(),
                repository
                        .findByIngredientNameEqualsIgnoreCase(dto.getName())
                        .orElse(new Ingredient(dto.getName())),
                recipe);
    }

    public RecipeIngredientDTO recipeIngredientToDTO(RecipeIngredient recipeIngredient) {
        return new RecipeIngredientDTO(recipeIngredient.getIngredient().getIngredientId(),
                recipeIngredient.getIngredient().getIngredientName(),
                recipeIngredient.getMeasurement(), recipeIngredient.getAmount());
    }
}