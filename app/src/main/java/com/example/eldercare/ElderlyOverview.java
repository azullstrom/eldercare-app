package com.example.eldercare;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
    String elderlyId1 = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         // Set current activity to ac
        setContentView(R.layout.activity_elderly);
        textView=findViewById(R.id.textViewTest);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("elderly-users");
//        databaseReference = firebaseDatabase.getReference()
//                    .child("elderly-users").child("Dag1930")
//                    .child("meals").child("breakfast")
//                    .child("time");
        //TODO Change "Dag1930" to currentUser and "breakfast" to mealTyep


        String elderlyUsername  = getIntent().getStringExtra("usernameElderly");

        DatabaseLib databaseLib = new DatabaseLib(this);
              databaseLib.getElderlyIdByEmail(elderlyUsername + "@elderly.eldercare.com", new DatabaseLib.ElderlyIdCallback() {
                  @Override
                  public void onElderlyIdFound(String elderlyId) {
                      elderlyId1=elderlyId;

                      Toast.makeText(ElderlyOverview.this, elderlyId, Toast.LENGTH_SHORT).show();
                      databaseReference = firebaseDatabase.getReference()
                              .child("elderly-users").child(elderlyId)
                              .child("meals").child("breakfast")
                              .child("time");
                      getData();
                  }

                  @Override
                  public void onElderlyIdNotFound() {
                      Toast.makeText(ElderlyOverview.this, "Not Found", Toast.LENGTH_SHORT).show();

                  }

                  @Override
                  public void onError(String errorMessage) {
                      Toast.makeText(ElderlyOverview.this, "on error", Toast.LENGTH_SHORT).show();

                  }
              });






    }

    public void getData(){
            databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                textView.setText(value);
                Toast.makeText(ElderlyOverview.this, "the id is  "+ elderlyId1, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ElderlyOverview.this, "Fel when fetch", Toast.LENGTH_SHORT).show();
            }
        });
        }




}
