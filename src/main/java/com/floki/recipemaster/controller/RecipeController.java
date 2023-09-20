package com.floki.recipemaster.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.floki.recipemaster.dto.CreateRecipeDTO;
import com.floki.recipemaster.dto.RecipeDTO;
import com.floki.recipemaster.dto.UpdateRecipeDTO;
import com.floki.recipemaster.entity.Recipe;
import com.floki.recipemaster.entity.RecipeCategory;
import com.floki.recipemaster.exception.InvalidParameterCombinationException;
import com.floki.recipemaster.repository.RecipeCategoryRepository;
import com.floki.recipemaster.repository.RecipeIngredientRepository;
import com.floki.recipemaster.repository.RecipeRepository;
import com.floki.recipemaster.service.RecipeConverter;
import com.floki.recipemaster.service.RecipeIngredientConverter;

import javax.validation.Valid;
import javax.validation.ValidationException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RestController
@CrossOrigin(origins = "*")
public class RecipeController {
    private final RecipeRepository recipeRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeCategoryRepository categoryRepository;

    private final RecipeIngredientConverter recipeIngredientConverter;
    private final RecipeConverter recipeConverter;

    @Autowired
    public RecipeController(RecipeRepository recipeRepository, RecipeIngredientRepository recipeIngredientRepository,
                            RecipeCategoryRepository categoryRepository,
                            RecipeIngredientConverter recipeIngredientConverter, RecipeConverter recipeConverter) {
        this.recipeRepository = recipeRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.categoryRepository = categoryRepository;
        this.recipeIngredientConverter = recipeIngredientConverter;
        this.recipeConverter = recipeConverter;
    }

    @GetMapping("/api/recipes/queries")
    public ResponseEntity<Collection<RecipeDTO>> getRecipes(
            @RequestParam(name = "name", required = false) String nameQuery,
            @RequestParam(name = "ingredient", required = false) String ingredient,
            @RequestParam(name = "categories", required = false) List<String> categories,
            @RequestParam(name = "servings", required = false) Integer servings,
            @RequestParam(name = "matchInstruction", required = false) String matchInstruction){
        if (invalidParamCombination(nameQuery, ingredient, categories, servings, matchInstruction)) {
            throw new InvalidParameterCombinationException(
                    "Parameters 'query', 'ingredient', 'categories', 'servings', matchInstruction cannot be combined. " +
                    "Please only specify one of them."
            );
        }
        Iterable<Recipe> results;

        if (nameQuery != null) {
            results = recipeRepository.findByRecipeNameContainingIgnoreCase(nameQuery);
        } else if (ingredient != null) {
            results = recipeRepository.findByIngredientName(ingredient);
        } else if (categories != null) {
            results = recipeRepository.findByCategoriesContainsAny(categories);
        } else if (servings != null) {
              results = recipeRepository.findByServings(servings);
        }else if (matchInstruction != null) {
          results = recipeRepository.findByInstructionsContainingIgnoreCase(matchInstruction);
        } else {
            results = recipeRepository.findAll();
        }

        return ResponseEntity.ok(StreamSupport
                .stream(results.spliterator(), false)
                .map(recipeConverter::recipeToDTO)
                .collect(Collectors.toList()));
    }
    
    @GetMapping("/api/recipes")
    public ResponseEntity<Collection<RecipeDTO>> getRecipesByCustomSearch(
        @RequestParam(name = "servings", required = false) Integer servings,
        @RequestParam(name = "includeIngredient", required = false) String includeIngredient,
        @RequestParam(name = "excludeIngredient", required = false) String excludeIngredient,
        @RequestParam(name = "matchInstruction", required = false) String matchInstruction)
    {

      Iterable<Recipe> results;

      if (servings != null && includeIngredient != null) {
        results = recipeRepository.findByServings(servings);
        return ResponseEntity.ok(
            StreamSupport.stream(results.spliterator(), false)
            .filter(rec -> { return rec.getIngredients().stream()
                .anyMatch(ring -> {
                  return includeIngredient.equals(ring.getIngredient().getIngredientName());
          });
        }).map(recipeConverter::recipeToDTO).collect(Collectors.toList()));
      }
      
      if (matchInstruction != null && excludeIngredient != null) {
        results = recipeRepository.findByInstructionsContainingIgnoreCase(matchInstruction);
        return ResponseEntity.ok(
            StreamSupport.stream(results.spliterator(), false)
            .filter(recp -> { 
             return  recp.getIngredients().stream()
              .filter(item -> {return excludeIngredient.equals(item.getIngredient().getIngredientName());}).collect(Collectors.toList()).isEmpty();
        }).map(recipeConverter::recipeToDTO).collect(Collectors.toList()));
      }
      return ResponseEntity.notFound().build();

    }
    

    @GetMapping("/api/recipes/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable int id) {
        return recipeRepository
                .findById(id)
                .map(recipeConverter::recipeToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/recipes")
    public ResponseEntity<Void> createRecipe(@Valid @RequestBody CreateRecipeDTO dto, BindingResult bind) {
        if (bind.hasErrors()) {
            throw new ValidationException(bind
                    .getFieldErrors()
                    .stream()
                    .map(fieldError -> String.format("%s - %s", fieldError.getField(), fieldError.getDefaultMessage()))
                    .collect(Collectors.joining(", "))
            );
        }

        Recipe recipe = new Recipe(dto.getName(), dto.getInstructions(), dto.getServings());

        recipe.setIngredients(dto
                .getIngredients()
                .stream()
                .map(ingredientDTO -> recipeIngredientConverter.dtoToRecipeIngredient(ingredientDTO, recipe))
                .collect(Collectors.toList()));

        recipe.setCategory(categoryRepository
                .findByCategoryEqualsIgnoreCase(dto.getCategory())
                .orElse(new RecipeCategory(dto.getCategory())));

        recipeRepository.save(recipe);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/recipes/{id}")
    public ResponseEntity<String> updateRecipe(@PathVariable Integer id, @Valid @RequestBody UpdateRecipeDTO dto,
                                               BindingResult bind) {
        if (bind.hasErrors()) {
            throw new ValidationException(bind
                    .getFieldErrors()
                    .stream()
                    .map(fieldError -> String.format("%s - %s", fieldError.getField(), fieldError.getDefaultMessage()))
                    .collect(Collectors.joining(", "))
            );
        }

        Optional<Recipe> recipe = recipeRepository.findById(id);

        if (!recipe.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        if (dto.getName() != null) {
            recipe.get().setRecipeName(dto.getName());
        }

        if (dto.getInstructions() != null) {
            recipe.get().setInstructions(dto.getInstructions());
        }
        
        if (dto.getServings() >= 0) {
          recipe.get().setServings(dto.getServings());
      }

        if (dto.getIngredients() != null) {
            recipe.get().clearIngredients();
            recipe.get().getIngredients().addAll(dto.getIngredients().stream()
                    .map(ingredientDTO -> recipeIngredientConverter.dtoToRecipeIngredient(ingredientDTO, recipe.get()))
                    .collect(Collectors.toList()));
        }

        if (dto.getCategory() != null) {
            recipe.get().setCategory(categoryRepository
                    .findByCategoryEqualsIgnoreCase(dto.getCategory())
                    .orElse(new RecipeCategory(dto.getCategory())));
        }

        recipeRepository.save(recipe.get());

        return ResponseEntity.ok(String.format("Recipe with id %d updated succesfully.", id));
    }

    @DeleteMapping("/api/recipes/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Integer id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);

        if (!recipe.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        recipeRepository.delete(recipe.get());

        return ResponseEntity.ok(String.format("Deleted recipe with id %d.", id));
    }

    private boolean invalidParamCombination(String nameQuery, String ingredient, List<String> categories, Integer servings, String matchInstruction) {
        return (nameQuery != null && ingredient != null) ||
               (nameQuery != null && categories != null) ||
               (ingredient != null && categories != null) ||
               (nameQuery != null && servings != null) ||
               (ingredient != null && servings != null) ||
               (categories != null && servings != null) ||
               (nameQuery != null &&  matchInstruction != null) ||
               (ingredient != null && matchInstruction != null) ||
               (categories != null && matchInstruction != null)||
               (matchInstruction != null && servings != null);
    }
}