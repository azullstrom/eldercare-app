package com.example.eldercare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DatabaseLib {
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private Context context;

    private boolean mealAdded;

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
     * @param yearOfBirth Example: 1920
     * @param callback Async. Add new ValueEventListener() {} and follow the automated functions.
     */
    public void getElderlyDataSnapshot(String firstNameElderly, String yearOfBirth, ValueEventListener callback) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly.trim()+yearOfBirth.trim());

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
     * Fetches all data for an elderly. This snapshot can be sent into convertSnapshotIntoJson function.
     *
     * @param usernameCaregiver Username of the caregiver.
     * @param callback Async. Add new ValueEventListener() {} and follow the automated functions.
     */
    public void getAssignedElderlyDataSnapshot(String usernameCaregiver, ValueEventListener callback) {
        DatabaseReference assignedRef = rootRef.child("caregiver-users").child(usernameCaregiver.trim()).child("assigned-elderly");

        assignedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onDataChange(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onCancelled(databaseError);
            }
        });
    }


    /**
     * Get elderly-id by email.
     */
    public void getElderlyIdByEmail(final String email, final ElderlyIdCallback callback) {
        DatabaseReference databaseReference = rootRef.child("elderly-users");
        Query query = databaseReference.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String elderlyId = snapshot.getKey();
                        callback.onElderlyIdFound(elderlyId);
                        break;
                    }
                } else {
                    // No elderly user found with the given email
                    callback.onElderlyIdNotFound();
                }
            }

           @Override
           public void onCancelled(DatabaseError databaseError) {
                // Handle any errors here
                callback.onError(databaseError.getMessage());
            }
        });
    }

   public interface ElderlyIdCallback {
        void onElderlyIdFound(String elderlyId);

        void onElderlyIdNotFound();

        void onError(String errorMessage);
    }

    /**
     * Get caregiver email by username.
     */
    public void getCaregiverEmailByUsername(String username, ValueEventListener callback) {
        DatabaseReference emailRef = rootRef.child("caregiver-users").child(username.trim()).child("email");

        emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onDataChange(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onCancelled(databaseError);
            }
        });
    }

    /**
     * Get elderly last name by elderly-id
     */
    public void getElderlyLastName(String elderlyId, ValueEventListener callback){
        DatabaseReference lastNameRef = rootRef.child("elderly-users").child(elderlyId).child("lastname");

        lastNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onDataChange(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCancelled(error);
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
     * @param yearOfBirthElderly Example: 1920
     * @param usernameCaregiver Username of the caregiver in the database.
     */
    public void assignElderlyToCaregiver(String firstNameElderly, String yearOfBirthElderly, String usernameCaregiver) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly.trim()+yearOfBirthElderly.trim());
        DatabaseReference caregiverRef = rootRef.child("caregiver-users").child(usernameCaregiver);

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    caregiverRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot caregiverSnapshot) {
                            if (caregiverSnapshot.exists()) {
                                // Assign the elderly to the caregiver by updating caregiver's node
                                caregiverRef.child("assigned-elderly").child(firstNameElderly.trim()+yearOfBirthElderly.trim()).setValue(true);
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
     * @param usernameCaregiver First name of the caregiver in the database.
     * @param username Elderlys new username. Example: gregerboy
     * @param pin Elderlys new 6-digit PIN code. Example: 123456
     * @param phoneNumber XXX-XXX XX XX
     * @param yearOfBirth Example: 1900
     * @param allergies String list of allergies. Call like this: Arrays.asList("peanuts", "shrimp")
     */
    public void assignAndCreateNewElderlyToCaregiver(String firstNameElderly, String lastNameElderly, String usernameCaregiver, String username, String pin, String phoneNumber, String yearOfBirth, List<String> allergies) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly.trim()+yearOfBirth.trim());
        DatabaseReference caregiverRef = rootRef.child("caregiver-users").child(usernameCaregiver);

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
                                String email = username.trim() + "@elderly.eldercare.com";
                                String pinCode = pin.trim() + "00";
                                caregiverRef.child("assigned-elderly").child(firstNameElderly.trim()+yearOfBirth.trim()).setValue(true);
                                registerUser(username, firstNameElderly, lastNameElderly, email, pinCode, phoneNumber, yearOfBirth, "elderly");
                                elderlyRef.child("allergies").setValue(allergies);
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
     * @param yearOfBirthElderly Example: 1920
     * @param usernameCaregiver Username of the caregiver in the database.
     */
    public void removeElderlyFromCaregiver(String firstNameElderly, String yearOfBirthElderly, String usernameCaregiver) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly);
        DatabaseReference caregiverRef = rootRef.child("caregiver-users").child(usernameCaregiver);

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    caregiverRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot caregiverSnapshot) {
                            if (caregiverSnapshot.exists()) {
                                // Remove the elderly from the caregiver's assigned-elderly node
                                caregiverRef.child("assigned-elderly").child(firstNameElderly.trim()+yearOfBirthElderly.trim()).removeValue();
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
     * @param yearOfBirthElderly Example: 1920
     * @param time             Formatting: hh:mm
     * @param mealType         "breakfast" || "lunch" || "dinner"
     */
    public void addMealToElderly(String toEat, String firstNameElderly, String yearOfBirthElderly, String time, String mealType, boolean eaten) {
        mealAdded = false;
        if(!isMealParamFormattedCorrectly(mealType, time)) {
            Toast.makeText(context, "Not right formatting on parameters", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly.trim()+yearOfBirthElderly.trim());

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    DatabaseReference mealsRef = elderlyRef.child("meals");
                    Meal meal = new Meal(time, toEat, mealType, eaten);
                    DatabaseReference specificMealRef = mealsRef.child(mealType);

                    specificMealRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Toast.makeText(context, "Meal already added", Toast.LENGTH_SHORT).show();
                            } else {
                                mealsRef.child(mealType).setValue(meal);
                                mealAdded = true;
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
     * @param yearOfBirthElderly Example: 1920
     * @param mealType         "breakfast" || "lunch" || "dinner"
     */
    public void removeMealFromElderly(String firstNameElderly, String yearOfBirthElderly, String mealType) {
        if(!isMealParamFormattedCorrectly(mealType)) {
            Toast.makeText(context, "Not right formatting on parameters", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly.trim()+yearOfBirthElderly.trim());

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    DatabaseReference mealsRef = elderlyRef.child("meals");
                    DatabaseReference specificMealRef = mealsRef.child(mealType);

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
     * @param yearOfBirthElderly Example: 1920
     * @param time             Formatting: hh:mm
     * @param mealType         "breakfast" || "lunch" || "dinner"
     */
    public void setToEat(String toEat, String firstNameElderly, String yearOfBirthElderly, String time, String mealType, boolean eaten) {
        if(!isMealParamFormattedCorrectly(mealType, time)) {
            Toast.makeText(context, "Not right formatting on parameters", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly.trim()+yearOfBirthElderly.trim());

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    DatabaseReference mealsRef = elderlyRef.child("meals");
                    Meal meal = new Meal(time, toEat, mealType, eaten);
                    DatabaseReference specificMealRef = mealsRef.child(mealType);

                    specificMealRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                mealsRef.child(mealType).setValue(meal);
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
     * Sets "type" for a meal which belongs to an elderly in the database.
     *
     * @param toEat Example: "pizza"
     * @param firstNameElderly First name of the elderly in the database
     * @param yearOfBirthElderly Example: 1920
     * @param time             Formatting: hh:mm
     * @param mealType         "breakfast" || "lunch" || "dinner"
     * @param newMealType      ""breakfast" || "lunch" || "dinner" the new type of the meal
     */
    public void setType(String toEat, String firstNameElderly, String yearOfBirthElderly, String time, String mealType, String newMealType, boolean eaten) {
        if(!isMealParamFormattedCorrectly(mealType, time)) {
            Toast.makeText(context, "Not right formatting on parameters", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly.trim()+yearOfBirthElderly.trim());

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    DatabaseReference mealsRef = elderlyRef.child("meals");
                    Meal meal = new Meal(time, toEat, mealType, eaten);
                    DatabaseReference specificMealRef = mealsRef.child(mealType);

                    specificMealRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                addMealToElderly(toEat, firstNameElderly, yearOfBirthElderly, time, newMealType, eaten);
                                //delay so that database has time to create new meal before removing old one
                                final Handler handler = new Handler();
                                handler.postDelayed(() -> {
                                    if(mealAdded){
                                        removeMealFromElderly(firstNameElderly, yearOfBirthElderly, mealType);
                                        Toast.makeText(context, "Meal successfully edited", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(context, "Meal type already exists", Toast.LENGTH_SHORT).show();
                                    }
                                }, 500);
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
    public void registerUser(String username, String firstName, String lastName, String email, String password, String phoneNumber, String yearOfBirth, String userType) {
        String firstNameUser = firstName.trim();
        String lastNameUser = lastName.trim();
        String emailUser = email.trim();
        String passwordUser = password.trim();
        String phoneUser = phoneNumber.trim();
        String yearOfBirthUser = yearOfBirth.trim();
        String userTypeUser = userType.trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(firstName) ||
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
                    DatabaseReference caregiverRef = rootRef.child("caregiver-users").child(username);
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
    public void loginUser(String username, String email, String password, String userType) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(context, "Enter username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        String passwordUser = password.trim();
        if(userType.contains("elderly")) {
            passwordUser = passwordUser + "00";
        }

        mAuth.signInWithEmailAndPassword(email, passwordUser)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Login Successful.", Toast.LENGTH_SHORT).show();

                            Intent intent;
                            if(userType.contains("elderly")) {
                                intent = new Intent(context, ElderlyOverview.class);

                            } else {
                                intent = new Intent(context, CaregiverMainActivity.class);
                                intent.putExtra("usernameCaregiver", username.trim());
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

    public interface LoginCallback {
        void onLoginSuccess();
        void onLoginFailure();
    }

    /**
     * Login a user with LoginCallback. Used if you want to achieve something only if it's success or not within the app.
     *
     * @param email Email user
     * @param password 6-digit PIN for elderly
     * @param userType "elderly" || "caregiver"
     */
    public void loginUser(String username, String email, String password, String userType, final LoginCallback callback) {
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(context, "Enter username", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(context, "Enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        String passwordUser = password.trim();
        if (userType.contains("elderly")) {
            passwordUser = passwordUser + "00";
        }

        mAuth.signInWithEmailAndPassword(email, passwordUser)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Login Successful.", Toast.LENGTH_SHORT).show();

                             Intent intent;
                            if (userType.contains("elderly")) {
                                //Add getElderlyIdByEmail and send Id by Intent to ElderlyOverview
                                getElderlyIdByEmail(email, new ElderlyIdCallback() {
                                    @Override
                                    public void onElderlyIdFound(String elderlyId) {
                                       Intent intent = new Intent(context, ElderlyOverview.class);
                                        intent.putExtra("usernameElderly", elderlyId);
                                        context.startActivity(intent);
                                    }

                                    @Override
                                    public void onElderlyIdNotFound() {

                                    }

                                    @Override
                                    public void onError(String errorMessage) {

                                    }
                                });

                            } else {
                                intent = new Intent(context, CaregiverMainActivity.class);
                                intent.putExtra("usernameCaregiver", username.trim());
                                context.startActivity(intent);
                            }


                            if (context instanceof Activity) {
                                ((Activity) context).finish();
                            }

                            // Notify the caller that login was successful
                            callback.onLoginSuccess();
                        } else {
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();

                            // Notify the caller that login failed
                            callback.onLoginFailure();
                        }
                    }
                });
    }


    public void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email.trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Reset mail sent", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, Login.class);
                    context.startActivity(intent);

                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                } else {
                    Toast.makeText(context, "Enter a valid email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public interface MealCallback {
        void onMealsReceived(ArrayList<Meal> meals);
    }

    public void getMeals(String elderlyId, MealCallback callback) {
        DatabaseReference mealsRef = rootRef.child("elderly-users").child(elderlyId).child("meals");

        mealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot mealSnapshot) {
                ArrayList<Meal> mealList = new ArrayList<>();

                for (DataSnapshot mealTypeSnapshot : mealSnapshot.getChildren()) {
                    Meal meal = mealTypeSnapshot.getValue(Meal.class);
                    if (meal != null) {
                        mealList.add(meal);
                    }
                }

                // Pass the ArrayList to the custom callback
                callback.onMealsReceived(mealList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }


    /**
     * Checks if parameters for meal functions are correctly formatted
     *
     * @param time             Formatting: hh:mm
     * @param mealType         "breakfast" || "lunch" || "dinner"
     */
    private boolean isMealParamFormattedCorrectly(String mealType, String time) {
        boolean isRightMealTypeFormat = mealType.contains("breakfast")
                || mealType.contains("lunch") || mealType.contains("dinner") ||
                mealType.contains("snack1") || mealType.contains("snack2")
                || mealType.contains("snack3");
        boolean isRightTimeFormat = time.matches("\\d{2}:\\d{2}");

        return isRightMealTypeFormat && isRightTimeFormat;
    }

    /**
     * Checks if parameters for meal functions are correctly formatted
     *
     * @param mealType         "breakfast" || "lunch" || "dinner"
     */
    private boolean isMealParamFormattedCorrectly(String mealType) {
        return mealType.contains("breakfast")
                || mealType.contains("lunch") || mealType.contains("dinner") ||
                mealType.contains("snack1") || mealType.contains("snack2")
                || mealType.contains("snack3");
    }
}
