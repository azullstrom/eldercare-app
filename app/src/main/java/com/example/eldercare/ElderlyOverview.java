package com.example.eldercare;

import android.os.Bundle;
import android.view.View;
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
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class ElderlyOverview extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseLib databaseLib;
    Button button;
    TextView textView;
    String elderlyId ;
    ArrayList<Meal> mealList ;
    ArrayList<String> mealType;
    Timer timer ;
    TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set current activity to ac
        setContentView(R.layout.activity_elderly);
        button=findViewById(R.id.buttonElderly);
        textView = findViewById(R.id.textViewTest);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("elderly-users");
//        databaseReference = firebaseDatabase.getReference()
//                    .child("elderly-users").child("Dag1930")
//                    .child("meals").child("breakfast")
//                    .child("time");
        databaseLib = new DatabaseLib(this);

      timer = new Timer();
      timerTask= new TimerTask() {
          @Override
          public void run() {

          }


      };


        timer.scheduleAtFixedRate(timerTask,0,60000);

        mealType = new ArrayList<>(Arrays.asList("breakfast", "lunch", "dinner", "snack1", "snack2", "snack3"));


        elderlyId = getIntent().getStringExtra("usernameElderly");
        String elderlyFirstName = elderlyId.replaceAll("[^a-zA-Z]", ""); // Extract alphabetic characters
        String elderlyYear = elderlyId.replaceAll("[^0-9]", ""); // Extract numeric characters

       // getData();
       databaseLib.getMeals(elderlyId, new DatabaseLib.MealCallback() {
                   @Override
                   public void onMealsReceived(ArrayList<Meal> meals) {
                       for (int i=0;i<meals.size();i++){
                           databaseLib.setMealEaten(elderlyFirstName,elderlyYear,meals.get(i).getMealType(),false);
                       }
                       if (meals != null) {
                           int index =0;
                           Toast.makeText(ElderlyOverview.this, "Not Null", Toast.LENGTH_SHORT).show();
                          if (!meals.get(index).isEaten())
                          {
                              Toast.makeText(ElderlyOverview.this, "Test", Toast.LENGTH_SHORT).show();
                              test(index,meals,elderlyFirstName,elderlyYear);
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
//    private void checkMealTimes() {
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
//        String currentTime = sdf.format(new Date());
//
//        // Compare with meal times
//        for (String mealTime : mealTimes) {
//            if (currentTime.equals(mealTime)) {
//                // Trigger a notification
//
//                break;  // Break if a match is found
//            }
//        }
//    }

    public void test(int index,ArrayList<Meal> meals,String elderlyFirstName, String elderlyYear){
        if(index<meals.size()){
        if(!meals.get(index).isEaten()) {  //if the meal is true(eaten) get next meal
            button.setText("Next meal \n"+ mealType.get(index)+" \nat "+ meals.get(index).getTime());
            Toast.makeText(this, "From Inside", Toast.LENGTH_SHORT).show();
            if (index==3){
                button.setClickable(false);
            }
            final int finalIndex = index;
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    databaseLib.setMealEaten(elderlyFirstName,elderlyYear,meals.get(finalIndex).getMealType(),true);



                }

            });

        }

            index++;
            test(index,meals,elderlyFirstName,elderlyYear);
        }
    }



}
