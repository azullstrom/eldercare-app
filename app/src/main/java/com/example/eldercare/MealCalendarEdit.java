package com.example.eldercare;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MealCalendarEdit extends AppCompatActivity {
    TextInputEditText editMealToEat;
    TextInputLayout layoutMealToEat;
    Spinner mealType;
    TextView patientAllergies;
    ImageView exit;
    RelativeLayout saveButton, deleteButton;
    CheckBox eatenBox;
    Meal meal;
    String typeInput, eatInput, elderlyName, elderlyYear;
    DatabaseLib database;
    int width, height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_calendar_edit);
        database = new DatabaseLib(this);
        meal = new Meal();

        meal.setToEat(getIntent().getStringExtra("mealToEat"));
        meal.setTime(getIntent().getStringExtra("mealTime"));
        meal.setDate(getIntent().getStringExtra("mealDate"));
        meal.setMealType(getIntent().getStringExtra("mealType"));
        elderlyYear = getIntent().getStringExtra("elderlyYear");
        elderlyName = getIntent().getStringExtra("elderlyName");

        mealType = findViewById(R.id.editTypeSpinner);
        editMealToEat = findViewById(R.id.editMealToEat);
        layoutMealToEat = findViewById(R.id.layoutMealToEat);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        eatenBox = findViewById(R.id.eatenBox);
        patientAllergies = findViewById(R.id.patientAllergiesEditTextView);

        layoutMealToEat.setHint(meal.getToEat());
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.9), (int) (height*0.5));

        exit = findViewById(R.id.exitEditMeal);
        exit.setOnClickListener(view -> finish());
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(getResources().getString(R.string.breakfast), getResources().getString(R.string.lunch), getResources().getString(R.string.dinner)));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealType.setAdapter(arrayAdapter);
        switch (meal.getMealType()){
            case "breakfast":
                mealType.setSelection(0);
                break;
            case "lunch":
                mealType.setSelection(1);
                break;
            case "dinner":
                mealType.setSelection(2);
                break;
        }

        final FirebaseDatabase databaseFire = FirebaseDatabase.getInstance();
        DatabaseReference ref = databaseFire.getReference("elderly-users/" + elderlyName + elderlyYear + "/allergies");
        //TODO: Refactor and move to database function
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //separator makes sure that "," appears like: item1, item2
                //                              and not like: ,item1, item2
                Character separator = Character.MIN_VALUE;
                for (DataSnapshot allergySnapshot: snapshot.getChildren()) {
                    patientAllergies.append(separator + " " + allergySnapshot.getValue().toString());
                    separator = ',';
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MealCalendarEdit.this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });

        saveButton.setOnClickListener(view -> {
            typeInput = String.valueOf(mealType.getSelectedItem());
            eatInput = String.valueOf(editMealToEat.getText());
            if(typeInput.matches("")){
                typeInput = meal.getMealType();
            }
            if(eatInput.matches("")){
                eatInput = meal.getToEat();
            }

            if(!eatInput.matches(meal.getToEat())){
                meal.setToEat(eatInput);
                database.setToEat(meal.getToEat(), elderlyName, elderlyYear, meal.getDate(), meal.getTime(), meal.getMealType());
            }
            if(!typeInput.matches(meal.getMealType())){
                //TODO: set meal type to new type, not added in database api yet
                //database.setType(meal.getToEat(), elderlyName, elderlyYear, meal.getDate(), meal.getTime(), meal.getMealType(), typeInput);

            }
            finish();
        });
        deleteButton.setOnClickListener(view -> {
            //TODO: add confirmation to delete
            Intent intent = new Intent(getApplicationContext(), MealCalendarConfirmDelete.class);
            intent.putExtra("elderlyName", elderlyName);
            intent.putExtra("elderlyYear", elderlyYear);
            intent.putExtra("mealDate", meal.getDate());
            intent.putExtra("mealType", meal.getMealType());
            startActivity(intent);
            finish();
        });

        eatenBox.setOnClickListener(view -> {
            //TODO: set eaten to true/false, not added in database structure yet
        });
    }
}