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

    private TextView tvRecipeName;

    private TextView tvRecipeIngredients;

    private TextView tvRecipeInstructions;


    // =========================================================
    // DATABASE
    // =========================================================

    private DatabaseHelper databaseHelper;


    // =========================================================
    // RECIPE ID
    // =========================================================

    private int recipeId = -1;


    // =========================================================
    // ACTIVITY CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Load recipe detail layout
        setContentView(R.layout.activity_recipe_detail);


        // -----------------------------------------------------
        // Initialize database
        // -----------------------------------------------------

        databaseHelper = new DatabaseHelper(this);


        // -----------------------------------------------------
        // Initialize views
        // -----------------------------------------------------

        initializeViews();


        // -----------------------------------------------------
        // Get recipe ID from Intent
        // -----------------------------------------------------

        recipeId = getIntent().getIntExtra(
                "recipe_id",
                -1
        );


        // -----------------------------------------------------
        // Validate recipe ID
        // -----------------------------------------------------

        if (recipeId == -1) {

            Toast.makeText(
                    this,
                    "Recipe could not be found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // -----------------------------------------------------
        // Load recipe
        // -----------------------------------------------------

        loadRecipeDetails();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        tvRecipeName =
                findViewById(
                        R.id.tvRecipeName
                );


        tvRecipeIngredients =
                findViewById(
                        R.id.tvRecipeIngredients
                );


        tvRecipeInstructions =
                findViewById(
                        R.id.tvRecipeInstructions
                );
    }


    // =========================================================
    // LOAD RECIPE DETAILS
    // =========================================================

    private void loadRecipeDetails() {

        loadRecipeInformation();

        loadRecipeIngredients();
    }


    // =========================================================
    // LOAD RECIPE INFORMATION
    // =========================================================

    private void loadRecipeInformation() {

        Cursor cursor = null;


        try {

            cursor =
                    databaseHelper.getRecipeById(
                            recipeId
                    );


            if (cursor != null &&
                    cursor.moveToFirst()) {


                // -------------------------------------------------
                // Recipe name
                // -------------------------------------------------

                String recipeName =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_RECIPE_NAME
                                )
                        );


                // -------------------------------------------------
                // Preparation instructions
                // -------------------------------------------------

                String instructions =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_RECIPE_INSTRUCTIONS
                                )
                        );


                // -------------------------------------------------
                // Display recipe name
                // -------------------------------------------------

                tvRecipeName.setText(
                        recipeName
                );


                // -------------------------------------------------
                // Display instructions
                // -------------------------------------------------

                tvRecipeInstructions.setText(
                        instructions
                );
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to load recipe",
                    Toast.LENGTH_SHORT
            ).show();

        } finally {

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


        StringBuilder ingredients =
                new StringBuilder();


        try {

            cursor =
                    databaseHelper.getRecipeIngredients(
                            recipeId
                    );


            if (cursor != null) {

                while (cursor.moveToNext()) {


                    // -------------------------------------------------
                    // Ingredient name
                    // -------------------------------------------------

                    String name =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COL_INGREDIENT_NAME
                                    )
                            );


                    // -------------------------------------------------
                    // Required quantity
                    // -------------------------------------------------

                    double quantity =
                            cursor.getDouble(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COL_REQUIRED_QUANTITY
                                    )
                            );


                    // -------------------------------------------------
                    // Unit
                    // -------------------------------------------------

                    String unit =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COL_INGREDIENT_UNIT
                                    )
                            );


                    // -------------------------------------------------
                    // Add ingredient to list
                    // -------------------------------------------------

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

            tvRecipeIngredients.setText(
                    ingredients.toString()
            );

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to load ingredients",
                    Toast.LENGTH_SHORT
            ).show();

        } finally {

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

        // Example:
        // 2.0 -> 2
        // 2.5 -> 2.5

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