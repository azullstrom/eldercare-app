package com.example.eldercare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class ForgotPassword extends AppCompatActivity {

    DatabaseLib databaseLib;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        databaseLib = new DatabaseLib(this);

        TextInputEditText editTextEmail;
        TextView loginButton;
        Button resetButton;

        editTextEmail = findViewById(R.id.email);
        loginButton = findViewById(R.id.loginNow);
        resetButton = findViewById(R.id.resetButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPassword.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(editTextEmail.getText());
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotPassword.this, "Enter email", Toast.LENGTH_SHORT).show();
                } else {
                    databaseLib.resetPassword(email);
                }

            }
        });
    }
}