package com.floki.recipemaster.repository;
import org.springframework.data.repository.CrudRepository;

import com.floki.recipemaster.entity.RecipeCategory;

import java.util.Optional;

public interface RecipeCategoryRepository extends CrudRepository<RecipeCategory, Integer> {
    Optional<RecipeCategory> findByCategoryEqualsIgnoreCase(String name);
}