package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.database.DatabaseHelper;

public class AddEditIngredientActivity extends AppCompatActivity {

    // ==========================================
    // UI COMPONENTS
    // ==========================================

    private EditText etIngredientName;
    private EditText etQuantity;
    private EditText etExpiryDate;

    private Spinner spinnerUnit;

    private Button btnSaveIngredient;

    private TextView tvFormTitle;


    // ==========================================
    // DATABASE
    // ==========================================

    private DatabaseHelper databaseHelper;


    // ==========================================
    // EDIT MODE
    // ==========================================

    /*
     * -1 means we are adding a new ingredient.
     *
     * If this contains a valid database ID,
     * we are editing an existing ingredient.
     */
    private int ingredientId = -1;


    // ==========================================
    // UNIT OPTIONS
    // ==========================================

    private final String[] units = {
            "Select Unit",
            "pieces",
            "grams",
            "kg",
            "ml",
            "litres",
            "cups",
            "tablespoons",
            "teaspoons"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_add_edit_ingredient
        );


        // ==========================================
        // INITIALIZE DATABASE
        // ==========================================

        databaseHelper = new DatabaseHelper(this);


        // ==========================================
        // INITIALIZE VIEWS
        // ==========================================

        initializeViews();


        // ==========================================
        // SETUP UNIT SPINNER
        // ==========================================

        setupUnitSpinner();


        // ==========================================
        // CHECK ADD OR EDIT MODE
        // ==========================================

        checkEditMode();


        // ==========================================
        // SAVE BUTTON
        // ==========================================

        btnSaveIngredient.setOnClickListener(
                v -> saveIngredient()
        );
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        tvFormTitle =
                findViewById(R.id.tvFormTitle);

        etIngredientName =
                findViewById(R.id.etIngredientName);

        etQuantity =
                findViewById(R.id.etQuantity);

        etExpiryDate =
                findViewById(R.id.etExpiryDate);

        spinnerUnit =
                findViewById(R.id.spinnerUnit);

        btnSaveIngredient =
                findViewById(R.id.btnSaveIngredient);
    }


    // =========================================================
    // SETUP UNIT SPINNER
    // =========================================================

    private void setupUnitSpinner() {

        ArrayAdapter<String> unitAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        units
                );

        unitAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerUnit.setAdapter(unitAdapter);
    }


    // =========================================================
    // CHECK WHETHER ADD OR EDIT MODE
    // =========================================================

    private void checkEditMode() {

        /*
         * MainActivity sends:
         *
         * intent.putExtra("ingredient_id", item.getId());
         *
         * when the user clicks Edit.
         */

        ingredientId =
                getIntent().getIntExtra(
                        "ingredient_id",
                        -1
                );


        if (ingredientId != -1) {

            // ==========================================
            // EDIT MODE
            // ==========================================

            tvFormTitle.setText(
                    "Edit Ingredient"
            );

            btnSaveIngredient.setText(
                    "Update Ingredient"
            );

            loadIngredientData();

        } else {

            // ==========================================
            // ADD MODE
            // ==========================================

            tvFormTitle.setText(
                    "Add Ingredient"
            );

            btnSaveIngredient.setText(
                    "Save Ingredient"
            );
        }
    }


    // =========================================================
    // LOAD EXISTING INGREDIENT
    // =========================================================

    private void loadIngredientData() {

        Cursor cursor = null;

        try {

            cursor =
                    databaseHelper.getPantryItemById(
                            ingredientId
                    );


            if (cursor != null &&
                    cursor.moveToFirst()) {


                // ==========================================
                // NAME
                // ==========================================

                String name =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_PANTRY_NAME
                                )
                        );


                // ==========================================
                // QUANTITY
                // ==========================================

                double quantity =
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_PANTRY_QUANTITY
                                )
                        );


                // ==========================================
                // UNIT
                // ==========================================

                String unit =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_PANTRY_UNIT
                                )
                        );


                // ==========================================
                // EXPIRY DATE
                // ==========================================

                String expiryDate =
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        DatabaseHelper.COL_PANTRY_EXPIRY
                                )
                        );


                // ==========================================
                // PUT DATA INTO FORM
                // ==========================================

                etIngredientName.setText(name);

                etQuantity.setText(
                        formatQuantity(quantity)
                );

                etExpiryDate.setText(
                        expiryDate
                );


                // ==========================================
                // SELECT UNIT IN SPINNER
                // ==========================================

                selectUnit(unit);
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Unable to load ingredient",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
    }


    // =========================================================
    // FORMAT QUANTITY
    // =========================================================

    private String formatQuantity(double quantity) {

        /*
         * If the database contains:
         *
         * 5.0
         *
         * display:
         *
         * 5
         *
         * instead of:
         *
         * 5.0
         */

        if (quantity == (long) quantity) {

            return String.valueOf(
                    (long) quantity
            );
        }

        return String.valueOf(quantity);
    }


    // =========================================================
    // SELECT UNIT
    // =========================================================

    private void selectUnit(String unit) {

        for (int i = 0; i < units.length; i++) {

            if (units[i].equalsIgnoreCase(unit)) {

                spinnerUnit.setSelection(i);

                return;
            }
        }
    }


    // =========================================================
    // SAVE / UPDATE INGREDIENT
    // =========================================================

    private void saveIngredient() {

        // ==========================================
        // GET FORM VALUES
        // ==========================================

        String name =
                etIngredientName
                        .getText()
                        .toString()
                        .trim();


        String quantityText =
                etQuantity
                        .getText()
                        .toString()
                        .trim();


        String unit =
                spinnerUnit
                        .getSelectedItem()
                        .toString();


        String expiryDate =
                etExpiryDate
                        .getText()
                        .toString()
                        .trim();


        // ==========================================
        // VALIDATE NAME
        // ==========================================

        if (TextUtils.isEmpty(name)) {

            etIngredientName.setError(
                    "Ingredient name is required"
            );

            etIngredientName.requestFocus();

            return;
        }


        // ==========================================
        // VALIDATE QUANTITY
        // ==========================================

        if (TextUtils.isEmpty(quantityText)) {

            etQuantity.setError(
                    "Quantity is required"
            );

            etQuantity.requestFocus();

            return;
        }


        double quantity;

        try {

            quantity =
                    Double.parseDouble(
                            quantityText
                    );

        } catch (NumberFormatException e) {

            etQuantity.setError(
                    "Enter a valid quantity"
            );

            etQuantity.requestFocus();

            return;
        }


        // ==========================================
        // QUANTITY MUST BE GREATER THAN ZERO
        // ==========================================

        if (quantity <= 0) {

            etQuantity.setError(
                    "Quantity must be greater than 0"
            );

            etQuantity.requestFocus();

            return;
        }


        // ==========================================
        // VALIDATE UNIT
        // ==========================================

        if (spinnerUnit.getSelectedItemPosition() == 0) {

            Toast.makeText(
                    this,
                    "Please select a unit",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // ==========================================
        // ADD OR UPDATE?
        // ==========================================

        if (ingredientId == -1) {

            // ==========================================
            // ADD NEW INGREDIENT
            // ==========================================

            addNewIngredient(
                    name,
                    quantity,
                    unit,
                    expiryDate
            );

        } else {

            // ==========================================
            // UPDATE EXISTING INGREDIENT
            // ==========================================

            updateExistingIngredient(
                    name,
                    quantity,
                    unit,
                    expiryDate
            );
        }
    }


    // =========================================================
    // ADD NEW INGREDIENT
    // =========================================================

    private void addNewIngredient(
            String name,
            double quantity,
            String unit,
            String expiryDate) {


        long result =
                databaseHelper.addPantryItem(
                        name,
                        quantity,
                        unit,
                        expiryDate
                );


        if (result != -1) {

            Toast.makeText(
                    this,
                    "Ingredient added successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to add ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    // =========================================================
    // UPDATE EXISTING INGREDIENT
    // =========================================================

    private void updateExistingIngredient(
            String name,
            double quantity,
            String unit,
            String expiryDate) {


        int result =
                databaseHelper.updatePantryItem(
                        ingredientId,
                        name,
                        quantity,
                        unit,
                        expiryDate
                );


        if (result > 0) {

            Toast.makeText(
                    this,
                    "Ingredient updated successfully",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Failed to update ingredient",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}