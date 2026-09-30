package com.example.smartpantrymanager.model;

/**
 * Model class representing an ingredient required by a recipe.
 *
 * This class stores the relationship between a recipe and
 * one of its required ingredients, including the required
 * quantity and measurement unit.
 */
public class RecipeIngredient {

    // Unique database ID for this recipe ingredient record.
    private int id;

    // ID of the recipe this ingredient belongs to.
    private int recipeId;

    // Name of the required ingredient.
    private String ingredientName;

    // Quantity of the ingredient required by the recipe.
    private double requiredQuantity;

    // Measurement unit used for the required quantity.
    private String unit;


    /**
     * Creates a RecipeIngredient using an existing database ID
     * and recipe ID.
     *
     * This constructor is useful when loading recipe ingredient
     * records from the SQLite database.
     */
    public RecipeIngredient(
            int id,
            int recipeId,
            String ingredientName,
            double requiredQuantity,
            String unit) {

        this.id = id;
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
    }


    /**
     * Creates a new RecipeIngredient without database IDs.
     *
     * This constructor can be used when creating a new
     * recipe ingredient before it is stored in the database.
     */
    public RecipeIngredient(
            String ingredientName,
            double requiredQuantity,
            String unit) {

        this.ingredientName = ingredientName;
        this.requiredQuantity = requiredQuantity;
        this.unit = unit;
    }


    // =========================================================
    // GETTERS
    // =========================================================

    /**
     * Returns the database ID of this ingredient record.
     */
    public int getId() {
        return id;
    }


    /**
     * Returns the ID of the recipe associated with
     * this ingredient.
     */
    public int getRecipeId() {
        return recipeId;
    }


    /**
     * Returns the ingredient name.
     */
    public String getIngredientName() {
        return ingredientName;
    }


    /**
     * Returns the quantity required by the recipe.
     */
    public double getRequiredQuantity() {
        return requiredQuantity;
    }


    /**
     * Returns the measurement unit required by the recipe.
     */
    public String getUnit() {
        return unit;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    /**
     * Updates the database ID of the ingredient record.
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Updates the recipe ID associated with this ingredient.
     */
    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }


    /**
     * Updates the ingredient name.
     */
    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }


    /**
     * Updates the required quantity.
     */
    public void setRequiredQuantity(double requiredQuantity) {
        this.requiredQuantity = requiredQuantity;
    }


    /**
     * Updates the measurement unit.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
}

