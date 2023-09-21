package com.example.eldercare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;

public class DatabaseLib {
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private Context context;

    /**
     * Constructor
     *
     * @param context Just write the class you're in: <code><b>this</b></code> should be fine.
     */
    public DatabaseLib(Context context) {
        this.context = context;
        rootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Fetches all data for an elderly. This snapshot can be sent into convertSnapshotIntoJson function.
     *
     * @param firstNameElderly First name of the elderly in the database.
     * @param callback Async. Add new ValueEventListener() {} and follow the automated functions.
     */
    public void getElderlyDataSnapshot(String firstNameElderly, ValueEventListener callback) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly);

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                callback.onDataChange(elderlySnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onCancelled(databaseError);
            }
        });
    }

    /**
     * Converts snapshot into JSON.
     *
     * @param dataSnapshot Snapshot from your async function call.
     *
     * @return JSON object || null
     */
    public JSONObject convertSnapshotToJson(DataSnapshot dataSnapshot) {
        JSONObject jsonObject = new JSONObject();

        try {
            // Iterate through the dataSnapshot to extract key-value pairs
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                String key = snapshot.getKey();
                Object value = snapshot.getValue();

                jsonObject.put(key, value);
            }

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds an existing elderly to an existing caregiver in the database.
     *
     * @param firstNameElderly First name of the elderly in the database.
     * @param firstNameCaregiver First name of the caregiver in the database.
     */
    public void assignElderlyToCaregiver(String firstNameElderly, String firstNameCaregiver) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly);
        DatabaseReference caregiverRef = rootRef.child("caregiver-users").child(firstNameCaregiver);

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    caregiverRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot caregiverSnapshot) {
                            if (caregiverSnapshot.exists()) {
                                // Assign the elderly to the caregiver by updating caregiver's node
                                caregiverRef.child("assigned-elderly").child(firstNameElderly).setValue(true);
                                Toast.makeText(context, "Successfully assigned!", Toast.LENGTH_SHORT).show();
                            } else {
                                // If the caregiver user doesn't exist
                                Toast.makeText(context, "Enter valid caregiver.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any database errors here
                        }
                    });
                } else {
                    // If the elderly user doesn't exist
                    Toast.makeText(context, "Enter valid elderly.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any database errors here
            }
        });
    }

    /**
     * Assign and adds a new elderly to an existing caregiver in the database.
     *
     * @param firstNameElderly First name of the elderly in the database.
     * @param lastNameElderly Last name of the elderly in the database.
     * @param firstNameCaregiver First name of the caregiver in the database.
     * @param email Elderlys new email. Example: example@elderly.eldercare.com
     * @param pin Elderlys new 6-digit PIN code. Example: 123456
     * @param phoneNumber XXX-XXX XX XX
     * @param yearOfBirth Example: 1900
     */
    public void assignAndCreateNewElderlyToCaregiver(String firstNameElderly, String lastNameElderly, String firstNameCaregiver, String email, String pin, String phoneNumber, String yearOfBirth) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly);
        DatabaseReference caregiverRef = rootRef.child("caregiver-users").child(firstNameCaregiver);

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    Toast.makeText(context, "Elderly already exists", Toast.LENGTH_SHORT).show();
                } else {
                    caregiverRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot caregiverSnapshot) {
                            if (caregiverSnapshot.exists()) {
                                // Assign the elderly to the caregiver by updating caregiver's node
                                caregiverRef.child("assigned-elderly").child(firstNameElderly).setValue(true);
                                registerUser(firstNameElderly, lastNameElderly, email, pin, phoneNumber, yearOfBirth, "elderly");
                                Toast.makeText(context, "Successfully added!", Toast.LENGTH_SHORT).show();
                            } else {
                                // If the caregiver user doesn't exist
                                Toast.makeText(context, "Enter valid caregiver.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(context, "Database error " + databaseError, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Database error " + databaseError, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Removes an existing elderly from an existing caregiver in the database.
     *
     * @param firstNameElderly First name of the elderly in the database.
     * @param firstNameCaregiver First name of the caregiver in the database.
     */
    public void removeElderlyFromCaregiver(String firstNameElderly, String firstNameCaregiver) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly);
        DatabaseReference caregiverRef = rootRef.child("caregiver-users").child(firstNameCaregiver);

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    caregiverRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot caregiverSnapshot) {
                            if (caregiverSnapshot.exists()) {
                                // Remove the elderly from the caregiver's assigned-elderly node
                                caregiverRef.child("assigned-elderly").child(firstNameElderly).removeValue();
                                Toast.makeText(context, "Successfully removed!", Toast.LENGTH_SHORT).show();
                            } else {
                                // If the caregiver user doesn't exist
                                Toast.makeText(context, "Enter valid caregiver.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any database errors here
                        }
                    });
                } else {
                    // If the elderly user doesn't exist
                    Toast.makeText(context, "Enter valid elderly.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any database errors here
            }
        });
    }

    /**
     * Adds a new meal for an elderly in the database.
     *
     * @param toEat Example: "hamburger"
     * @param firstNameElderly First name of the elderly in the database
     * @param date             Formatting: YYYY-MM-DD
     * @param time             Formatting: hh:mm
     * @param mealType         "breakfast" || "lunch" || "dinner"
     */
    public void addMealToElderly(String toEat, String firstNameElderly, String date, String time, String mealType) {
        if(!isMealParamFormattedCorrectly(mealType, date, time)) {
            Toast.makeText(context, "Not right formatting on parameters", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly);

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    DatabaseReference mealsRef = elderlyRef.child("meals");
                    Meal meal = new Meal(date, time, toEat, mealType);
                    DatabaseReference specificMealRef = mealsRef.child(date).child(mealType);

                    specificMealRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(context, "Meal already added", Toast.LENGTH_SHORT).show();
                            } else {
                                mealsRef.child(date).child(mealType).setValue(meal);
                                Toast.makeText(context, "Meal added successfully", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any database errors here
                        }
                    });
                } else {
                    // If the elderly user doesn't exist
                    Toast.makeText(context, "Elderly does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any database errors here
                Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Removes a meal for an elderly in the database.
     *
     * @param firstNameElderly First name of the elderly in the database
     * @param date             Formatting: YYYY-MM-DD
     * @param mealType         "breakfast" || "lunch" || "dinner"
     */
    public void removeMealFromElderly(String firstNameElderly, String date, String mealType) {
        if(!isMealParamFormattedCorrectly(mealType, date)) {
            Toast.makeText(context, "Not right formatting on parameters", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly);

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    DatabaseReference mealsRef = elderlyRef.child("meals");
                    DatabaseReference specificMealRef = mealsRef.child(date).child(mealType);

                    specificMealRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                specificMealRef.removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(context, "Meal removed successfully", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Handle removal failure
                                                    Toast.makeText(context, "Failed to remove meal", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            } else {
                                Toast.makeText(context, "Meal does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any database errors here
                        }
                    });
                } else {
                    // If the elderly user doesn't exist
                    Toast.makeText(context, "Elderly does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any database errors here
                Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sets "toEat" for an elderly in the database.
     *
     * @param toEat Example: "pizza"
     * @param firstNameElderly First name of the elderly in the database
     * @param date             Formatting: YYYY-MM-DD
     * @param time             Formatting: hh:mm
     * @param mealType         "breakfast" || "lunch" || "dinner"
     */
    public void setToEat(String toEat, String firstNameElderly, String date, String time, String mealType) {
        if(!isMealParamFormattedCorrectly(mealType, date, time)) {
            Toast.makeText(context, "Not right formatting on parameters", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly);

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    DatabaseReference mealsRef = elderlyRef.child("meals");
                    Meal meal = new Meal(date, time, toEat, mealType);
                    DatabaseReference specificMealRef = mealsRef.child(date).child(mealType);

                    specificMealRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                mealsRef.child(date).child(mealType).setValue(meal);
                                Toast.makeText(context, "Meal successfully edited", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Meal does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any database errors here
                        }
                    });
                } else {
                    // If the elderly user doesn't exist
                    Toast.makeText(context, "Elderly does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any database errors here
                Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Registers a new user in the database. Elderly or Caregiver.
     *
     * @param firstName First name user
     * @param lastName Last name user
     * @param email Email user
     * @param password 6-digit PIN for elderly
     * @param phoneNumber XXX-XXX XX XX
     * @param yearOfBirth Example: 1919
     * @param userType "elderly" || "caregiver"
     */
    public void registerUser(String firstName, String lastName, String email, String password, String phoneNumber, String yearOfBirth, String userType) {
        String firstNameUser = firstName.trim();
        String lastNameUser = lastName.trim();
        String emailUser = email.trim();
        String passwordUser = password.trim();
        String phoneUser = phoneNumber.trim();
        String yearOfBirthUser = yearOfBirth.trim();
        String userTypeUser = userType.trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(firstName) ||
                TextUtils.isEmpty(lastName) || TextUtils.isEmpty(phoneNumber) ||
                TextUtils.isEmpty(password)) {
            Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!userTypeUser.contains("caregiver") && !userTypeUser.contains("elderly")) {
            Toast.makeText(context, "Error parameter userType", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(emailUser, passwordUser)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // User registration successful
                    DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameUser+yearOfBirthUser);
                    DatabaseReference caregiverRef = rootRef.child("caregiver-users").child(firstNameUser);
                    DatabaseReference userReference;
                    if(userTypeUser.contains("caregiver")) {
                        userReference = caregiverRef;
                    } else {
                        userReference = elderlyRef;
                        userReference.child("year-of-birth").setValue(yearOfBirthUser);
                    }
                    userReference.child("email").setValue(emailUser);
                    userReference.child("firstname").setValue(firstNameUser);
                    userReference.child("lastname").setValue(lastNameUser);
                    userReference.child("phone-number").setValue(phoneUser);

                    Toast.makeText(context, "Registration Successful.", Toast.LENGTH_SHORT).show();
                } else {
                    // Registration failed
                    Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Login a user
     *
     * @param email Email user
     * @param password 6-digit PIN for elderly
     * @param userType "elderly" || "caregiver"
     */
    public void loginUser(String email, String password, String userType) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(context, "Enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Login Successful.", Toast.LENGTH_SHORT).show();

                            Intent intent;
                            if(userType.contains("elderly")) {
                                intent = new Intent(context, ElderlyMainActivity.class);

                            } else {
                                intent = new Intent(context, CaregiverMainActivity.class);
                            }
                            context.startActivity(intent);

                            if (context instanceof Activity) {
                                ((Activity) context).finish();
                            }
                        } else {
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Checks if parameters for meal functions are correctly formatted
     *
     * @param date             Formatting: YYYY-MM-DD
     * @param time             Formatting: hh:mm
     * @param mealType         "breakfast" || "lunch" || "dinner"
     */
    private boolean isMealParamFormattedCorrectly(String mealType, String date, String time) {
        boolean isRightMealTypeFormat = mealType.contains("breakfast")
                || mealType.contains("lunch") || mealType.contains("dinner");
        boolean isRightDateFormat = date.matches("\\d{4}-\\d{2}-\\d{2}");
        boolean isRightTimeFormat = time.matches("\\d{2}:\\d{2}");

        return isRightMealTypeFormat && isRightDateFormat && isRightTimeFormat;
    }

    /**
     * Checks if parameters for meal functions are correctly formatted
     *
     * @param date             Formatting: YYYY-MM-DD
     * @param mealType         "breakfast" || "lunch" || "dinner"
     */
    private boolean isMealParamFormattedCorrectly(String mealType, String date) {
        boolean isRightMealTypeFormat = mealType.contains("breakfast")
                || mealType.contains("lunch") || mealType.contains("dinner");
        boolean isRightDateFormat = date.matches("\\d{4}-\\d{2}-\\d{2}");

        return isRightMealTypeFormat && isRightDateFormat;
    }
}