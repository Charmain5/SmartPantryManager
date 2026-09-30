
package com.example.smartpantrymanager.matcher;

import android.database.Cursor;

import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the strict matching logic used to determine
 * which recipes can be prepared using the ingredients
 * currently available in the pantry.
 *
 * A recipe is suggested only when:
 * - Every required ingredient is available.
 * - Ingredient names match after normalization.
 * - Units are compatible.
 * - The pantry quantity is equal to or greater than
 *   the quantity required by the recipe.
 */
public class StrictRecipeMatcher {

    /*
     * Database helper used to retrieve pantry items,
     * recipes, and recipe ingredients.
     */
    private final DatabaseHelper databaseHelper;


    /**
     * Creates a new recipe matcher using the application's
     * database helper.
     */
    public StrictRecipeMatcher(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }


    // =========================================================
    // GET ALL RECIPES THAT CAN CURRENTLY BE MADE
    // =========================================================

    /**
     * Checks all stored recipes and returns only the recipes
     * that can currently be prepared from the pantry.
     */
    public List<Recipe> getSuggestedRecipes() {

        List<Recipe> suggestedRecipes = new ArrayList<>();

        // Load the ingredients currently available in the pantry.
        List<PantryItem> pantryItems = getPantryItems();

        // Retrieve all recipes from the database.
        Cursor recipeCursor = databaseHelper.getAllRecipes();

        if (recipeCursor == null) {
            return suggestedRecipes;
        }

        try {

            while (recipeCursor.moveToNext()) {

                int recipeId =
                        recipeCursor.getInt(
                                recipeCursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_RECIPE_ID
                                )
                        );

                String recipeName =
                        recipeCursor.getString(
                                recipeCursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_RECIPE_NAME
                                )
                        );

                String instructions =
                        recipeCursor.getString(
                                recipeCursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_RECIPE_INSTRUCTIONS
                                )
                        );

                // Create a Recipe object from the database record.
                Recipe recipe = new Recipe(
                        recipeId,
                        recipeName,
                        instructions
                );

                /*
                 * The recipe is added ONLY if every required
                 * ingredient passes the strict matching test.
                 */
                if (canMakeRecipe(recipe, pantryItems)) {

                    suggestedRecipes.add(recipe);
                }
            }

        } finally {

            // Always close the database cursor after processing.
            recipeCursor.close();
        }

        return suggestedRecipes;
    }


    // =========================================================
    // CHECK ONE RECIPE
    // =========================================================

    /**
     * Checks whether the pantry contains everything required
     * to prepare the specified recipe.
     */
    private boolean canMakeRecipe(
            Recipe recipe,
            List<PantryItem> pantryItems) {

        Cursor ingredientCursor =
                databaseHelper.getRecipeIngredients(
                        recipe.getId()
                );

        if (ingredientCursor == null) {
            return false;
        }

        try {

            /*
             * A recipe must contain at least one ingredient.
             */
            if (!ingredientCursor.moveToFirst()) {
                return false;
            }

            do {

                String requiredName =
                        ingredientCursor.getString(
                                ingredientCursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_INGREDIENT_NAME
                                )
                        );

                double requiredQuantity =
                        ingredientCursor.getDouble(
                                ingredientCursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_REQUIRED_QUANTITY
                                )
                        );

                String requiredUnit =
                        ingredientCursor.getString(
                                ingredientCursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_INGREDIENT_UNIT
                                )
                        );


                /*
                 * Find a pantry item that can satisfy this
                 * particular recipe ingredient.
                 */
                PantryItem matchingPantryItem =
                        findMatchingPantryItem(
                                requiredName,
                                requiredQuantity,
                                requiredUnit,
                                pantryItems
                        );


                /*
                 * If even ONE ingredient is missing or
                 * insufficient, the entire recipe fails.
                 */
                if (matchingPantryItem == null) {
                    return false;
                }

            } while (ingredientCursor.moveToNext());

        } finally {

            // Close the ingredient cursor after processing.
            ingredientCursor.close();
        }

        /*
         * Every ingredient passed.
         */
        return true;
    }


    // =========================================================
    // FIND MATCHING PANTRY ITEM
    // =========================================================

    /**
     * Searches the pantry for an ingredient that satisfies
     * the required name, unit, and quantity.
     */
    private PantryItem findMatchingPantryItem(
            String requiredName,
            double requiredQuantity,
            String requiredUnit,
            List<PantryItem> pantryItems) {

        for (PantryItem pantryItem : pantryItems) {

            /*
             * First compare normalized ingredient names.
             *
             * Example:
             * tomato  -> tomato
             * tomatoes -> tomato
             */
            if (!ingredientNamesMatch(
                    pantryItem.getName(),
                    requiredName)) {

                continue;
            }


            /*
             * Then check whether the units can actually
             * be compared.
             */
            if (!areUnitsCompatible(
                    pantryItem.getUnit(),
                    requiredUnit)) {

                continue;
            }


            /*
             * Convert both quantities to a common unit
             * where necessary.
             */
            double pantryQuantity =
                    convertToBaseUnit(
                            pantryItem.getQuantity(),
                            pantryItem.getUnit()
                    );

            double requiredAmount =
                    convertToBaseUnit(
                            requiredQuantity,
                            requiredUnit
                    );


            /*
             * STRICT quantity requirement:
             *
             * Pantry must contain AT LEAST the amount
             * required by the recipe.
             */
            if (pantryQuantity >= requiredAmount) {

                return pantryItem;
            }
        }

        // No suitable pantry item was found.
        return null;
    }


    // =========================================================
    // GET PANTRY ITEMS
    // =========================================================

    /**
     * Loads all pantry items from SQLite and converts
     * each database record into a PantryItem object.
     */
    private List<PantryItem> getPantryItems() {

        List<PantryItem> pantryItems =
                new ArrayList<>();

        Cursor cursor =
                databaseHelper.getAllPantryItems();

        if (cursor == null) {
            return pantryItems;
        }

        try {

            while (cursor.moveToNext()) {

                int id =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_PANTRY_ID
                                )
                        );

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_PANTRY_NAME
                                )
                        );

                double quantity =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_PANTRY_QUANTITY
                                )
                        );

                String unit =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_PANTRY_UNIT
                                )
                        );

                String expiryDate =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_PANTRY_EXPIRY
                                )
                        );

                // Convert the database row into a pantry model object.
                pantryItems.add(
                        new PantryItem(
                                id,
                                name,
                                quantity,
                                unit,
                                expiryDate
                        )
                );
            }

        } finally {

            // Close the cursor after all pantry records are loaded.
            cursor.close();
        }

        return pantryItems;
    }


    // =========================================================
    // INGREDIENT NAME NORMALIZATION
    // =========================================================

    /**
     * Compares two ingredient names after normalization.
     *
     * Normalization allows common variations such as
     * singular and plural ingredient names to match.
     */
    private boolean ingredientNamesMatch(
            String pantryName,
            String recipeName) {

        String pantry =
                normalizeIngredientName(pantryName);

        String recipe =
                normalizeIngredientName(recipeName);

        return pantry.equals(recipe);
    }


    /**
     * Converts ingredient names into a consistent format
     * before comparing them.
     */
    private String normalizeIngredientName(
            String ingredientName) {

        if (ingredientName == null) {
            return "";
        }

        String name =
                ingredientName
                        .trim()
                        .toLowerCase()
                        .replaceAll("\\s+", " ");

        /*
         * Handle common plural forms and a few common
         * irregular forms.
         */

        switch (name) {

            case "tomatoes":
                return "tomato";

            case "potatoes":
                return "potato";

            case "onions":
                return "onion";

            case "eggs":
                return "egg";

            case "bananas":
                return "banana";

            case "carrots":
                return "carrot";

            case "peas":
                return "pea";

            case "garlics":
                return "garlic";

            case "chickens":
                return "chicken";

            case "tunas":
                return "tuna";

            case "breads":
                return "bread";

            case "pastas":
                return "pasta";

            default:
                /*
                 * Basic plural handling.
                 */
                if (name.endsWith("ies") &&
                        name.length() > 3) {

                    return name.substring(
                            0,
                            name.length() - 3
                    ) + "y";
                }

                if (name.endsWith("es") &&
                        name.length() > 3) {

                    return name.substring(
                            0,
                            name.length() - 2
                    );
                }

                if (name.endsWith("s") &&
                        name.length() > 2) {

                    return name.substring(
                            0,
                            name.length() - 1
                    );
                }

                return name;
        }
    }


    // =========================================================
    // UNIT COMPATIBILITY
    // =========================================================

    /**
     * Determines whether the pantry unit and recipe unit
     * can be compared with each other.
     */
    private boolean areUnitsCompatible(
            String pantryUnit,
            String recipeUnit) {

        String pantry =
                normalizeUnit(pantryUnit);

        String recipe =
                normalizeUnit(recipeUnit);

        /*
         * Same unit.
         */
        if (pantry.equals(recipe)) {
            return true;
        }

        /*
         * Weight units:
         *
         * kg <-> grams
         */
        if (isWeightUnit(pantry) &&
                isWeightUnit(recipe)) {

            return true;
        }

        /*
         * Volume units:
         *
         * litres <-> ml
         */
        if (isVolumeUnit(pantry) &&
                isVolumeUnit(recipe)) {

            return true;
        }

        /*
         * Everything else must match directly.
         *
         * For example:
         *
         * pieces != slices
         * grams != pieces
         * ml != tablespoons
         */
        return false;
    }


    // =========================================================
    // NORMALIZE UNIT
    // =========================================================

    /**
     * Converts different spellings and abbreviations
     * into a standard unit name.
     */
    private String normalizeUnit(String unit) {

        if (unit == null) {
            return "";
        }

        String value =
                unit.trim().toLowerCase();

        switch (value) {

            case "g":
            case "gram":
            case "grams":
                return "grams";

            case "kg":
            case "kilogram":
            case "kilograms":
                return "kg";

            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "litres";

            case "piece":
            case "pieces":
                return "pieces";

            case "tablespoon":
            case "tablespoons":
            case "tbsp":
                return "tablespoons";

            case "teaspoon":
            case "teaspoons":
            case "tsp":
                return "teaspoons";

            case "slice":
            case "slices":
                return "slices";

            case "cup":
            case "cups":
                return "cups";

            default:
                return value;
        }
    }


    // =========================================================
    // UNIT GROUPS
    // =========================================================

    /**
     * Identifies units that represent weight.
     */
    private boolean isWeightUnit(String unit) {

        return unit.equals("grams") ||
                unit.equals("kg");
    }


    /**
     * Identifies units that represent volume.
     */
    private boolean isVolumeUnit(String unit) {

        return unit.equals("ml") ||
                unit.equals("litres");
    }


    // =========================================================
    // CONVERT TO BASE UNIT
    // =========================================================

    /**
     * Converts compatible weight and volume measurements
     * into their respective base units.
     *
     * Weight base unit  = grams
     * Volume base unit  = millilitres
     */
    private double convertToBaseUnit(
            double quantity,
            String unit) {

        String normalizedUnit =
                normalizeUnit(unit);

        /*
         * Weight base unit = grams
         */
        if (normalizedUnit.equals("kg")) {

            return quantity * 1000.0;
        }

        if (normalizedUnit.equals("grams")) {

            return quantity;
        }


        /*
         * Volume base unit = millilitres
         */
        if (normalizedUnit.equals("litres")) {

            return quantity * 1000.0;
        }

        if (normalizedUnit.equals("ml")) {

            return quantity;
        }


        /*
         * Pieces, tablespoons, teaspoons, slices and cups
         * remain in their own unit.
         */
        return quantity;
    }
}

