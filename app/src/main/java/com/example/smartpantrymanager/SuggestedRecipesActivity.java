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

    private RecyclerView recyclerViewRecipes;

    private TextView tvNoRecipes;


    // =========================================================
    // DATABASE
    // =========================================================

    private DatabaseHelper databaseHelper;


    // =========================================================
    // ADAPTER & DATA
    // =========================================================

    private RecipeAdapter recipeAdapter;

    private List<Recipe> suggestedRecipes;


    // =========================================================
    // ACTIVITY CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_suggested_recipes
        );


        // -----------------------------------------------------
        // Initialize database
        // -----------------------------------------------------

        databaseHelper =
                new DatabaseHelper(this);


        // -----------------------------------------------------
        // Initialize views
        // -----------------------------------------------------

        initializeViews();


        // -----------------------------------------------------
        // Setup RecyclerView
        // -----------------------------------------------------

        setupRecyclerView();


        // -----------------------------------------------------
        // Load recipes
        // -----------------------------------------------------

        loadSuggestedRecipes();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        recyclerViewRecipes =
                findViewById(
                        R.id.recyclerViewRecipes
                );


        tvNoRecipes =
                findViewById(
                        R.id.tvNoRecipes
                );
    }


    // =========================================================
    // RECYCLER VIEW
    // =========================================================

    private void setupRecyclerView() {

        recyclerViewRecipes.setLayoutManager(
                new LinearLayoutManager(this)
        );


        recyclerViewRecipes.setHasFixedSize(true);


        suggestedRecipes =
                new ArrayList<>();


        recipeAdapter =
                new RecipeAdapter(
                        suggestedRecipes,
                        recipe -> openRecipeDetails(recipe)
                );


        recyclerViewRecipes.setAdapter(
                recipeAdapter
        );
    }


    // =========================================================
    // LOAD SUGGESTED RECIPES
    // =========================================================

    private void loadSuggestedRecipes() {

        StrictRecipeMatcher matcher =
                new StrictRecipeMatcher(
                        databaseHelper
                );


        List<Recipe> results =
                matcher.getSuggestedRecipes();


        suggestedRecipes.clear();


        suggestedRecipes.addAll(
                results
        );


        recipeAdapter.notifyDataSetChanged();


        // -----------------------------------------------------
        // Empty state
        // -----------------------------------------------------

        if (suggestedRecipes.isEmpty()) {

            recyclerViewRecipes.setVisibility(
                    View.GONE
            );

            tvNoRecipes.setVisibility(
                    View.VISIBLE
            );

        } else {

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

        android.content.Intent intent =
                new android.content.Intent(
                        this,
                        RecipeDetailActivity.class
                );


        intent.putExtra(
                "recipe_id",
                recipe.getId()
        );


        startActivity(intent);


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


        if (databaseHelper != null &&
                recipeAdapter != null) {

            loadSuggestedRecipes();
        }
    }
}