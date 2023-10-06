package com.example.eldercare;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Timer;

public class FirstTimeUse extends AppCompatActivity {

    Button elderlyButton, caregiverButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Ask for permission to use notifications (only asks if permission not granted)
        if (ContextCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{POST_NOTIFICATIONS}, 1);
        }
        FireBaseMessageReceiver rec = new FireBaseMessageReceiver();
        setContentView(R.layout.activity_first_time_use);

        //TODO: REMOVE, just for testing
        NotificationLib notificationLib = new NotificationLib(this, "Notification title", "This is the text");
        notificationLib.sendNotification("test");
        //Timer notificationTimer = notificationLib.scheduleRepeatableNotification(13,15);
        //notificationTimer.cancel();

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