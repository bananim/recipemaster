package com.floki.recipemaster.dto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.floki.recipemaster.validation.ElementsNotNull;
import com.floki.recipemaster.validation.NotEmptyUnlessNull;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class CreateRecipeDTO {
    @NotNull
    @NotEmpty
    private final String name;

    @NotNull
    @NotEmpty
    private final String instructions;
    
    @NotNull
    private final Integer servings;

    @NotNull
    @NotEmpty
    @ElementsNotNull
    private final List<RecipeIngredientDTO> ingredients;

    @NotEmptyUnlessNull
    private final String category;

    @JsonCreator
    public CreateRecipeDTO(String name, String instructions, int servings, List<RecipeIngredientDTO> ingredients,
                           String category) {
        this.name = name;
        this.instructions = instructions;
        this.servings = servings;
        this.ingredients = ingredients;
        this.category = category;
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