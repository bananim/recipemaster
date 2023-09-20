# recipemaster

Standalone java application which allows users to manage their favourite recipes. It
allow adding, updating, removing and fetching recipes. Additionally users are able to filter
available recipes based on one or more of the following criteria:
1. Whether or not the dish is vegetarian
2. The number of servings
3. Specific ingredient (either include or exclude)
4. Text search within the instructions.
For example, the API is able to handle the following search requests:  
• All vegetarian recipes
• Recipes that can serve 4 persons and have “potatoes” as an ingredient
• Recipes without “salmon” as an ingredient that has “oven” in the instructions.

## Environment:
- Java version: 11
- Maven version: 3.*
- Spring Boot version: 2.4.1

##How to run the service:

1.Run the app using maven : mvn spring-boot:run
   
2.CD to the target folder of the project and run the Springboot fat jar : java -jar recipemaster-1.0.0-SNAPSHOT.jar

The application can be accessed at http://localhost:8080


## API

### Recipes

#### Create recipe

`POST /api/recipes`

__Request body__:
```
{
   "name": "<string>",
   "instructions": "<string>",
   "servings":"<Integer>",
   "ingredients": [
      {
         "name": "<string>",
         "measurement": "<LITER|DECILITER|CENTILITER|KILOGRAM|GRAM|MILLIGRAM|PIECES>",
         "amount": <number>
      },
      ...
   ],
   "category": "<string>"
}
```

Any ingredients or categories that do not exist will be created.

##### Example:

`POST /api/recipes`

__Request Body__:
```
{
    "name": "Pancakes",
    "instructions": "1. Whisk flour, egg and milk. 2. Let rest for 30 min. 3. Cook on frying pan 1 minute on each side.",
    "servings": 4
    "ingredients": [
        {
            "name": "plain flour",
            "measurement": "GRAM",
            "amount": 100
        },
        {
            "name": "large egg",
            "measurement": "PIECES",
            "amount": 2
        },
        {
            "name": "milk",
            "measurement": "DECILITER",
            "amount": 3
        }
    ],
    "category": "Non-Vegetarian"
}
```

#### Get all recipes

`GET /api/recipes/queries

#### Get a single recipe by id

`GET /api/recipes/<id>`

#### Search recipes by name 

`GET /api/recipes/queries?query=<name query>`

##### Examples

- `GET /api/recipes/queries?query=cake`

#### Get all recipes that is categorized by any of the specified categories

`GET /api/recipes/queries?categories=<comma separated list of categories>`

Case-sensitive.

##### Examples

- `GET /api/recipes/queries?categories=Non-Vegetarian`
- `GET /api/recipes/queries?categories=Vegetarian,Non-Vegetarian`

#### Get all recipes that contains the specified ingredient

`GET /api/recipes?ingredient=<ingredient name>`

The ingredient name must match exactly but case is ignored. `ingredient=Plain Flour` for example will not match 'Flour', but `ingredient=flour` will.

##### Examples

- `GET /api/recipes/queries?ingredient=Plain Flour`
- `GET /api/recipes/queries?ingredient=flour`

#### Update recipe

`PATCH /api/recipes/<id>`

__Request body__:
```
{
   "name": "<string>",
   "instructions": "<string>",
   "servings":"<Integer>",
   "ingredients": [
      {
         "name": "<string>",
         "measurement": "<LITER|DECILITER|CENTILITER|KILOGRAM|GRAM|MILLIGRAM|PIECES>",
         "amount": <number>
      },
      ...
   ],
   "category": "<string>",
}
```

##### Example: Update recipe name

`PATCH /api/recipes/1`

__Request body__:
```
{
   "name": "new name"
}
```

#### Delete a recipe

`DELETE /api/recipes/<id>`
