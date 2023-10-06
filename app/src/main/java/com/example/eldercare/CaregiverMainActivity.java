package com.example.eldercare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CaregiverMainActivity extends AppCompatActivity {

    /*******************************************************
     *
     *
     * TODO:
     *  - Remove elder (reassign?!)
     *  - Display assigned elders (Fix better buttons)
     *  - Add existing elder functionality
     *
     *
     ******************************************************/

    DatabaseLib databaseLib = new DatabaseLib(this);
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private boolean eldersExist = true;
    LinearLayout eldersContainer;
    // Anders: Gjorde denna global i klassen så att man slipper hämta den hela tiden.
    private String usernameCaregiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caregiver_main);

        //Things that Will be moved somewhere else?
        TextView welcomeTextView = findViewById(R.id.your_patients);
        welcomeTextView.setText("Your Patients");
        ImageButton addPatientButton = findViewById(R.id.addImageButton);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        // Anders: instansierar variabeln
        usernameCaregiver = getIntent().getStringExtra("usernameCaregiver");

        /*  Checks if the caregiver has any elders assigned   */
        // Anders: usernameCaregiver istället för "Bengan"
        databaseLib.getAssignedElderlyDataSnapshot(usernameCaregiver, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.hasChildren()) {
                    eldersExist = true;
                } else {
                    eldersExist = false;
                }
                updateUI(eldersExist);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Plus (i.e add) button listener
        addPatientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewOrExistingPatientAlertDialog();
            }
        });
    }

    private void eldersIterator() {
        eldersContainer = findViewById(R.id.eldersContainer);
        eldersContainer.removeAllViews();
        List<String> elderKeys = new ArrayList<>();

        // Anders: usernameCaregiver istället för "Bengan"
        databaseLib.getAssignedElderlyDataSnapshot(usernameCaregiver, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                JSONObject json = databaseLib.convertSnapshotToJson(snapshot);

                try {
                    // Iterate over the keys in the JSON object
                    Iterator<String> keys = json.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        Object value = json.get(key);
                        elderKeys.add(key);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int index = 0;
                for(String elderKey : elderKeys) {
                    Button elderButton = new Button(CaregiverMainActivity.this);
                    elderButton.setText(elderKey);
                    elderButton.setTag(elderKey);
                    elderButton.setOnClickListener(view -> {
                        //Connect to Ahmads code
                        String selectedElderKey = (String)view.getTag();
                        Intent intent = new Intent(CaregiverMainActivity.this, CaregiverElderlyOverviewActivity.class);
                        intent.putExtra("elderlyName", selectedElderKey.substring(0, selectedElderKey.length() - 4));
                        intent.putExtra("elderlyYear", selectedElderKey.substring(selectedElderKey.length()-4));
                        startActivity(intent);
                    });
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    int topMargin = index == 0 ? 390 : 10; // Use counter variable here
                    params.setMargins(0, topMargin, 0, 10); // Apply topMargin here
                    elderButton.setLayoutParams(params);
                    eldersContainer.addView(elderButton);
                    index++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /*  Updates the UI depending on if there is elders assigned or not should also take in "key" (i.e elder name/ID I guess)  */
    private void updateUI(boolean eldersExist) {
        //Set to true if you want to see empty page layout
        //eldersExist = false;


        ImageView noPatientsImageView = findViewById(R.id.noPatientsImageView);
        ImageView arrowImageView = findViewById(R.id.arrowImageView);
        TextView noPatientsTextView = findViewById(R.id.noPatientsTextView);
        TextView clickToAddTextView = findViewById(R.id.clickToAddTextView);
        eldersContainer = findViewById(R.id.eldersContainer);

        //If exists -> Hide a lot of text, Show patients
        if(eldersExist) {
            noPatientsImageView.setVisibility(View.INVISIBLE);
            noPatientsTextView.setVisibility(View.INVISIBLE);
            clickToAddTextView.setVisibility(View.INVISIBLE);
            arrowImageView.setVisibility(View.INVISIBLE);
            eldersContainer.setVisibility(View.VISIBLE);
            eldersIterator();

        }
        //If not exists -> Show image, "no patients text"
        else {
            noPatientsImageView.setVisibility(View.VISIBLE);
            noPatientsTextView.setVisibility(View.VISIBLE);
            clickToAddTextView.setVisibility(View.VISIBLE);
            arrowImageView.setVisibility(View.VISIBLE);
            eldersContainer.setVisibility(View.GONE);
        }
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
        //Create new elder button listener
        addNewElderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewOrExistingAlertDialog.dismiss();
                addNewElderAlertDialog();
            }
        });
        //Add existing elder button listener
        addExistingElderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Add existing elder
            }
        });

        addNewOrExistingAlertDialog.show();
    }


    //Alert-Dialog when pressing "add new elder"
    private void addNewElderAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.activity_caregiver_main_add_new_popup, null);

        // Anders: EditText caregiverFirstName = dialogView.findViewById(R.id.caregiver_first_name);
        EditText elderFirstName = dialogView.findViewById(R.id.elder_first_name);
        EditText elderLastName = dialogView.findViewById(R.id.elder_last_name);
        EditText elderYearOfBirth = dialogView.findViewById(R.id.elder_year_of_birth);
        EditText elderUsername = dialogView.findViewById(R.id.elder_username);
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

        //Confirm button
        confirmNewElderButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                /* Tar input från allergirutan och skapar en lista av innehållet,
                 * separerat med ett kommatecken
                 * Tomt innehåll bör även accepteras. Inte testat
                 */

                // Anders: Lade till safe input och lade in alla editTexts i strängar.
                String firstName = elderFirstName.getText().toString().trim();
                String lastName = elderLastName.getText().toString().trim();
                String yearOfBirth = elderYearOfBirth.getText().toString().trim();
                String username = elderUsername.getText().toString().trim();
                String pin = elderPinCode.getText().toString().trim();
                String phone = elderPhoneNumber.getText().toString().trim();
                if(TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(yearOfBirth) ||
                        TextUtils.isEmpty(username) || TextUtils.isEmpty(pin) || TextUtils.isEmpty(phone)) {
                    Toast.makeText(CaregiverMainActivity.this, "Required fields missing", Toast.LENGTH_SHORT).show();
                    return;
                }

                String input = elderAllergies.getText().toString().trim();
                List<String> allergiesList = new ArrayList<>();
                if(!input.isEmpty()) {
                    String[] allergiesArray = input.split(",");
                    for (String allergy : allergiesArray) {
                        allergiesList.add(allergy.trim());
                    }
                }

                // Anders: usernameCaregiver och elderUsername istället för mail. Ändrade även till nya variablerna på allt annat. Se nedan
                databaseLib.assignAndCreateNewElderlyToCaregiver(
                        firstName,
                        lastName,
                        usernameCaregiver.trim(),
                        username,
                        pin,
                        phone,
                        yearOfBirth,
                        allergiesList);

                addNewElderAlertDialog.dismiss();
            }
        });
        addNewElderAlertDialog.show();
    }
}
