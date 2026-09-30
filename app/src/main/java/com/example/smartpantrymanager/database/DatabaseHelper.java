package com.example.smartpantrymanager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // =========================================================
    // DATABASE
    // =========================================================

    // Name of the local SQLite database used by the application.
    private static final String DATABASE_NAME = "SmartPantry.db";

    /*
     * Database version 2 includes the recipe system.
     *
     * The upgrade process keeps existing pantry data
     * when the database structure is upgraded.
     */
    private static final int DATABASE_VERSION = 2;


    // =========================================================
    // PANTRY TABLE
    // =========================================================

    // Table containing the user's pantry ingredients.
    public static final String TABLE_PANTRY = "pantry_items";

    // Primary key for each pantry item.
    public static final String COL_PANTRY_ID = "id";

    // Ingredient name stored in the pantry.
    public static final String COL_PANTRY_NAME = "name";

    // Quantity available for the pantry ingredient.
    public static final String COL_PANTRY_QUANTITY = "quantity";

    // Unit used for the ingredient quantity.
    public static final String COL_PANTRY_UNIT = "unit";

    // Optional expiry date for the pantry ingredient.
    public static final String COL_PANTRY_EXPIRY = "expiry_date";


    // =========================================================
    // RECIPES TABLE
    // =========================================================

    // Table containing the recipes available in the application.
    public static final String TABLE_RECIPES = "recipes";

    // Primary key for each recipe.
    public static final String COL_RECIPE_ID = "id";

    // Name of the recipe.
    public static final String COL_RECIPE_NAME = "name";

    // Cooking/preparation instructions for the recipe.
    public static final String COL_RECIPE_INSTRUCTIONS = "instructions";


    // =========================================================
    // RECIPE INGREDIENTS TABLE
    // =========================================================

    // Table connecting recipes with their required ingredients.
    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";

    // Primary key for each recipe ingredient record.
    public static final String COL_RECIPE_INGREDIENT_ID = "id";

    // Foreign key that connects an ingredient to a recipe.
    public static final String COL_RECIPE_ID_FK = "recipe_id";

    // Name of the required ingredient.
    public static final String COL_INGREDIENT_NAME =
            "ingredient_name";

    // Quantity required by the recipe.
    public static final String COL_REQUIRED_QUANTITY =
            "required_quantity";

    // Measurement unit used by the recipe.
    public static final String COL_INGREDIENT_UNIT =
            "unit";


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    /*
     * Creates the DatabaseHelper instance.
     *
     * The helper manages creation, upgrades, and access
     * to the application's local SQLite database.
     */
    public DatabaseHelper(Context context) {

        super(
                context,
                DATABASE_NAME,
                null,
                DATABASE_VERSION
        );
    }


    // =========================================================
    // DATABASE CREATED
    // =========================================================

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create the pantry table when the database
        // is created for the first time.
        createPantryTable(db);

        // Create the recipe and recipe ingredient tables.
        createRecipeTables(db);

        // Add the application's initial recipe data.
        seedRecipes(db);
    }


    // =========================================================
    // DATABASE UPGRADE
    // =========================================================

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        /*
         * IMPORTANT:
         *
         * The pantry table is intentionally not deleted
         * during a database upgrade.
         *
         * This prevents existing user pantry data
         * from being lost.
         */

        if (oldVersion < 2) {

            // Add the recipe system when upgrading
            // from an older database version.
            createRecipeTables(db);

            // Add the default recipes.
            seedRecipes(db);
        }
    }


    // =========================================================
    // ENABLE FOREIGN KEYS
    // =========================================================

    @Override
    public void onConfigure(SQLiteDatabase db) {

        super.onConfigure(db);

        /*
         * Enable SQLite foreign key constraints.
         *
         * This allows recipe ingredient records to remain
         * correctly linked to their parent recipes.
         */
        db.setForeignKeyConstraintsEnabled(true);
    }


    // =========================================================
    // CREATE PANTRY TABLE
    // =========================================================

    private void createPantryTable(SQLiteDatabase db) {

        /*
         * Creates the pantry_items table.
         *
         * Each pantry item contains:
         * - ID
         * - Ingredient name
         * - Quantity
         * - Unit
         * - Optional expiry date
         */

        String sql =
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_PANTRY
                        + " ("
                        + COL_PANTRY_ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                        + COL_PANTRY_NAME
                        + " TEXT NOT NULL, "

                        + COL_PANTRY_QUANTITY
                        + " REAL NOT NULL, "

                        + COL_PANTRY_UNIT
                        + " TEXT NOT NULL, "

                        + COL_PANTRY_EXPIRY
                        + " TEXT"
                        + ")";

        db.execSQL(sql);
    }


    // =========================================================
    // CREATE RECIPE TABLES
    // =========================================================

    private void createRecipeTables(SQLiteDatabase db) {

        // -----------------------------------------------------
        // Recipes
        // -----------------------------------------------------

        /*
         * The recipes table stores the basic information
         * about each recipe.
         */

        String recipesSql =
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_RECIPES
                        + " ("
                        + COL_RECIPE_ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                        + COL_RECIPE_NAME
                        + " TEXT NOT NULL, "

                        + COL_RECIPE_INSTRUCTIONS
                        + " TEXT NOT NULL"
                        + ")";

        db.execSQL(recipesSql);


        // -----------------------------------------------------
        // Recipe ingredients
        // -----------------------------------------------------

        /*
         * The recipe_ingredients table stores the ingredients
         * required by each recipe.
         *
         * recipe_id is a foreign key connected to the recipes
         * table. ON DELETE CASCADE automatically removes
         * related ingredients when a recipe is deleted.
         */

        String ingredientsSql =
                "CREATE TABLE IF NOT EXISTS "
                        + TABLE_RECIPE_INGREDIENTS
                        + " ("
                        + COL_RECIPE_INGREDIENT_ID
                        + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                        + COL_RECIPE_ID_FK
                        + " INTEGER NOT NULL, "

                        + COL_INGREDIENT_NAME
                        + " TEXT NOT NULL, "

                        + COL_REQUIRED_QUANTITY
                        + " REAL NOT NULL, "

                        + COL_INGREDIENT_UNIT
                        + " TEXT NOT NULL, "

                        + "FOREIGN KEY ("
                        + COL_RECIPE_ID_FK
                        + ") REFERENCES "
                        + TABLE_RECIPES
                        + "("
                        + COL_RECIPE_ID
                        + ") ON DELETE CASCADE"
                        + ")";

        db.execSQL(ingredientsSql);
    }


    // =========================================================
    // PANTRY CRUD
    // =========================================================

    /*
     * Adds a new ingredient to the user's pantry.
     *
     * @return the ID of the newly inserted pantry item.
     */
    public long addPantryItem(
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COL_PANTRY_NAME,
                name
        );

        values.put(
                COL_PANTRY_QUANTITY,
                quantity
        );

        values.put(
                COL_PANTRY_UNIT,
                unit
        );

        values.put(
                COL_PANTRY_EXPIRY,
                expiryDate
        );

        return db.insert(
                TABLE_PANTRY,
                null,
                values
        );
    }


    /*
     * Retrieves all pantry items.
     *
     * Items are sorted alphabetically by ingredient name.
     */
    public Cursor getAllPantryItems() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                TABLE_PANTRY,
                null,
                null,
                null,
                null,
                null,
                COL_PANTRY_NAME + " ASC"
        );
    }


    /*
     * Retrieves one pantry item using its database ID.
     */
    public Cursor getPantryItemById(int id) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                TABLE_PANTRY,
                null,
                COL_PANTRY_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null,
                null,
                null
        );
    }


    /*
     * Updates an existing pantry item.
     *
     * The item is located using its unique database ID.
     */
    public int updatePantryItem(
            int id,
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        ContentValues values =
                new ContentValues();

        values.put(
                COL_PANTRY_NAME,
                name
        );

        values.put(
                COL_PANTRY_QUANTITY,
                quantity
        );

        values.put(
                COL_PANTRY_UNIT,
                unit
        );

        values.put(
                COL_PANTRY_EXPIRY,
                expiryDate
        );

        return db.update(
                TABLE_PANTRY,
                values,
                COL_PANTRY_ID + "=?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }


    /*
     * Deletes a pantry item using its unique database ID.
     */
    public int deletePantryItem(int id) {

        SQLiteDatabase db =
                this.getWritableDatabase();

        return db.delete(
                TABLE_PANTRY,
                COL_PANTRY_ID + "=?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }


    // =========================================================
    // ADD RECIPE
    // =========================================================

    /*
     * Inserts a recipe into the recipes table.
     *
     * This method is used internally while creating
     * the default recipe data.
     */
    private long addRecipe(
            SQLiteDatabase db,
            String name,
            String instructions) {

        ContentValues values =
                new ContentValues();

        values.put(
                COL_RECIPE_NAME,
                name
        );

        values.put(
                COL_RECIPE_INSTRUCTIONS,
                instructions
        );

        return db.insert(
                TABLE_RECIPES,
                null,
                values
        );
    }


    // =========================================================
    // ADD RECIPE INGREDIENT
    // =========================================================

    /*
     * Adds an ingredient requirement to a recipe.
     *
     * recipeId connects this ingredient to the parent recipe.
     */
    private void addRecipeIngredient(
            SQLiteDatabase db,
            long recipeId,
            String ingredientName,
            double quantity,
            String unit) {

        ContentValues values =
                new ContentValues();

        values.put(
                COL_RECIPE_ID_FK,
                recipeId
        );

        values.put(
                COL_INGREDIENT_NAME,
                ingredientName
        );

        values.put(
                COL_REQUIRED_QUANTITY,
                quantity
        );

        values.put(
                COL_INGREDIENT_UNIT,
                unit
        );

        db.insert(
                TABLE_RECIPE_INGREDIENTS,
                null,
                values
        );
    }


    // =========================================================
    // SEED RECIPES
    // =========================================================

    /*
     * Inserts the default recipes used by the application.
     *
     * A check is performed first to prevent the same
     * recipes from being inserted multiple times.
     */
    private void seedRecipes(SQLiteDatabase db) {

        /*
         * Check whether at least one recipe already exists.
         *
         * This prevents duplicate seed data if this method
         * is called more than once.
         */

        Cursor cursor = db.query(
                TABLE_RECIPES,
                new String[]{COL_RECIPE_ID},
                null,
                null,
                null,
                null,
                null,
                "1"
        );

        boolean alreadySeeded =
                cursor != null && cursor.moveToFirst();

        if (cursor != null) {
            cursor.close();
        }

        if (alreadySeeded) {
            return;
        }


        // =====================================================
        // 1. TOMATO PASTA
        // =====================================================

        // Add the recipe and store its database ID.
        long recipeId = addRecipe(
                db,
                "Tomato Pasta",
                "1. Boil the pasta until tender.\n"
                        + "2. Heat oil in a pan.\n"
                        + "3. Add onion and garlic.\n"
                        + "4. Add tomatoes and cook until soft.\n"
                        + "5. Mix in the cooked pasta and serve."
        );

        // Add the ingredients required by Tomato Pasta.
        addRecipeIngredient(
                db, recipeId, "pasta", 200, "grams"
        );

        addRecipeIngredient(
                db, recipeId, "tomato", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "onion", 1, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "garlic", 2, "pieces"
        );


        // =====================================================
        // 2. CHICKEN PASTA
        // =====================================================

        recipeId = addRecipe(
                db,
                "Chicken Pasta",
                "1. Cook the pasta.\n"
                        + "2. Cut chicken into small pieces.\n"
                        + "3. Cook the chicken in a pan.\n"
                        + "4. Add onion and garlic.\n"
                        + "5. Add pasta and mix well."
        );

        addRecipeIngredient(
                db, recipeId, "chicken", 300, "grams"
        );

        addRecipeIngredient(
                db, recipeId, "pasta", 200, "grams"
        );

        addRecipeIngredient(
                db, recipeId, "onion", 1, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "garlic", 2, "pieces"
        );


        // =====================================================
        // 3. VEGETABLE FRIED RICE
        // =====================================================

        recipeId = addRecipe(
                db,
                "Vegetable Fried Rice",
                "1. Cook the rice.\n"
                        + "2. Heat oil in a pan.\n"
                        + "3. Add onion and mixed vegetables.\n"
                        + "4. Add cooked rice.\n"
                        + "5. Stir-fry until everything is hot."
        );

        addRecipeIngredient(
                db, recipeId, "rice", 300, "grams"
        );

        addRecipeIngredient(
                db, recipeId, "carrot", 1, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "peas", 100, "grams"
        );

        addRecipeIngredient(
                db, recipeId, "onion", 1, "pieces"
        );


        // =====================================================
        // 4. OMELETTE
        // =====================================================

        recipeId = addRecipe(
                db,
                "Vegetable Omelette",
                "1. Beat the eggs.\n"
                        + "2. Chop the onion and tomato.\n"
                        + "3. Mix all ingredients together.\n"
                        + "4. Cook in a lightly oiled pan.\n"
                        + "5. Fold and serve."
        );

        addRecipeIngredient(
                db, recipeId, "egg", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "tomato", 1, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "onion", 1, "pieces"
        );


        // =====================================================
        // 5. SCRAMBLED EGGS
        // =====================================================

        recipeId = addRecipe(
                db,
                "Scrambled Eggs",
                "1. Crack the eggs into a bowl.\n"
                        + "2. Beat the eggs.\n"
                        + "3. Heat a pan.\n"
                        + "4. Add eggs and stir continuously.\n"
                        + "5. Cook until set."
        );

        addRecipeIngredient(
                db, recipeId, "egg", 3, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "milk", 50, "ml"
        );


        // =====================================================
        // 6. CHICKEN SANDWICH
        // =====================================================

        recipeId = addRecipe(
                db,
                "Chicken Sandwich",
                "1. Cook the chicken.\n"
                        + "2. Toast the bread.\n"
                        + "3. Add lettuce and tomato.\n"
                        + "4. Add cooked chicken.\n"
                        + "5. Assemble the sandwich."
        );

        addRecipeIngredient(
                db, recipeId, "chicken", 150, "grams"
        );

        addRecipeIngredient(
                db, recipeId, "bread", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "tomato", 1, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "lettuce", 2, "pieces"
        );


        // =====================================================
        // 7. GRILLED CHEESE
        // =====================================================

        recipeId = addRecipe(
                db,
                "Grilled Cheese Sandwich",
                "1. Place cheese between two slices of bread.\n"
                        + "2. Heat a pan.\n"
                        + "3. Toast both sides until golden.\n"
                        + "4. Serve hot."
        );

        addRecipeIngredient(
                db, recipeId, "bread", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "cheese", 2, "slices"
        );


        // =====================================================
        // 8. FRENCH TOAST
        // =====================================================

        recipeId = addRecipe(
                db,
                "French Toast",
                "1. Beat eggs with milk.\n"
                        + "2. Dip bread into the mixture.\n"
                        + "3. Heat a pan with butter.\n"
                        + "4. Cook both sides until golden."
        );

        addRecipeIngredient(
                db, recipeId, "bread", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "egg", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "milk", 100, "ml"
        );


        // =====================================================
        // 9. PANCAKES
        // =====================================================

        recipeId = addRecipe(
                db,
                "Pancakes",
                "1. Mix flour, milk and eggs.\n"
                        + "2. Add sugar.\n"
                        + "3. Heat a pan.\n"
                        + "4. Pour batter into the pan.\n"
                        + "5. Cook both sides."
        );

        addRecipeIngredient(
                db, recipeId, "flour", 200, "grams"
        );

        addRecipeIngredient(
                db, recipeId, "milk", 250, "ml"
        );

        addRecipeIngredient(
                db, recipeId, "egg", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "sugar", 2, "tablespoons"
        );


        // =====================================================
        // 10. GARLIC PASTA
        // =====================================================

        recipeId = addRecipe(
                db,
                "Garlic Pasta",
                "1. Cook the pasta.\n"
                        + "2. Slice the garlic.\n"
                        + "3. Heat oil and gently cook garlic.\n"
                        + "4. Add cooked pasta.\n"
                        + "5. Mix and serve."
        );

        addRecipeIngredient(
                db, recipeId, "pasta", 200, "grams"
        );

        addRecipeIngredient(
                db, recipeId, "garlic", 3, "pieces"
        );


        // =====================================================
        // 11. TOMATO SOUP
        // =====================================================

        recipeId = addRecipe(
                db,
                "Tomato Soup",
                "1. Chop the tomatoes and onion.\n"
                        + "2. Cook them until soft.\n"
                        + "3. Add water and simmer.\n"
                        + "4. Blend until smooth.\n"
                        + "5. Serve hot."
        );

        addRecipeIngredient(
                db, recipeId, "tomato", 4, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "onion", 1, "pieces"
        );


        // =====================================================
        // 12. POTATO OMELETTE
        // =====================================================

        recipeId = addRecipe(
                db,
                "Potato Omelette",
                "1. Slice the potatoes.\n"
                        + "2. Cook potatoes until soft.\n"
                        + "3. Beat the eggs.\n"
                        + "4. Combine eggs and potatoes.\n"
                        + "5. Cook until set."
        );

        addRecipeIngredient(
                db, recipeId, "potato", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "egg", 3, "pieces"
        );


        // =====================================================
        // 13. EGG FRIED RICE
        // =====================================================

        recipeId = addRecipe(
                db,
                "Egg Fried Rice",
                "1. Cook the rice.\n"
                        + "2. Scramble the eggs.\n"
                        + "3. Add rice to the pan.\n"
                        + "4. Stir-fry everything together.\n"
                        + "5. Serve hot."
        );

        addRecipeIngredient(
                db, recipeId, "rice", 300, "grams"
        );

        addRecipeIngredient(
                db, recipeId, "egg", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "onion", 1, "pieces"
        );


        // =====================================================
        // 14. CHICKEN RICE
        // =====================================================

        recipeId = addRecipe(
                db,
                "Chicken Rice",
                "1. Cook the chicken with onion.\n"
                        + "2. Add rice.\n"
                        + "3. Add water.\n"
                        + "4. Simmer until rice is cooked.\n"
                        + "5. Serve hot."
        );

        addRecipeIngredient(
                db, recipeId, "chicken", 300, "grams"
        );

        addRecipeIngredient(
                db, recipeId, "rice", 300, "grams"
        );

        addRecipeIngredient(
                db, recipeId, "onion", 1, "pieces"
        );


        // =====================================================
        // 15. POTATO CURRY
        // =====================================================

        recipeId = addRecipe(
                db,
                "Potato Curry",
                "1. Peel and cut potatoes.\n"
                        + "2. Cook onion until soft.\n"
                        + "3. Add potatoes and spices.\n"
                        + "4. Add water and simmer.\n"
                        + "5. Cook until potatoes are tender."
        );

        addRecipeIngredient(
                db, recipeId, "potato", 4, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "onion", 1, "pieces"
        );


        // =====================================================
        // 16. TUNA SANDWICH
        // =====================================================

        recipeId = addRecipe(
                db,
                "Tuna Sandwich",
                "1. Drain the tuna.\n"
                        + "2. Mix tuna with mayonnaise.\n"
                        + "3. Add tomato.\n"
                        + "4. Place mixture between bread slices.\n"
                        + "5. Serve."
        );

        addRecipeIngredient(
                db, recipeId, "tuna", 150, "grams"
        );

        addRecipeIngredient(
                db, recipeId, "bread", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "mayonnaise", 2, "tablespoons"
        );

        addRecipeIngredient(
                db, recipeId, "tomato", 1, "pieces"
        );


        // =====================================================
        // 17. VEGETABLE PASTA
        // =====================================================

        recipeId = addRecipe(
                db,
                "Vegetable Pasta",
                "1. Cook the pasta.\n"
                        + "2. Chop the vegetables.\n"
                        + "3. Cook vegetables in a pan.\n"
                        + "4. Add pasta.\n"
                        + "5. Mix and serve."
        );

        addRecipeIngredient(
                db, recipeId, "pasta", 200, "grams"
        );

        addRecipeIngredient(
                db, recipeId, "carrot", 1, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "tomato", 1, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "onion", 1, "pieces"
        );


        // =====================================================
        // 18. BANANA PANCAKES
        // =====================================================

        recipeId = addRecipe(
                db,
                "Banana Pancakes",
                "1. Mash the banana.\n"
                        + "2. Add egg and flour.\n"
                        + "3. Mix into a batter.\n"
                        + "4. Cook small pancakes in a pan.\n"
                        + "5. Serve warm."
        );

        addRecipeIngredient(
                db, recipeId, "banana", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "egg", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "flour", 150, "grams"
        );


        // =====================================================
        // 19. CHICKEN CURRY
        // =====================================================

        recipeId = addRecipe(
                db,
                "Chicken Curry",
                "1. Cook onion until golden.\n"
                        + "2. Add chicken.\n"
                        + "3. Add tomatoes and spices.\n"
                        + "4. Add water and simmer.\n"
                        + "5. Cook until chicken is tender."
        );

        addRecipeIngredient(
                db, recipeId, "chicken", 500, "grams"
        );

        addRecipeIngredient(
                db, recipeId, "tomato", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "onion", 2, "pieces"
        );


        // =====================================================
        // 20. VEGETABLE SOUP
        // =====================================================

        recipeId = addRecipe(
                db,
                "Vegetable Soup",
                "1. Chop all vegetables.\n"
                        + "2. Add vegetables to a pot.\n"
                        + "3. Add water.\n"
                        + "4. Simmer until vegetables are tender.\n"
                        + "5. Season and serve."
        );

        addRecipeIngredient(
                db, recipeId, "carrot", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "potato", 2, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "onion", 1, "pieces"
        );

        addRecipeIngredient(
                db, recipeId, "peas", 100, "grams"
        );
    }


    // =========================================================
    // GET ALL RECIPES
    // =========================================================

    /*
     * Retrieves all recipes from the database.
     *
     * Recipes are returned alphabetically by recipe name.
     */
    public Cursor getAllRecipes() {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                COL_RECIPE_NAME + " ASC"
        );
    }


    // =========================================================
    // GET RECIPE BY ID
    // =========================================================

    /*
     * Retrieves a specific recipe using its unique ID.
     *
     * This is used by RecipeDetailActivity to display
     * the selected recipe.
     */
    public Cursor getRecipeById(int recipeId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                TABLE_RECIPES,
                null,
                COL_RECIPE_ID + "=?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                null
        );
    }


    // =========================================================
    // GET INGREDIENTS FOR A RECIPE
    // =========================================================

    /*
     * Retrieves all ingredients belonging to a specific recipe.
     *
     * Ingredients are sorted alphabetically by ingredient name.
     */
    public Cursor getRecipeIngredients(int recipeId) {

        SQLiteDatabase db =
                this.getReadableDatabase();

        return db.query(
                TABLE_RECIPE_INGREDIENTS,
                null,
                COL_RECIPE_ID_FK + "=?",
                new String[]{
                        String.valueOf(recipeId)
                },
                null,
                null,
                COL_INGREDIENT_NAME + " ASC"
        );
    }
}

