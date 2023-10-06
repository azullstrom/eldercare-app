package com.example.eldercare;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ElderlyOverview extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase ;
    DatabaseReference databaseReference;
    FirebaseUser user;
//    String elderlyUser , mealType;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         // Set current activity to ac
        setContentView(R.layout.activity_elderly);
        textView=findViewById(R.id.textViewTest);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference()
                    .child("elderly-users").child("Dag1930")
                    .child("meals").child("breakfast")
                    .child("time");
        //TODO Change "Dag1930" to currentUser and "breakfast" to mealTyep

       getData();
       user= FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Toast.makeText(this, "user is known " + user.getEmail() , Toast.LENGTH_SHORT).show();
        } else {
            // No user is signed in
            Toast.makeText(this, "user is unknown", Toast.LENGTH_SHORT).show();
        }

    }

        public void getData(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                textView.setText(value);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ElderlyOverview.this, "Fel when fetch", Toast.LENGTH_SHORT).show();
            }
        });
        }




}
