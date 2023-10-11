package com.example.eldercare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
     *  - Finish implementing deletion/unassign
     *  - Implement alert icon when notification has been received
     *  - Add existing elder functionality
     *
     *
     ******************************************************/

    DatabaseLib databaseLib = new DatabaseLib(this);
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private boolean eldersExist = true;

    private String elderlyLastName;
    LinearLayout eldersContainer;
    // Anders: Gjorde denna global i klassen så att man slipper hämta den hela tiden.
    private String usernameCaregiver;
    private boolean isDeleteModeEnabled = false;
    private List<ImageView> deleteIcons = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root_layout);

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





    public interface ElderlyLastNameCallback {
        void onLastNameReceived(String lastName);
        void onError(String errorMessage);
    }

    private void getEldersLastName(String elderID, ElderlyLastNameCallback callback) {
        databaseLib.getElderlyLastName(elderID, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String elderlyLastName = snapshot.getValue(String.class);
                    callback.onLastNameReceived(elderlyLastName);
                } else {
                    callback.onError("ElderlyId not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError("Database error");
            }
        });
    }

    private void eldersIterator(View newContent) {
        eldersContainer = newContent.findViewById(R.id.display_elders_eldersContainer);
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
                //Dynamically creates buttons for every assigned elder in the loop below
                LayoutInflater inflater = LayoutInflater.from(CaregiverMainActivity.this);
                for (String elderKey : elderKeys) {
                    String elderlyFirstName = elderKey.substring(0, elderKey.length() - 4);
                    getEldersLastName(elderKey, new ElderlyLastNameCallback() {
                        @Override
                        public void onLastNameReceived(String lastName) {
                            View customView = inflater.inflate(R.layout.patient_card, null);
                            eldersContainer.addView(customView);
                            ImageView deleteElderIcon = customView.findViewById(R.id.delete_elder);
                            deleteIcons.add(deleteElderIcon);
                            TextView patientFullName = customView.findViewById(R.id.patientFullName);
                            TextView patientID = customView.findViewById(R.id.patientID);
                            customView.setTag(elderKey);
                            patientFullName.setText(elderlyFirstName + " " + lastName);
                            patientID.setText("ID: " + elderKey);

                            customView.setOnClickListener(v -> {
                                String selectedElderKey = (String) v.getTag();
                                String yearOfBirth = selectedElderKey.replaceAll("[^0-9]", ""); // Extract numeric part
                                Intent intent = new Intent(CaregiverMainActivity.this, CaregiverElderlyOverviewActivity.class);
                                intent.putExtra("elderlyName", elderlyFirstName);
                                intent.putExtra("dateOfBirth", yearOfBirth);
                                startActivity(intent);
                            });
                        }

                        @Override
                        public void onError(String errorMessage) {
                            Toast.makeText(CaregiverMainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /*  Updates the UI depending on if there is elders assigned or not*/
    private void updateUI(boolean eldersExist) {
        FrameLayout contentView = findViewById(R.id.contentView);
        LayoutInflater inflater = LayoutInflater.from(this);
        View newContent;
        //eldersExist = false;

        //If exists -> Show patients
        if(eldersExist) {
            newContent = inflater.inflate(R.layout.display_elders, contentView, false);
            ImageView elderSettingsImageView = newContent.findViewById(R.id.elder_settings_icon);
            eldersSettings(elderSettingsImageView);
            eldersIterator(newContent);
        }
        //If not exists -> Show image, "no patients text"
        else {
            newContent = inflater.inflate(R.layout.activity_caregiver_main, contentView, false);
        }

        contentView.removeAllViews();
        contentView.addView(newContent);
    }


    private void eldersSettings(ImageView eldersSettings) {
        eldersSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("YOU CLICKED THE SETTINGS BUTTON!!!!!!");
                isDeleteModeEnabled = !isDeleteModeEnabled;
                toggleDeleteMode();
                /******************************************************
                 * TODO:
                 * Implement remove (unassign) functionality
                 */
            }
        });
    }

    private void toggleDeleteMode() {
        ImageView deleteElder = findViewById(R.id.delete_elder);

        if (isDeleteModeEnabled) {
            for(ImageView deleteIcon : deleteIcons) {
                deleteIcon.setVisibility(View.VISIBLE);
            }
        } else {
            deleteElder.setVisibility(View.INVISIBLE);
            for(ImageView deleteIcon : deleteIcons) {
                deleteIcon.setVisibility(View.INVISIBLE);
            }
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
