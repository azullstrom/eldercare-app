package com.example.eldercare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirstTimeUse extends AppCompatActivity {

    Button elderlyButton, caregiverButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_use);

        mAuth = FirebaseAuth.getInstance();
        elderlyButton = findViewById(R.id.elderlyButton);
        caregiverButton = findViewById(R.id.caregiverButton);

        elderlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showElderlyMailDialog();
            }
        });

        caregiverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFirstTimeUsePreferences(false, true);
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setFirstTimeUsePreferences(boolean isFirstTimeUse, boolean isCaregiver) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isFirstTimeUse", isFirstTimeUse);
        editor.putBoolean("isCaregiver", isCaregiver);
        editor.apply();
    }

    private void setFirstTimeUsePreferences(boolean isFirstTimeUse, boolean isCaregiver, String elderlyMail) {
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("elderlyMail", elderlyMail);
        editor.putBoolean("isFirstTimeUse", isFirstTimeUse);
        editor.putBoolean("isCaregiver", isCaregiver);
        editor.apply();
    }

    private void showElderlyMailDialog() {
        final EditText email = new EditText(FirstTimeUse.this);
        final EditText pin = new EditText(FirstTimeUse.this);
        email.setHint("Enter elderly email");
        pin.setHint("Enter PIN code");

        LinearLayout layout = new LinearLayout(FirstTimeUse.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(email);
        layout.addView(pin);

        new AlertDialog.Builder(FirstTimeUse.this)
                .setTitle("First Time Use as Elderly")
                .setMessage("Authorization")
                .setView(layout)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String elderlyMail = email.getText().toString().trim();
                        String elderlyPin = pin.getText().toString().trim();
                        if (TextUtils.isEmpty(elderlyMail) || elderlyMail.contains("caregiver")) {
                            Toast.makeText(FirstTimeUse.this, "Enter a valid email.", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.signInWithEmailAndPassword(elderlyMail, elderlyPin)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                setFirstTimeUsePreferences(false, false, elderlyMail);
                                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Toast.makeText(FirstTimeUse.this, "Authentication failed.",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        dialog.dismiss();
                    }
                })
                .create().show();
    }
}