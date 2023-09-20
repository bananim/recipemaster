package com.floki.recipemaster.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.floki.recipemaster.dto.CreateIngredientDTO;
import com.floki.recipemaster.dto.IngredientDTO;
import com.floki.recipemaster.entity.Ingredient;
import com.floki.recipemaster.entity.Recipe;
import com.floki.recipemaster.exception.UniquenessViolationException;
import com.floki.recipemaster.repository.IngredientRepository;
import com.floki.recipemaster.repository.RecipeRepository;
import com.floki.recipemaster.service.IngredientConverter;

import javax.validation.Valid;
import javax.validation.ValidationException;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@CrossOrigin(origins = "*")
public class IngredientController {

    private final IngredientRepository ingredientRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientConverter converter;

    @Autowired
    public IngredientController(IngredientRepository ingredientRepository, RecipeRepository recipeRepository,
                                IngredientConverter converter) {
        this.ingredientRepository = ingredientRepository;
        this.recipeRepository = recipeRepository;
        this.converter = converter;
    }

    @GetMapping("/api/ingredients")
    public ResponseEntity<Collection<IngredientDTO>> getIngredients(
            @RequestParam(name = "query", required = false) String query
    ) {
        Iterable<Ingredient> ingredients = query != null ?
                        ingredientRepository.findByIngredientNameContainingIgnoreCase(query) :
                        ingredientRepository.findAll();

        return ResponseEntity.ok(StreamSupport.stream(ingredients.spliterator(), false)
                .map(converter::ingredientToDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/api/ingredients/{id}")
    public ResponseEntity<IngredientDTO> getIngredients(@PathVariable("id") Integer id) {
        return ingredientRepository
                .findById(id)
                .map(converter::ingredientToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/ingredients")
    public ResponseEntity<Void> createIngredient(@Valid @RequestBody CreateIngredientDTO dto, BindingResult bind) {
        if (bind.hasErrors()) {
            throw new ValidationException("Value for 'name' must not be empty.");
        }

        if (ingredientRepository.findByIngredientNameEqualsIgnoreCase(dto.getName()).isPresent()) {
            throw new UniquenessViolationException(String.format("An ingredient with name '%s' already exists.",
                    dto.getName()));
        }

        ingredientRepository.save(new Ingredient(dto.getName()));

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/ingredients/{id}")
    public ResponseEntity<String> deleteIngredient(@PathVariable Integer id) {
        Optional<Ingredient> toDelete = ingredientRepository.findById(id);

        if (!toDelete.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Collection<Recipe> recipesContainingIngredient =
                recipeRepository.findByIngredientName(toDelete.get().getIngredientName());

        if (recipesContainingIngredient.size() > 0) {
            return ResponseEntity.unprocessableEntity().body(String.format(
                    "Cannot delete ingredient since it is in use by the following recipes: %s. Delete them and then " +
                            "try again.",
                    recipesContainingIngredient.stream().map(Recipe::getRecipeName).collect(Collectors.toList())));
        }

        ingredientRepository.delete(toDelete.get());

        return ResponseEntity.ok(String.format("Deleted ingredient with id %d.", id));
    }

    @PatchMapping("/api/ingredients/{id}")
    public ResponseEntity<String> updateIngredient(@PathVariable Integer id,
                                                   @Valid @RequestBody CreateIngredientDTO dto,
                                                   BindingResult bind) {

        if (bind.hasErrors()) {
            throw new ValidationException("Value for 'name' must not be empty.");
        }


        Optional<Ingredient> toUpdate = ingredientRepository.findById(id);

        if (!toUpdate.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        toUpdate.get().setIngredientName(dto.getName());
        ingredientRepository.save(toUpdate.get());

        return ResponseEntity.ok(String.format("Updated name '%s' set for ingredient with id %d.", dto.getName(), id));
    }
}