package com.floki.recipemaster.repository;
import org.springframework.data.repository.CrudRepository;

import com.floki.recipemaster.entity.RecipeIngredient;


public interface RecipeIngredientRepository extends CrudRepository<RecipeIngredient, String> {
}

