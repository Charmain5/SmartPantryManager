package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    // =========================================================
    // UI COMPONENTS
    // =========================================================

    // Displays the application name on the Settings screen.
    private TextView tvAppName;

    // Displays the current application version.
    private TextView tvVersion;

    // Displays a short description of the application.
    private TextView tvDescription;


    // =========================================================
    // ACTIVITY CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Load the Settings screen layout.
        setContentView(R.layout.activity_settings);

        // Initialize the Settings screen views and
        // populate them with application information.
        initializeViews();
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private void initializeViews() {

        // Connect the Java variables with the TextViews
        // defined in activity_settings.xml.
        tvAppName = findViewById(R.id.tvAppName);

        tvVersion = findViewById(R.id.tvVersion);

        tvDescription = findViewById(R.id.tvDescription);


        // -----------------------------------------------------
        // APPLICATION INFORMATION
        // -----------------------------------------------------

        // Display the application name.
        tvAppName.setText("Smart Pantry Manager");


        // Display the current application version.
        tvVersion.setText("Version 1.0");


        // Display a short description explaining
        // the purpose of Smart Pantry Manager.
        tvDescription.setText(
                "Smart Pantry Manager helps you manage your pantry ingredients " +
                        "and find recipes based on the ingredients you currently have."
        );
    }
}

