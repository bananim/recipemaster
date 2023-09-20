package com.floki.recipemaster.dto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.floki.recipemaster.validation.ElementsNotNull;
import com.floki.recipemaster.validation.NotEmptyUnlessNull;

import java.util.List;

import javax.validation.constraints.NotNull;

public class UpdateRecipeDTO {
    @NotEmptyUnlessNull
    private final String name;

    @NotEmptyUnlessNull
    private final String instructions;
    
    @NotNull
    private final Integer servings;

    @NotEmptyUnlessNull
    @ElementsNotNull
    private final List<RecipeIngredientDTO> ingredients;

    @NotEmptyUnlessNull
    private final String category;

    @JsonCreator
    public UpdateRecipeDTO(String name, String instructions, int servings, List<RecipeIngredientDTO> ingredients, String category) {
        this.name = name;
        this.instructions = instructions;
        this.ingredients = ingredients;
        this.category = category;
        this.servings = servings;
    }

    public String getName() {
        return name;
    }

    public String getInstructions() {
        return instructions;
    }
    
    public Integer getServings() {
      return servings;
  }

    public List<RecipeIngredientDTO> getIngredients() {
        return ingredients;
    }

    public String getCategory() {
        return category;
    }
}