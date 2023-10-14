package com.example.eldercare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

    private List<FrameLayout> deleteIcons;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergies);

        databaseLib = new DatabaseLib(this);
        isDeleteModeEnabled = false;
        deleteIcons = new ArrayList<FrameLayout>();

        List<String> allergyValues = new ArrayList<>();
        ImageView backButton = findViewById(R.id.back_button);
        ImageView newAllergy = findViewById(R.id.new_allergy_button);
        String elderlyName = getIntent().getStringExtra("elderlyName");
        String yearOfBirth = getIntent().getStringExtra("dateOfBirth");
        LinearLayout allergiesLayout = findViewById(R.id.allergies_layout);
        ImageView settingsButton = findViewById(R.id.elder_settings_icon);

        settingsButton.setOnClickListener(view -> {
            toggleDeleteMode();
        });

        databaseLib.getElderlyAllergiesDataSnapshot(elderlyName, yearOfBirth, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                JSONObject json = databaseLib.convertSnapshotToJson(snapshot);

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

                for (String allergyValue : allergyValues) {
                    String allergy = allergyValue;

                    View customView = inflater.inflate(R.layout.allergy_card, null);
                    allergiesLayout.addView(customView);

                    FrameLayout deleteAllergyIcon = customView.findViewById(R.id.delete_allergy);
                    deleteIcons.add(deleteAllergyIcon);

                    TextView allergyName = customView.findViewById(R.id.allergySection);
                    customView.setTag(allergy);
                    allergyName.setText(allergy);

                    deleteAllergyIcon.setOnClickListener(view -> {
                        // Fetch the allergy from the customView that contains the clicked deleteIcon
                        String clickedAllergy = (String) customView.getTag();
                        databaseLib.removeAllergyFromElderly(clickedAllergy, elderlyName, yearOfBirth, new DatabaseLib.AllergyRemovalCallback() {
                            @Override
                            public void onAllergyRemoved() {
                                recreate();
                            }

                            @Override
                            public void onAllergyRemovalError(String errorMessage) {

                            }
                        });
                    });

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

    private void toggleDeleteMode() {
        isDeleteModeEnabled = !isDeleteModeEnabled;
        FrameLayout deleteAllergy = findViewById(R.id.delete_allergy);

        if (isDeleteModeEnabled) {
            for(FrameLayout deleteIcon : deleteIcons) {
                deleteIcon.setVisibility(View.VISIBLE);
            }
        } else {
            deleteAllergy.setVisibility(View.INVISIBLE);
            for(FrameLayout deleteIcon : deleteIcons) {
                deleteIcon.setVisibility(View.INVISIBLE);
            }
        }
    }
}