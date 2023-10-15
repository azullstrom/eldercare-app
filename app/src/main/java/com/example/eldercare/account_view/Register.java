package com.example.eldercare.account_view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.eldercare.R;
import com.example.eldercare.modules.DatabaseLib;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;

public class Register extends AppCompatActivity {

    private TextInputEditText editTextUsername, editTextEmail, editTextFirstName, editTextLastName, editTextPhoneNumber, editTextPassword;
    private Button registerButton;
    private TextView textView;
    private DatabaseLib databaseLib;
    private String tokenString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseLib = new DatabaseLib(this);

        editTextUsername = findViewById(R.id.username);
        editTextEmail = findViewById(R.id.email);
        editTextFirstName = findViewById(R.id.firstname);
        editTextLastName = findViewById(R.id.lastname);
        editTextPhoneNumber = findViewById(R.id.phonenumber);
        editTextPassword = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);
        textView = findViewById(R.id.loginNow);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> tokenString = s);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String firstName = editTextFirstName.getText().toString();
                String lastName = editTextLastName.getText().toString();
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                String phone = editTextPhoneNumber.getText().toString();
                databaseLib.registerUser(username, firstName, lastName, email, password, phone, "", "caregiver", tokenString, new DatabaseLib.SuccessCallback() {
                    @Override
                    public void onSuccess() {
                        // Handle if needed
                    }

                    @Override
                    public void onFailure(String str) {
                        // Handle if needed
                    }
                });

                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
