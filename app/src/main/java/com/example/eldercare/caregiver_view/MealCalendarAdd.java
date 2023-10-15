package com.example.eldercare.caregiver_view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.eldercare.R;
import com.example.eldercare.modules.DatabaseLib;
import com.example.eldercare.modules.Meal;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MealCalendarAdd extends AppCompatActivity {
    TextInputEditText editMealName;
    Spinner mealType;
    TimePicker timePicker;
    RelativeLayout addMealButton;
    ImageView exitButton;
    TextView patientAllergies;
    Meal meal;
    String elderlyName, elderlyYear;
    DatabaseLib database;
    int width, height;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mealcalendar_add);

        editMealName = findViewById(R.id.editMealName);
        timePicker = findViewById(R.id.timePicker);
        exitButton = findViewById(R.id.exitAddMeal);
        addMealButton = findViewById(R.id.addNewMealButton);
        patientAllergies = findViewById(R.id.patientAllergiesTextView);
        mealType = findViewById(R.id.addTypeSpinner);

        elderlyName = getIntent().getStringExtra("elderlyName");
        elderlyYear = getIntent().getStringExtra("elderlyYear");

        database = new DatabaseLib(this);

        //TODO: refactor and make it database function
        final FirebaseDatabase databaseFire = FirebaseDatabase.getInstance();
        DatabaseReference ref = databaseFire.getReference("elderly-users/" + elderlyName + elderlyYear + "/allergies");

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
                Toast.makeText(MealCalendarAdd.this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });

        timePicker.setIs24HourView(true);
        
        meal = new Meal();
        timePicker.setHour(12);
        timePicker.setMinute(30);
        meal.setTime(timePicker.getHour() + ":" + timePicker.getMinute());

        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList("breakfast", "lunch", "dinner", "snack1", "snack2", "snack3"));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealType.setAdapter(arrayAdapter);

        exitButton.setOnClickListener(view -> finish());

        addMealButton.setOnClickListener(view -> {
            meal.setMealType(String.valueOf(mealType.getSelectedItem()));
            meal.setToEat(String.valueOf(editMealName.getText()));
            if(meal.getToEat().matches("") || meal.getMealType().matches("")){
                Toast.makeText(MealCalendarAdd.this, R.string.please_enter_all_info, Toast.LENGTH_SHORT).show();
                return;
            }
            database.addMealToElderly(meal.getToEat(), elderlyName, elderlyYear, meal.getTime(), meal.getMealType(), false);
            finish();
        });

        timePicker.setOnTimeChangedListener((timePicker, i, i1) -> {
            String time = "";
            if(i < 10){
                time += "0";
            }
            time += i + ":";
            if(i1 < 10){
                time +=  "0";
            }
            time += i1;
            meal.setTime(time);
        });
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        height = dm.heightPixels;
        getWindow().setLayout((int) (width*0.9), (int) (height*0.9));
    }
}