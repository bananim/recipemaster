package com.floki.recipemaster.service;
import org.springframework.stereotype.Service;

import com.floki.recipemaster.dto.RecipeCategoryDTO;
import com.floki.recipemaster.entity.RecipeCategory;

@Service
public class RecipeCategoryConverter {
    public RecipeCategoryDTO recipeCategoryToDTO(RecipeCategory category) {
        return new RecipeCategoryDTO(category.getRecipeCategoryId(), category.getCategory());
    }
}