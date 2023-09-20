package com.floki.recipemaster.repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.floki.recipemaster.entity.Recipe;

import java.util.Collection;
import java.util.Set;

public interface RecipeRepository extends CrudRepository<Recipe, Integer> {
    /**
     * Find all recipes which name contains the specified query, case insensitive.
     * @param query search query for recipe name.
     * @return A set containing all recipes which name contains 'query'.
     */
    Set<Recipe> findByRecipeNameContainingIgnoreCase(String query);

    /**
     * Find all recipes that contains ingredients with the specified name. The match is exact and case insensitive.
     * @param ingredientName name of the ingredient to search recipes by.
     * @return A set containing all recipes that contain the specified ingredient.
     */
    @Query("SELECT r FROM Recipe r JOIN FETCH r.ingredients AS ri WHERE UPPER(ri.ingredient.ingredientName) = UPPER(:name)")
    Set<Recipe> findByIngredientName(@Param("name") String ingredientName);
    
    /**
     * Find all recipes that contains servings with the specified servings. The match is exact.
     * @param servings servings of the recipe to search recipes by.
     * @return A set containing all recipes that contain the specified servings.
     */
    Set<Recipe> findByServings(Integer servings);
    
    /**
     * Find all recipes that contains matching instruction with the specified instruction. The match is exact.
     * @param matchingInstruction instructions of the recipe to search recipes by.
     * @return A set containing all recipes that contain the specified instruction.
     */
    Set<Recipe> findByInstructionsContainingIgnoreCase(String instruction);


    /**
     * Find all recipes that are categorized with the specified category.
     * @param category Name of category to search recipes by.
     * @return A set containing all recipes with the specified category.
     */
    @Query("SELECT r FROM Recipe r JOIN FETCH r.category AS rc WHERE UPPER(rc.category) = UPPER(:category)")
    Set<Recipe> findByCategory(@Param("category") String category);

    /**
     * Find all recipes that contain one or more of the specified categories.
     * @param categories Collection of categories to match recipes by.
     * @return A set containing all recipes that contain one or more of the specified categories.
     */
    @Query("SELECT r FROM Recipe r JOIN FETCH r.category AS rc WHERE rc.category IN (:categories)")
    Set<Recipe> findByCategoriesContainsAny(@Param("categories") Collection<String> categories);
    
    /**
     * Find all recipes that contain servings and one or more of the specified ingredients.
     * @param servings servings for the recipe
     * @param  Collection of ingredients to match recipes by.
     * @return A set containing all recipes that contain specified servings and one or more of the specified ingredients.
     */
    @Query("SELECT r FROM Recipe r JOIN FETCH r.ingredients AS ri WHERE r.servings = (:servings) and ri.ingredient.ingredientName IN (:ingredients)")
    Set<Recipe> findByServingsAndIngredients(@Param("servings") Integer servings, @Param("ingredients") Collection<String> ingredients);
    
}