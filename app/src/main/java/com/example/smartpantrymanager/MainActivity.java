```java
package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.PantryAdapter;
import com.example.smartpantrymanager.database.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // =========================================================
    // UI COMPONENTS
    // =========================================================

    private RecyclerView recyclerViewPantry;

    private Button btnAddIngredient;

    private Button btnSuggestedRecipes;

    private Button btnSettings;


    // =========================================================
    // DATABASE
    // =========================================================

    private DatabaseHelper databaseHelper;


    // =========================================================
    // ADAPTER & DATA
    // =========================================================

    private PantryAdapter pantryAdapter;

    private List<PantryItem> pantryItems;


    // =========================================================
    // ACTIVITY CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Load main layout
        setContentView(R.layout.activity_main);


        // -----------------------------------------------------
        // Initialize database
        // -----------------------------------------------------

        databaseHelper = new DatabaseHelper(this);


        // -----------------------------------------------------
        // Initialize UI
        // -----------------------------------------------------

        initializeViews();


        // -----------------------------------------------------
        // Configure RecyclerView
        // -----------------------------------------------------

        setupRecyclerView();


        // -----------------------------------------------------
        // Configure buttons
        // -----------------------------------------------------

        setupAddButton();

        setupSuggestedRecipesButton();

        setupSettingsButton();


        // -----------------------------------------------------
        // Load pantry data
        // -----------------------------------------------------

        loadPantryItems();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        recyclerViewPantry =
                findViewById(R.id.recyclerViewPantry);


        btnAddIngredient =
                findViewById(R.id.btnAddIngredient);


        btnSuggestedRecipes =
                findViewById(R.id.btnSuggestedRecipes);


        btnSettings =
                findViewById(R.id.btnSettings);
    }


    // =========================================================
    // RECYCLER VIEW
    // =========================================================

    private void setupRecyclerView() {

        recyclerViewPantry.setLayoutManager(
                new LinearLayoutManager(this)
        );


        recyclerViewPantry.setHasFixedSize(true);


        pantryItems = new ArrayList<>();


        pantryAdapter = new PantryAdapter(
                pantryItems,
                new PantryAdapter.OnPantryItemActionListener() {

                    @Override
                    public void onEdit(PantryItem item) {

                        openEditIngredient(item);
                    }


                    @Override
                    public void onDelete(PantryItem item) {

                        showDeleteConfirmation(item);
                    }
                }
        );


        recyclerViewPantry.setAdapter(
                pantryAdapter
        );
    }


    // =========================================================
    // ADD INGREDIENT BUTTON
    // =========================================================

    private void setupAddButton() {

        /*
         * Connects the Add Ingredient button on the main screen
         * to the Add/Edit Ingredient screen.
         *
         * The existing click behavior is kept unchanged.
         */

        btnAddIngredient.setOnClickListener(view -> {

            openAddIngredient();
        });
    }


    // =========================================================
    // OPEN ADD INGREDIENT SCREEN
    // =========================================================

    private void openAddIngredient() {

        /*
         * Opens AddEditIngredientActivity when the user
         * selects the Add Ingredient button.
         */

        Intent intent = new Intent(
                MainActivity.this,
                AddEditIngredientActivity.class
        );


        startActivity(intent);


        // Simple transition for now.
        // We will replace this with a better animation
        // during the final UI polish stage.

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
    }


    // =========================================================
    // SUGGESTED RECIPES BUTTON
    // =========================================================

    private void setupSuggestedRecipesButton() {

        /*
         * Connects the Suggested Recipes button on the main
         * screen to the Suggested Recipes screen.
         *
         * The existing navigation logic is unchanged.
         */

        btnSuggestedRecipes.setOnClickListener(view -> {

            openSuggestedRecipes();
        });
    }


    // =========================================================
    // OPEN SUGGESTED RECIPES SCREEN
    // =========================================================

    private void openSuggestedRecipes() {

        /*
         * Opens SuggestedRecipesActivity from the main screen.
         */

        Intent intent = new Intent(
                MainActivity.this,
                SuggestedRecipesActivity.class
        );


        startActivity(intent);


        // Simple transition for now.
        // Final attractive animation will be added
        // during the UI polish stage.

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
    }


    // =========================================================
    // SETTINGS BUTTON
    // =========================================================

    private void setupSettingsButton() {

        /*
         * Connects the Settings button on the main screen
         * to the Settings screen.
         *
         * The current button behavior is intentionally
         * unchanged.
         */

        btnSettings.setOnClickListener(view -> {

            openSettings();
        });
    }


    // =========================================================
    // OPEN SETTINGS SCREEN
    // =========================================================

    private void openSettings() {

        /*
         * Opens SettingsActivity when the Settings button
         * is selected from the main screen.
         */

        Intent intent = new Intent(
                MainActivity.this,
                SettingsActivity.class
        );


        startActivity(intent);


        // Simple transition for now.
        // Final attractive animation will be added
        // during the UI polish stage.

        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
    }


    // =========================================================
    // EDIT INGREDIENT
    // =========================================================

    private void openEditIngredient(PantryItem item) {

        Intent intent = new Intent(
                MainActivity.this,
                AddEditIngredientActivity.class
        );


        // Tell AddEditIngredientActivity
        // that this is EDIT mode.

        intent.putExtra(
                "ingredient_id",
                item.getId()
        );


        startActivity(intent);


        overridePendingTransition(
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
    }


    // =========================================================
    // LOAD PANTRY ITEMS FROM SQLITE
    // =========================================================

    private void loadPantryItems() {

        pantryItems.clear();


        Cursor cursor = null;


        try {

            cursor =
                    databaseHelper.getAllPantryItems();


            if (cursor != null) {

                while (cursor.moveToNext()) {

                    // -------------------------------------------------
                    // ID
                    // -------------------------------------------------

                    int id =
                            cursor.getInt(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COL_PANTRY_ID
                                    )
                            );


                    // -------------------------------------------------
                    // INGREDIENT NAME
                    // -------------------------------------------------

                    String name =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COL_PANTRY_NAME
                                    )
                            );


                    // -------------------------------------------------
                    // QUANTITY
                    // -------------------------------------------------

                    double quantity =
                            cursor.getDouble(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COL_PANTRY_QUANTITY
                                    )
                            );


                    // -------------------------------------------------
                    // UNIT
                    // -------------------------------------------------

                    String unit =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COL_PANTRY_UNIT
                                    )
                            );


                    // -------------------------------------------------
                    // EXPIRY DATE
                    // -------------------------------------------------

                    String expiryDate =
                            cursor.getString(
                                    cursor.getColumnIndexOrThrow(
                                            DatabaseHelper.COL_PANTRY_EXPIRY
                                    )
                            );


                    // -------------------------------------------------
                    // CREATE PANTRY MODEL
                    // -------------------------------------------------

                    PantryItem item =
                            new PantryItem(
                                    id,
                                    name,
                                    quantity,
                                    unit,
                                    expiryDate
                            );


                    // -------------------------------------------------
                    // ADD TO LIST
                    // -------------------------------------------------

                    pantryItems.add(item);
                }
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to load pantry items",
                    Toast.LENGTH_SHORT
            ).show();

        } finally {

            if (cursor != null) {

                cursor.close();
            }
        }


        // ---------------------------------------------------------
        // Refresh RecyclerView
        // ---------------------------------------------------------

        if (pantryAdapter != null) {

            pantryAdapter.notifyDataSetChanged();
        }


        // ---------------------------------------------------------
        // Handle empty pantry
        // ---------------------------------------------------------

        updateEmptyState();
    }


    // =========================================================
    // EMPTY PANTRY STATE
    // =========================================================

    private void updateEmptyState() {

        /*
         * We are keeping the RecyclerView visible for now.
         *
         * During the final UI stage we will add a proper
         * empty-state design with an icon/message.
         */

        recyclerViewPantry.setVisibility(
                View.VISIBLE
        );
    }


    // =========================================================
    // DELETE CONFIRMATION
    // =========================================================

    private void showDeleteConfirmation(
            PantryItem item) {

        new AlertDialog.Builder(this)

                .setTitle(
                        "Delete Ingredient"
                )

                .setMessage(
                        "Are you sure you want to delete \""
                                + item.getName()
                                + "\" from your pantry?"
                )

                .setPositiveButton(
                        "Delete",
                        (dialog, which) -> {

                            deletePantryItem(item);
                        }
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .show();
    }


    // =========================================================
    // DELETE INGREDIENT
    // =========================================================

    private void deletePantryItem(
            PantryItem item) {

        try {

            int deletedRows =
                    databaseHelper.deletePantryItem(
                            item.getId()
                    );


            if (deletedRows > 0) {

                Toast.makeText(
                        this,
                        item.getName()
                                + " deleted successfully",
                        Toast.LENGTH_SHORT
                ).show();


                // Reload data from SQLite
                loadPantryItems();

            } else {

                Toast.makeText(
                        this,
                        "Unable to delete ingredient",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "An error occurred while deleting",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // =========================================================
    // REFRESH WHEN RETURNING TO SCREEN
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();


        /*
         * When the user adds or edits an ingredient,
         * AddEditIngredientActivity closes and this Activity
         * becomes visible again.
         *
         * Reload the SQLite data so the RecyclerView
         * immediately shows the latest information.
         */

        if (databaseHelper != null &&
                pantryAdapter != null) {

            loadPantryItems();
        }
    }
}
```
