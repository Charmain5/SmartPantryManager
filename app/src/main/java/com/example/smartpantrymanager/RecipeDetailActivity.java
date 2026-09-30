package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;

public class RecipeDetailActivity extends AppCompatActivity {

    // =========================================================
    // UI COMPONENTS
    // =========================================================

    // Displays the name of the selected recipe.
    private TextView tvRecipeName;

    // Displays all ingredients required for the recipe.
    private TextView tvRecipeIngredients;

    // Displays the preparation instructions for the recipe.
    private TextView tvRecipeInstructions;


    // =========================================================
    // DATABASE
    // =========================================================

    // Handles all database operations related to recipes.
    private DatabaseHelper databaseHelper;


    // =========================================================
    // RECIPE ID
    // =========================================================

    // Stores the ID of the recipe received from the previous screen.
    // -1 means that no valid recipe ID was provided.
    private int recipeId = -1;


    // =========================================================
    // ACTIVITY CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Load the recipe detail screen layout.
        setContentView(R.layout.activity_recipe_detail);


        // -----------------------------------------------------
        // Initialize database
        // -----------------------------------------------------

        // Create the database helper so recipe information
        // can be retrieved from the local SQLite database.
        databaseHelper = new DatabaseHelper(this);


        // -----------------------------------------------------
        // Initialize views
        // -----------------------------------------------------

        // Connect Java variables with the TextViews
        // defined in activity_recipe_detail.xml.
        initializeViews();


        // -----------------------------------------------------
        // Get recipe ID from Intent
        // -----------------------------------------------------

        // Retrieve the selected recipe ID that was passed
        // from the previous Activity.
        recipeId = getIntent().getIntExtra(
                "recipe_id",
                -1
        );


        // -----------------------------------------------------
        // Validate recipe ID
        // -----------------------------------------------------

        // A valid recipe ID is required to load recipe details.
        if (recipeId == -1) {

            Toast.makeText(
                    this,
                    "Recipe could not be found",
                    Toast.LENGTH_SHORT
            ).show();

            // Close this screen when no valid recipe exists.
            finish();

            return;
        }


        // -----------------------------------------------------
        // Load recipe
        // -----------------------------------------------------

        // Load the selected recipe information and ingredients.
        loadRecipeDetails();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        // Find the TextView used to display the recipe name.
        tvRecipeName =
                findViewById(
                        R.id.tvRecipeName
                );


        // Find the TextView used to display recipe ingredients.
        tvRecipeIngredients =
                findViewById(
                        R.id.tvRecipeIngredients
                );


        // Find the TextView used to display recipe instructions.
        tvRecipeInstructions =
                findViewById(
                        R.id.tvRecipeInstructions
                );
    }


    // =========================================================
    // LOAD RECIPE DETAILS
    // =========================================================

    private void loadRecipeDetails() {

        // Load general recipe information such as
        // the recipe name and preparation instructions.
        loadRecipeInformation();

        // Load the ingredients required by the selected recipe.
        loadRecipeIngredients();
    }


    // =========================================================
    // LOAD RECIPE INFORMATION
    // =========================================================

    private void loadRecipeInformation() {

        Cursor cursor = null;


        try {

            // Query the database for the selected recipe.
            cursor =
                    databaseHelper.getRecipeById(
                            recipeId
                    );


            if (cursor != null &&
                    cursor.moveToFirst()) {


                // -------------------------------------------------
                // Recipe name
                // -------------------------------------------------

                // Read the recipe name from the database.
                String recipeName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_RECIPE_NAME
                                )
                        );


                // -------------------------------------------------
                // Preparation instructions
                // -------------------------------------------------

                // Read the preparation instructions from the database.
                String instructions =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_RECIPE_INSTRUCTIONS
                                )
                        );


                // -------------------------------------------------
                // Display recipe name
                // -------------------------------------------------

                // Show the recipe name on the detail screen.
                tvRecipeName.setText(
                        recipeName
                );


                // -------------------------------------------------
                // Display instructions
                // -------------------------------------------------

                // Show the preparation instructions on the screen.
                tvRecipeInstructions.setText(
                        instructions
                );
            }

        } catch (Exception e) {

            // Display an error message if the recipe
            // information cannot be loaded.
            Toast.makeText(
                    this,
                    "Unable to load recipe",
                    Toast.LENGTH_SHORT
            ).show();

        } finally {

            // Always close the database cursor after use
            // to avoid leaving database resources open.
            if (cursor != null) {

                cursor.close();
            }
        }
    }


    // =========================================================
    // LOAD RECIPE INGREDIENTS
    // =========================================================

    private void loadRecipeIngredients() {

        Cursor cursor = null;


        // StringBuilder is used to build a formatted list
        // containing all recipe ingredients.
        StringBuilder ingredients =
                new StringBuilder();


        try {

            // Retrieve all ingredients associated with
            // the selected recipe from the database.
            cursor =
                    databaseHelper.getRecipeIngredients(
                            recipeId
                    );


            if (cursor != null) {

                while (cursor.moveToNext()) {


                    // -------------------------------------------------
                    // Ingredient name
                    // -------------------------------------------------

                    // Read the ingredient name from the database.
                    String name =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COL_INGREDIENT_NAME
                                    )
                            );


                    // -------------------------------------------------
                    // Required quantity
                    // -------------------------------------------------

                    // Read the required quantity for the ingredient.
                    double quantity =
                            cursor.getDouble(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COL_REQUIRED_QUANTITY
                                    )
                            );


                    // -------------------------------------------------
                    // Unit
                    // -------------------------------------------------

                    // Read the measurement unit such as
                    // grams, kilograms, cups, or pieces.
                    String unit =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COL_INGREDIENT_UNIT
                                    )
                            );


                    // -------------------------------------------------
                    // Add ingredient to list
                    // -------------------------------------------------

                    // Add the ingredient information to the
                    // formatted ingredient list.
                    ingredients
                            .append("• ")
                            .append(name)
                            .append(" — ")
                            .append(formatQuantity(quantity))
                            .append(" ")
                            .append(unit)
                            .append("\n");
                }
            }


            // ---------------------------------------------------------
            // Display ingredients
            // ---------------------------------------------------------

            // Display the complete formatted ingredient list.
            tvRecipeIngredients.setText(
                    ingredients.toString()
            );

        } catch (Exception e) {

            // Show an error message if ingredients cannot be loaded.
            Toast.makeText(
                    this,
                    "Unable to load ingredients",
                    Toast.LENGTH_SHORT
            ).show();

        } finally {

            // Close the cursor after the database operation is complete.
            if (cursor != null) {

                cursor.close();
            }
        }
    }


    // =========================================================
    // FORMAT QUANTITY
    // =========================================================

    private String formatQuantity(
            double quantity) {

        /*
         * Removes unnecessary decimal places from whole numbers.
         *
         * Example:
         * 2.0 -> 2
         * 2.5 -> 2.5
         */

        if (quantity == (long) quantity) {

            return String.valueOf(
                    (long) quantity
            );
        }


        return String.valueOf(
                quantity
        );
    }
}

