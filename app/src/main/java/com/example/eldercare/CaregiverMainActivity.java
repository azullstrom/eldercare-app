package com.example.eldercare;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CaregiverMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_main);


        //Things that Will be moved somewhere else
        TextView welcomeTextView = findViewById(R.id.your_patients);
        welcomeTextView.setText("Your Patients");
        ImageView noPatientsImageView = findViewById(R.id.noPatientsImageView);
        TextView noPatientsTextView = findViewById(R.id.noPatientsTextView);
        ImageButton addPatientButton = findViewById(R.id.addImageButton);


        //Listener for "plus" ImageButton
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

    }


    //AlertDialog-method (When pressing plus ImageButton)
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_caregiver_main_dialogbox, null);

        Button addNewElderButton = dialogView.findViewById(R.id.add_new_elder);
        Button addExistingElderButton = dialogView.findViewById(R.id.add_existing_elder);

        addNewElderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add new elder
            }
        });

        addExistingElderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add existing elder
            }
        });

        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();

        //Changes the dimension and position of the dialog box. Feels too over complicated
        Window window = alertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }

        alertDialog.show();
    }



    protected boolean patientsExists() {
        //If not exists -> Show image, "no patients text" and plus Imagebutton
        //If exists -> Show patients (another method)
        //TODO
        return true;
    }

    protected void addPatient() {
        //TODO
    }
}
