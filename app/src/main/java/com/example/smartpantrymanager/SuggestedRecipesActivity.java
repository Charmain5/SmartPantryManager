package com.example.smartpantrymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.matcher.StrictRecipeMatcher;
import com.example.smartpantrymanager.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    // =========================================================
    // UI COMPONENTS
    // =========================================================

    // RecyclerView used to display the list of suggested recipes.
    private RecyclerView recyclerViewRecipes;

    // TextView displayed when no matching recipes are available.
    private TextView tvNoRecipes;


    // =========================================================
    // DATABASE
    // =========================================================

    // Provides access to the local SQLite database.
    private DatabaseHelper databaseHelper;


    // =========================================================
    // ADAPTER & DATA
    // =========================================================

    // Adapter responsible for displaying recipe items.
    private RecipeAdapter recipeAdapter;

    // Stores the recipes suggested by the recipe matching system.
    private List<Recipe> suggestedRecipes;


    // =========================================================
    // ACTIVITY CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Load the Suggested Recipes screen layout.
        setContentView(
                R.layout.activity_suggested_recipes
        );


        // -----------------------------------------------------
        // Initialize database
        // -----------------------------------------------------

        // Create the database helper used to retrieve
        // pantry and recipe information.
        databaseHelper =
                new DatabaseHelper(this);


        // -----------------------------------------------------
        // Initialize views
        // -----------------------------------------------------

        // Connect Java variables with the views
        // defined in the XML layout.
        initializeViews();


        // -----------------------------------------------------
        // Setup RecyclerView
        // -----------------------------------------------------

        // Configure the RecyclerView used for displaying recipes.
        setupRecyclerView();


        // -----------------------------------------------------
        // Load recipes
        // -----------------------------------------------------

        // Find recipes that match the current pantry contents.
        loadSuggestedRecipes();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        // Find the RecyclerView used to display recipe suggestions.
        recyclerViewRecipes =
                findViewById(
                        R.id.recyclerViewRecipes
                );


        // Find the TextView used for the empty recipe state.
        tvNoRecipes =
                findViewById(
                        R.id.tvNoRecipes
                );
    }


    // =========================================================
    // RECYCLER VIEW
    // =========================================================

    private void setupRecyclerView() {

        // Use a vertical LinearLayoutManager so recipes
        // are displayed in a scrolling list.
        recyclerViewRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );


        // Improve RecyclerView performance by informing it
        // that the size of the RecyclerView itself is fixed.
        recyclerViewRecipes.setHasFixedSize(true);


        // Create an empty list that will later contain
        // the suggested recipes.
        suggestedRecipes =
                new ArrayList<>();


        // Create the recipe adapter and connect each recipe
        // click to the recipe detail screen.
        recipeAdapter =
                new RecipeAdapter(
                        suggestedRecipes,
                        recipe -> openRecipeDetails(recipe)
                );


        // Attach the adapter to the RecyclerView.
        recyclerViewRecipes.setAdapter(
                recipeAdapter
        );
    }


    // =========================================================
    // LOAD SUGGESTED RECIPES
    // =========================================================

    private void loadSuggestedRecipes() {

        // Create the strict recipe matcher.
        //
        // The matcher is responsible for finding recipes
        // that can be prepared using the user's pantry items.
        StrictRecipeMatcher matcher =
                new StrictRecipeMatcher(
                        databaseHelper
                );


        // Retrieve the recipes that match the current
        // pantry ingredients.
        List<Recipe> results =
                matcher.getSuggestedRecipes();


        // Clear any previous suggestions before
        // displaying the latest results.
        suggestedRecipes.clear();


        // Add the newly generated suggestions to the list.
        suggestedRecipes.addAll(
                results
        );


        // Notify RecyclerView that the recipe data
        // has been updated.
        recipeAdapter.notifyDataSetChanged();


        // -----------------------------------------------------
        // Empty state
        // -----------------------------------------------------

        // If there are no matching recipes, hide the
        // RecyclerView and display the empty-state message.
        if (suggestedRecipes.isEmpty()) {

            recyclerViewRecipes.setVisibility(
                    View.GONE
            );

            tvNoRecipes.setVisibility(
                    View.VISIBLE
            );

        } else {

            // When recipes are available, display the
            // RecyclerView and hide the empty-state message.
            recyclerViewRecipes.setVisibility(
                    View.VISIBLE
            );

            tvNoRecipes.setVisibility(
                    View.GONE
            );
        }
    }


    // =========================================================
    // OPEN RECIPE DETAILS
    // =========================================================

    private void openRecipeDetails(
            Recipe recipe) {

        // Create an Intent to open the recipe detail screen.
        android.content.Intent intent =
                new android.content.Intent(
                        this,
                        RecipeDetailActivity.class
                );


        // Pass the selected recipe ID to the detail Activity
        // so the correct recipe can be loaded.
        intent.putExtra(
                "recipe_id",
                recipe.getId()
        );


        // Open the recipe detail screen.
        startActivity(intent);


        // Apply a simple fade transition between screens.
        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
    }


    // =========================================================
    // REFRESH WHEN RETURNING
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();


        /*
         * Refresh the suggested recipes whenever the user
         * returns to this screen.
         *
         * This ensures that recipe suggestions reflect
         * the latest pantry data.
         */

        if (databaseHelper != null &&
                recipeAdapter != null) {

            loadSuggestedRecipes();
        }
    }
}

