package com.example.eldercare;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class AllergiesActivity extends AppCompatActivity {

    private DatabaseLib databaseLib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies);

        databaseLib = new DatabaseLib(this);

        ImageView backButton = findViewById(R.id.back_button);
        ImageView newAllergy = findViewById(R.id.new_allergy_button);
        String elderlyName = getIntent().getStringExtra("elderlyName");
        String yearOfBirth = getIntent().getStringExtra("dateOfBirth");

        backButton.setOnClickListener(view -> {
            finish();
        });

        newAllergy.setOnClickListener(view -> {
            addNewAllergyDialog(elderlyName, yearOfBirth);
        });
    }

    private void addNewAllergyDialog(String elderlyName, String yearOfBirth) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_allergies_addpopup, null);

        EditText newAllergyText = dialogView.findViewById(R.id.allergy_edit_text);
        Button submitButton = dialogView.findViewById(R.id.submit_allergy_button);

        builder.setView(dialogView);
        AlertDialog addNewOrExistingAlertDialog = builder.create();

        //**
        //Code below changes the dimension and position of the dialog box.
        //Should probably be abstracted away in a function
        //**
        Window window = addNewOrExistingAlertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }

        String allergyText = newAllergyText.getText().toString();

        submitButton.setOnClickListener(view -> {
            databaseLib.addAllergyToElderly(allergyText, elderlyName, yearOfBirth);
        });

        addNewOrExistingAlertDialog.show();
    }
}