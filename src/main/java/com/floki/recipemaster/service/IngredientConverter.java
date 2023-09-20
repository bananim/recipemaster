package com.floki.recipemaster.service;
import org.springframework.stereotype.Service;

import com.floki.recipemaster.dto.IngredientDTO;
import com.floki.recipemaster.entity.Ingredient;

@Service
public class IngredientConverter {
    public IngredientDTO ingredientToDTO(Ingredient ingredient) {
        return new IngredientDTO(ingredient.getIngredientId(), ingredient.getIngredientName());
    }
}