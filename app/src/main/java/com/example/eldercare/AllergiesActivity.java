package com.example.eldercare;

import androidx.annotation.NonNull;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AllergiesActivity extends AppCompatActivity {

    private DatabaseLib databaseLib;
    private boolean isDeleteModeEnabled;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies);

        databaseLib = new DatabaseLib(this);

        List<String> allergyValues = new ArrayList<>();
        ImageView backButton = findViewById(R.id.back_button);
        ImageView newAllergy = findViewById(R.id.new_allergy_button);
        String elderlyName = getIntent().getStringExtra("elderlyName");
        String yearOfBirth = getIntent().getStringExtra("dateOfBirth");
        LinearLayout allergiesLayout = findViewById(R.id.allergies_layout);
        isDeleteModeEnabled = false;

        databaseLib.getElderlyAllergiesDataSnapshot(elderlyName, yearOfBirth, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                JSONObject json = databaseLib.convertSnapshotToJson(snapshot);
                List<String> allergyValues = new ArrayList<>();

                try {
                    Iterator<String> keys = json.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        Object value = json.get(key);
                        allergyValues.add(value.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Dynamically creates buttons for every assigned elder in the loop below
                LayoutInflater inflater = LayoutInflater.from(AllergiesActivity.this);

                // Clear the existing allergiesLayout before adding the updated list of allergies
                allergiesLayout.removeAllViews();

                for (String allergyValue : allergyValues) {
                    String allergy = allergyValue;

                    View customView = inflater.inflate(R.layout.allergy_card, null);
                    allergiesLayout.addView(customView);
                    TextView allergyName = customView.findViewById(R.id.allergySection);
                    customView.setTag(allergy);
                    allergyName.setText(allergy);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AllergiesActivity", "Failed to fetch allergies: " + error.getMessage());
            }
        });

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
        AlertDialog addAllergyDialog = builder.create();

        Window window = addAllergyDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }

        submitButton.setOnClickListener(view -> {
            String allergyText = newAllergyText.getText().toString();
            databaseLib.addAllergyToElderly(allergyText, elderlyName, yearOfBirth);
            addAllergyDialog.dismiss();
            recreate();
        });

        addAllergyDialog.show();
    }
}