package com.example.eldercare;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ElderlyOverview extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseLib databaseLib;
    Button button;
    TextView textView;
    String elderlyId ;
    ArrayList<Meal> mealList ;
    ArrayList<String> mealType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set current activity to ac
        setContentView(R.layout.activity_elderly);
        button=findViewById(R.id.buttonElderly);
        textView = findViewById(R.id.textViewTest);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("elderly-users");
//        databaseReference = firebaseDatabase.getReference()
//                    .child("elderly-users").child("Dag1930")
//                    .child("meals").child("breakfast")
//                    .child("time");
        databaseLib = new DatabaseLib(this);

        //TODO Change "Dag1930" to currentUser and "breakfast" to mealTyep



        mealList = new ArrayList<>();
        elderlyId = getIntent().getStringExtra("usernameElderly");
        String elderlyFirstName = elderlyId.replaceAll("[^a-zA-Z]", ""); // Extract alphabetic characters
        String elderlyYear = elderlyId.replaceAll("[^0-9]", ""); // Extract numeric characters

        getData();
       databaseLib.getMeals(elderlyId, new DatabaseLib.MealCallback() {

           @Override
           public void onMealsReceived(ArrayList<Meal> meals) {
               databaseLib.setMealEaten(elderlyFirstName,elderlyYear,meals.get(0).getMealType(),false);
            if (meals != null && !meals.get(0).isEaten()) {

               String firstMeal = meals.get(0).getMealType();
               String firstMealTime = meals.get(0).getTime();
               button.setText("Next meal \n"+ firstMeal+" \nat "+ firstMealTime);
                switch (meals.get(0).getMealType()){
                    case "breakfast":
                        button.setText("Next meal \n"+ mealType.get(0)+" \nat "+ firstMealTime);
                        break;
                    case "lunch":
                        button.setText("Next meal \n"+ mealType.get(1)+" \nat "+ firstMealTime);
                        break;
                    case "dinner":
                        button.setText("Next meal \n"+ mealType.get(2)+" \nat "+ firstMealTime);
                        break;
                    case "snack1":
                        button.setText("Next meal \n"+ mealType.get(3)+" \nat "+ firstMealTime);
                        break;
                    case "snack2":
                        button.setText("Next meal \n"+ mealType.get(4)+" \nat "+ firstMealTime);
                        break;
                    case "snack3":
                        button.setText("Next meal \n"+ mealType.get(5)+" \nat "+ firstMealTime);
                        break;
                }

            }


           }
       });

    }




    public void getData() {
        databaseReference = firebaseDatabase.getReference()
                .child("elderly-users").child(elderlyId)
                .child("meals").child("snack1")
                .child("time");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                textView.setText(value);
                Toast.makeText(ElderlyOverview.this, "the id is  "+ elderlyId , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ElderlyOverview.this, "Fel when fetch", Toast.LENGTH_SHORT).show();
            }
        });
    }

    List<String> displayMealsDate(DatabaseReference ref) {
        ArrayList<String> list = new ArrayList<>();
        int i = 0;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot mealTypeSnapshot : snapshot.getChildren()) {
                    Meal meal = mealTypeSnapshot.getValue(Meal.class);
                    list.add(meal.getMealType());

                    textView.setText(list.get(0));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ElderlyOverview.this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
        return list;

    }




}
