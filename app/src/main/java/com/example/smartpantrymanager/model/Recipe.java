package com.example.smartpantrymanager.model;

/**
 * Model class representing a recipe in the Smart Pantry Manager.
 *
 * A recipe contains:
 * - A unique database ID
 * - The recipe name
 * - Preparation instructions
 */
public class Recipe {

    // Unique database ID for the recipe.
    private int id;

    // Name of the recipe.
    private String name;

    // Instructions describing how to prepare the recipe.
    private String instructions;


    /**
     * Creates a Recipe object using the information
     * retrieved from the database.
     *
     * @param id unique recipe ID
     * @param name recipe name
     * @param instructions recipe preparation instructions
     */
    public Recipe(
            int id,
            String name,
            String instructions) {

        this.id = id;
        this.name = name;
        this.instructions = instructions;
    }


    // =========================================================
    // GETTERS
    // =========================================================

    /**
     * Returns the unique database ID of the recipe.
     */
    public int getId() {
        return id;
    }


    /**
     * Returns the recipe name.
     */
    public String getName() {
        return name;
    }


    /**
     * Returns the recipe preparation instructions.
     */
    public String getInstructions() {
        return instructions;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    /**
     * Updates the recipe database ID.
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Updates the recipe name.
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Updates the recipe preparation instructions.
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}

