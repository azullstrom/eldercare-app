package com.example.eldercare;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class CaregiverMainActivity extends AppCompatActivity {

    /*******************************************************
     * TODO:
     *  - Error handling in the textfields (no empty spaces, numbers etc)?
     *  - Link the input to database
     *  - Add existing elder functionality
     *
     *
     ******************************************************/

    DatabaseLib databaseLib = new DatabaseLib(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_main);

        //Things that Will be moved somewhere else?
        TextView welcomeTextView = findViewById(R.id.your_patients);
        welcomeTextView.setText("Your Patients");
        ImageView noPatientsImageView = findViewById(R.id.noPatientsImageView);
        TextView noPatientsTextView = findViewById(R.id.noPatientsTextView);
        ImageButton addPatientButton = findViewById(R.id.addImageButton);

        //Listener for "plus" ImageButton
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewOrExistingPatientAlertDialog();
            }
        });

    }

    protected boolean patientsExists() {
        //If not exists -> Show image, "no patients text" and plus Imagebutton
        //If exists -> Show patients (another method)
        //TODO
        return true;
    }


    //AlertDialog-method (When pressing plus ImageButton)
    private void addNewOrExistingPatientAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_caregiver_main_add_popup, null);

        Button addNewElderButton = dialogView.findViewById(R.id.add_new_elder_button);
        Button addExistingElderButton = dialogView.findViewById(R.id.add_existing_elder_button);


        builder.setView(dialogView);
        AlertDialog addNewOrExistingAlertDialog = builder.create();

        //**
        //Code below changes the dimension and position of the dialog box.
        //Should probably be abstracted away in a function
        //**
        Window window = addNewOrExistingAlertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }
        addNewElderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewOrExistingAlertDialog.dismiss();
                addNewElderAlertDialog();
            }
        });
        addExistingElderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Add existing elder
            }
        });

        addNewOrExistingAlertDialog.show();
    }


    private void addNewElderAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_caregiver_main_add_new_popup, null);

        EditText caregiverFirstName = dialogView.findViewById(R.id.caregiver_first_name);
        EditText elderFirstName = dialogView.findViewById(R.id.elder_first_name);
        EditText elderLastName = dialogView.findViewById(R.id.elder_last_name);
        EditText elderYearOfBirth = dialogView.findViewById(R.id.elder_year_of_birth);
        EditText elderEmail = dialogView.findViewById(R.id.elder_email);
        EditText elderPinCode = dialogView.findViewById(R.id.elder_pin_code);
        EditText elderPhoneNumber = dialogView.findViewById(R.id.elder_phone_number);
        EditText elderAllergies = dialogView.findViewById(R.id.elder_allergies);
        Button confirmNewElderButton = dialogView.findViewById(R.id.confirm_new_elder);

        builder.setView(dialogView);

        AlertDialog addNewElderAlertDialog = builder.create();
        Window window = addNewElderAlertDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }

        confirmNewElderButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /*TODO:
                 * Check for empty fields -> input to database
                 */
                databaseLib.assignAndCreateNewElderlyToCaregiver(
                        elderFirstName.getText().toString().trim(),
                        elderLastName.getText().toString().trim(),
                        caregiverFirstName.getText().toString().trim(),
                        elderEmail.getText().toString().trim(),
                        elderPinCode.getText().toString().trim(),
                        elderPhoneNumber.getText().toString().trim(),
                        elderYearOfBirth.getText().toString().trim());

                addNewElderAlertDialog.dismiss();
            }
        });
        addNewElderAlertDialog.show();
    }
}
