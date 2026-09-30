package com.example.smartpantrymanager.model;

/**
 * Model class representing a single ingredient stored
 * in the user's pantry.
 *
 * This class stores the ingredient's:
 * - Database ID
 * - Name
 * - Quantity
 * - Unit
 * - Optional expiry date
 */
public class PantryItem {

    // Unique database ID for the pantry item.
    private int id;

    // Ingredient name, such as tomato, rice, or chicken.
    private String name;

    // Amount of the ingredient currently available.
    private double quantity;

    // Measurement unit used for the quantity.
    private String unit;

    // Optional expiry date of the ingredient.
    private String expiryDate;


    /**
     * Creates a PantryItem using an existing database ID.
     *
     * This constructor is mainly used when loading
     * existing pantry records from SQLite.
     */
    public PantryItem(int id, String name, double quantity,
                      String unit, String expiryDate) {

        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }


    /**
     * Creates a new PantryItem without a database ID.
     *
     * This constructor is useful when creating a new
     * ingredient before it is inserted into the database.
     */
    public PantryItem(String name, double quantity,
                      String unit, String expiryDate) {

        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }


    // =========================================================
    // GETTERS
    // =========================================================

    /**
     * Returns the database ID of this pantry item.
     */
    public int getId() {
        return id;
    }


    /**
     * Returns the ingredient name.
     */
    public String getName() {
        return name;
    }


    /**
     * Returns the available ingredient quantity.
     */
    public double getQuantity() {
        return quantity;
    }


    /**
     * Returns the measurement unit.
     */
    public String getUnit() {
        return unit;
    }


    /**
     * Returns the expiry date.
     *
     * The value may be empty when no expiry date
     * was provided by the user.
     */
    public String getExpiryDate() {
        return expiryDate;
    }


    // =========================================================
    // SETTERS
    // =========================================================

    /**
     * Updates the database ID.
     */
    public void setId(int id) {
        this.id = id;
    }


    /**
     * Updates the ingredient name.
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Updates the available quantity.
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }


    /**
     * Updates the measurement unit.
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }


    /**
     * Updates the expiry date.
     */
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
}

