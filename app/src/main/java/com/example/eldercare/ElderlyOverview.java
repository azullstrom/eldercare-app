package com.example.eldercare;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ElderlyOverview extends AppCompatActivity {


    FirebaseDatabase firebaseDatabase ;
    DatabaseReference databaseReference;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         // Set current activity to ac
        setContentView(R.layout.activity_elderly);
        textView.findViewById(R.id.textViewTest);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("elderly-users").child("Dag1930").child("meals");

    }


}
