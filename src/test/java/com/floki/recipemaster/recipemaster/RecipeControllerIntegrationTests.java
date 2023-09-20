package com.floki.recipemaster.recipemaster;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.NestedServletException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.floki.recipemaster.dto.CreateRecipeDTO;
import com.floki.recipemaster.dto.RecipeIngredientDTO;
import com.floki.recipemaster.dto.UpdateRecipeDTO;
import com.floki.recipemaster.entity.Measurement;
import com.floki.recipemaster.entity.Recipe;
import com.floki.recipemaster.repository.RecipeRepository;

public class RecipeControllerIntegrationTests extends IntegrationTest {

  Map<String, CreateRecipeDTO> testData;
  @Autowired
  RecipeRepository recipeRepository;

  @BeforeEach
  public void setup() {
    // productsRepository.deleteAll();
    testData = getTestData();
  }

  @Test
  public void testAddRecipe_successful() throws Exception {
    List<RecipeIngredientDTO> ingredients = new ArrayList<>();
    RecipeIngredientDTO recipeIngredientDTO1 = new RecipeIngredientDTO(1, "milk", Measurement.LITER, 1.5);
    RecipeIngredientDTO recipeIngredientDTO2 = new RecipeIngredientDTO(2, "egg", Measurement.PIECES, 4.0);
    ingredients.add(recipeIngredientDTO1);
    ingredients.add(recipeIngredientDTO2);

    CreateRecipeDTO expectedRecord = testData.get("Pancake");

    mvc.perform(get("/api/recipes/queries").contentType("application/content-json")).andExpect(status().isOk());

    ObjectMapper mapper = new ObjectMapper();
    mvc.perform(post("/api/recipes").contentType("application/json").content(mapper.writeValueAsString(expectedRecord)))
        .andDo(print()).andExpect(status().isOk());

    mvc.perform(get("/api/recipes/queries").contentType("application/content-json")).andExpect(status().isOk())
        .andDo(print());

  }

  @Test
  public void testAddRecipe_badinput() throws Exception {
    List<RecipeIngredientDTO> ingredients = new ArrayList<>();
    RecipeIngredientDTO recipeIngredientDTO1 = new RecipeIngredientDTO(1, "milk", Measurement.LITER, 1.5);
    RecipeIngredientDTO recipeIngredientDTO2 = new RecipeIngredientDTO(2, "egg", Measurement.PIECES, 4.0);
    ingredients.add(recipeIngredientDTO1);
    ingredients.add(recipeIngredientDTO2);

    CreateRecipeDTO expectedRecord = testData.get("Pancake");

    ObjectMapper mapper = new ObjectMapper();
    mvc.perform(post("/api/recipes").contentType("application/json").content(mapper.writeValueAsString(expectedRecord)))
        .andDo(print()).andExpect(status().isOk());

    Exception exception = assertThrows(NestedServletException.class, () -> {
      mvc.perform(
          get("/api/recipes/queries?query=pasta&categories=vegetarian").contentType("application/content-json"));
    });
    String expectedMessage = "Parameters 'query', 'ingredient', 'categories', 'servings', matchInstruction cannot be combined. "
        + "Please only specify one of them.";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));

  }

  @Test
  public void testUpdateRecipee_Successful() throws Exception {
    // test existing
    CreateRecipeDTO dto = testData.get("Pancake");

    ObjectMapper mapper = new ObjectMapper();
    mvc.perform(post("/api/recipes").contentType("application/json").content(mapper.writeValueAsString(dto)))
        .andDo(print()).andExpect(status().isOk());
    Set<Recipe> record = recipeRepository.findByRecipeNameContainingIgnoreCase(dto.getName());
    Recipe actualRecord = record.iterator().next();
    System.out.println("actual record" + actualRecord);
    UpdateRecipeDTO updateRecipeDTO = new UpdateRecipeDTO("Cake", null, 8, null, "Non-vegetarian");
    mvc.perform(patch("/api/recipes/" + actualRecord.getRecipeId()).contentType("application/json")
        .content(mapper.writeValueAsString(updateRecipeDTO))).andDo(print()).andExpect(status().isOk());

    Recipe updatedRecord = recipeRepository.findById(actualRecord.getRecipeId()).get();
    // assertFalse(new ReflectionEquals(actualRecord).matches(updatedRecord));
    System.out.println("updated record" + updatedRecord);
    System.out.println("updated record name " + updatedRecord.getRecipeName());
    // assertFalse(updateRecipeDTO.getName().equals(actualRecord.getRecipeName()));
    // assertTrue(updateRecipeDTO.getName().equals(updatedRecord.getRecipeName()));
  }

  @Test
  public void testDeleteRecipee_Successful() throws Exception {

    CreateRecipeDTO dto = testData.get("Grill Chicken");

    ObjectMapper mapper = new ObjectMapper();
    mvc.perform(post("/api/recipes").contentType("application/json").content(mapper.writeValueAsString(dto)))
        .andDo(print()).andExpect(status().isOk());
    Set<Recipe> record = recipeRepository.findByRecipeNameContainingIgnoreCase(dto.getName());
    Recipe actualRecord = record.iterator().next();
    mvc.perform(delete("/api/recipes/" + actualRecord.getRecipeId()).contentType("application/json")).andDo(print())
        .andExpect(status().isOk());

    Exception exception = assertThrows(NoSuchElementException.class, () -> {
      recipeRepository.findById(actualRecord.getRecipeId()).get();
    });
    String expectedMessage = "No value present";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));

  }

  @Test
  public void testGetRecipes_WithServingAndIncludeIngredient_Success() throws Exception {

    CreateRecipeDTO dto = testData.get("Grill Chicken");

    ObjectMapper mapper = new ObjectMapper();
    mvc.perform(post("/api/recipes").contentType("application/json").content(mapper.writeValueAsString(dto)))
        .andDo(print()).andExpect(status().isOk());
    mvc.perform(get("/api/recipes?servings=8&includeIngredient=yogurt").contentType("application/content-json"))
        .andExpect(status().isOk()).andDo(print());

  }

  @Test
  public void testGetRecipes_WithServingAndExcludeIngredient_NotFound() throws Exception {

    CreateRecipeDTO dto = testData.get("Grill Chicken");

    ObjectMapper mapper = new ObjectMapper();
    mvc.perform(post("/api/recipes").contentType("application/json").content(mapper.writeValueAsString(dto)))
        .andDo(print()).andExpect(status().isOk());
    mvc.perform(get("/api/recipes?servings=10&excludeIngredient=milk").contentType("application/content-json"))
        .andExpect(status().isNotFound()).andDo(print());

  }

  @Test
  public void testGetRecipes_WithMatchInstructionAndExcludeIngredient_Success() throws Exception {

    CreateRecipeDTO dto = testData.get("Grill Chicken");

    ObjectMapper mapper = new ObjectMapper();
    mvc.perform(post("/api/recipes").contentType("application/json").content(mapper.writeValueAsString(dto)))
        .andDo(print()).andExpect(status().isOk());
    mvc.perform(
        get("/api/recipes?matchInstruction=oven&excludeIngredient=milk").contentType("application/content-json"))
        .andExpect(status().isOk()).andDo(print());

  }

  @Test
  public void testGetRecipes_WithMatchInstructionAndServings_NotFound() throws Exception {

    CreateRecipeDTO dto = testData.get("Grill Chicken");

    ObjectMapper mapper = new ObjectMapper();
    mvc.perform(post("/api/recipes").contentType("application/json").content(mapper.writeValueAsString(dto)))
        .andDo(print()).andExpect(status().isOk());
    mvc.perform(get("/api/recipes?matchInstruction=oven&servings=8").contentType("application/content-json"))
        .andExpect(status().isNotFound()).andDo(print());

  }

  @Test
  public void testGetRecipes_WithId_Success() throws Exception {

    CreateRecipeDTO dto = testData.get("Grill Chicken");
    ObjectMapper mapper = new ObjectMapper();
    mvc.perform(post("/api/recipes").contentType("application/json").content(mapper.writeValueAsString(dto)))
        .andDo(print()).andExpect(status().isOk());
    Set<Recipe> record = recipeRepository.findByRecipeNameContainingIgnoreCase(dto.getName());
    Recipe actualRecord = record.iterator().next();
    mvc.perform(get("/api/recipes/" + actualRecord.getRecipeId()).contentType("application/content-json"))
        .andExpect(status().isOk()).andDo(print());

  }

  @Test
  public void testGetRecipes_WithId_NotFound() throws Exception {

    mvc.perform(get("/api/recipes/" + Integer.MAX_VALUE).contentType("application/content-json"))
        .andExpect(status().isNotFound()).andDo(print());

  }

  private Map<String, CreateRecipeDTO> getTestData() {
    Map<String, CreateRecipeDTO> data = new HashMap<>();

    List<RecipeIngredientDTO> pc_ingredients = new ArrayList<>();
    RecipeIngredientDTO recipeIngredientPc1 = new RecipeIngredientDTO(1, "milk", Measurement.LITER, 1.5);
    RecipeIngredientDTO recipeIngredientPc2 = new RecipeIngredientDTO(2, "egg", Measurement.PIECES, 4.0);
    RecipeIngredientDTO recipeIngredientPc3 = new RecipeIngredientDTO(2, "plain flour", Measurement.GRAM, 100.0);
    pc_ingredients.add(recipeIngredientPc1);
    pc_ingredients.add(recipeIngredientPc2);
    pc_ingredients.add(recipeIngredientPc3);

    CreateRecipeDTO createRecipePancake = new CreateRecipeDTO("Pancake",
        "1. Whisk flour, egg and milk. 2. Let rest for 30 min. 3. Cook on frying pan 1 minute on each side.", 6,
        pc_ingredients, "Non-Vegetarian");
    data.put("Pancake", createRecipePancake);

    List<RecipeIngredientDTO> gc_ingredients = new ArrayList<>();
    RecipeIngredientDTO recipeIngredientGc1 = new RecipeIngredientDTO(1, "chicken", Measurement.KILOGRAM, 1.0);
    RecipeIngredientDTO recipeIngredientGc2 = new RecipeIngredientDTO(2, "yogurt", Measurement.GRAM, 25.0);
    RecipeIngredientDTO recipeIngredientGc3 = new RecipeIngredientDTO(3, "spices", Measurement.GRAM, 5.0);

    gc_ingredients.add(recipeIngredientGc1);
    gc_ingredients.add(recipeIngredientGc2);
    gc_ingredients.add(recipeIngredientGc3);

    CreateRecipeDTO createRecipeGrillChicken = new CreateRecipeDTO("Grill Chicken",
        "1. Whisk yogurt, salt, pepper, and grill spice mix. 2. Marinate chicken with the mix and let rest for 30 min. 3. Bake in oven for 30 minute at 180 degree.",
        8, pc_ingredients, "Non-Vegetarian");
    data.put("Grill Chicken", createRecipeGrillChicken);

    return data;
  }

}
