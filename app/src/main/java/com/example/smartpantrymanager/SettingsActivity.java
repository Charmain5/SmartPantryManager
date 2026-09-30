package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private TextView tvAppName;
    private TextView tvVersion;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
    }

    private void initializeViews() {
        tvAppName = findViewById(R.id.tvAppName);
        tvVersion = findViewById(R.id.tvVersion);
        tvDescription = findViewById(R.id.tvDescription);

        tvAppName.setText("Smart Pantry Manager");
        tvVersion.setText("Version 1.0");
        tvDescription.setText(
                "Smart Pantry Manager helps you manage your pantry ingredients " +
                        "and find recipes based on the ingredients you currently have."
        );
    }
}