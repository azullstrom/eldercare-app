package com.example.eldercare;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirstTimeUse extends AppCompatActivity {

    Button elderlyButton, caregiverButton;
    FirebaseAuth mAuth;
    DatabaseLib databaseLib ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_use);

        mAuth = FirebaseAuth.getInstance();
        elderlyButton = findViewById(R.id.elderlyButton);
        caregiverButton = findViewById(R.id.caregiverButton);
        databaseLib= new DatabaseLib(this);

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
        View dialogView = getLayoutInflater().inflate(R.layout.activity_elderly_emailpopup, null);

        TextInputEditText username = dialogView.findViewById(R.id.username);
        TextInputEditText pin = dialogView.findViewById(R.id.pin);

        new AlertDialog.Builder(FirstTimeUse.this)
                .setView(dialogView)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String usernameElderly = username.getText().toString().trim();
                        String elderlyMail =  usernameElderly + "@elderly.eldercare.com";
                        String elderlyPin = pin.getText().toString().trim() + "00";
                        if (TextUtils.isEmpty(usernameElderly) || TextUtils.isEmpty(elderlyPin)) {
                            Toast.makeText(FirstTimeUse.this, "All fields required.", Toast.LENGTH_SHORT).show();
                        } else {
                            mAuth.signInWithEmailAndPassword(elderlyMail, elderlyPin)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                setFirstTimeUsePreferences(false, false, elderlyMail);
                                                //Add getElderlyIdByEmail and send Id by Intent to ElderlyOverview
                                                databaseLib.getElderlyIdByEmail(elderlyMail, new DatabaseLib.ElderlyIdCallback() {
                                                    @Override
                                                    public void onElderlyIdFound(String elderlyId) {
                                                        Intent intent = new Intent(getApplicationContext(), ElderlyOverview.class);
                                                        intent.putExtra("usernameElderly",elderlyId);
                                                        startActivity(intent);
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onElderlyIdNotFound() {

                                                    }

                                                    @Override
                                                    public void onError(String errorMessage) {

                                                    }
                                                });

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