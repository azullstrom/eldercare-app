package com.example.eldercare;

import android.os.Bundle;
import android.util.Log;
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
    TextView textView;
    String elderlyId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set current activity to ac
        setContentView(R.layout.activity_elderly);
        textView = findViewById(R.id.textViewTest);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("elderly-users");
//        databaseReference = firebaseDatabase.getReference()
//                    .child("elderly-users").child("Dag1930")
//                    .child("meals").child("breakfast")
//                    .child("time");
        databaseLib = new DatabaseLib(this);
        //TODO Change "Dag1930" to currentUser and "breakfast" to mealTyep


        elderlyId = getIntent().getStringExtra("usernameElderly");
        getData();
       databaseLib.getMeals(elderlyId, new DatabaseLib.MealCallback() {
           @Override
           public void onMealsReceived(ArrayList<Meal> meals) {
               for (Meal meal: meals ) {
                   Log.d("Meals",meal.getMealType());
                   Log.d("Meals",meal.getTime());
                   Log.d("Meals",String.valueOf(meal.isEaten()));
                   Log.d("Meals","++++++++++++++++++++++++++++++++++");
               }
           }
       });
    }




    public void getData() {
        databaseReference = firebaseDatabase.getReference()
                .child("elderly-users").child(elderlyId)
                .child("meals").child("breakfast")
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
