package com.floki.recipemaster.dto;
import java.util.Collection;

public class RecipeDTO {
    private final Integer id;
    private final String name;
    private final String instructions;
    private Integer servings;
    private final Collection<RecipeIngredientDTO> ingredients;
    private final RecipeCategoryDTO category;

    public RecipeDTO(Integer id, String name, String instructions, Integer servings,
                     Collection<RecipeIngredientDTO> ingredients,
                     RecipeCategoryDTO category) {
        this.id = id;
        this.name = name;
        this.instructions = instructions;
        this.servings =  servings;
        this.ingredients = ingredients;
        this.category = category;
    }

    public Integer getId() {
        return id;
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

    public Collection<RecipeIngredientDTO> getIngredients() {
        return ingredients;
    }

    public RecipeCategoryDTO getCategory() {
        return category;
    }
}