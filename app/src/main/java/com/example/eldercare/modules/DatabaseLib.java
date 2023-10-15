package com.example.eldercare.modules;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eldercare.caregiver_view.CaregiverMainActivity;
import com.example.eldercare.elderly_view.ElderlyOverview;
import com.example.eldercare.account_view.Login;
import com.example.eldercare.R;
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

    /***********************************************************************************/
    /**************************************INTERFACES***********************************/
    /***********************************************************************************/

    public interface SuccessCallback {
        void onSuccess();
        void onFailure(String errorMessage);
    }

    public interface StringCallback {
        void onFound(String str);

        void onNotFound();

        void onError(String errorMessage);
    }

    public interface ListCallback {
        void onFound(List<String> list);

        void onNotFound();

        void onError(String errorMessage);
    }

    public interface ArrayListStringCallback{
        void onFound(ArrayList<String> list);
    }

    public interface ArrayListMealCallback{
        void onFound(ArrayList<Meal> list);
    }

    /***********************************************************************************/
    /********************************PUBLIC FUNCTIONS***********************************/
    /***********************************************************************************/

    /************************************CHAPTER 1**************************************/
    /***********************************************************************************/
    /********************************* GET Functions ***********************************/
    /***********************************************************************************/

    /**
     * Fetches all data for an elderly. This snapshot can be sent into convertSnapshotIntoJson function.
     *
     * @param firstNameElderly First name of the elderly in the database.
     * @param yearOfBirth Example: 1920
     * @param callback Async. Add new ValueEventListener() {} and follow the automated functions.
     */
    public void getElderlyAllergiesDataSnapshot(String firstNameElderly, String yearOfBirth, ValueEventListener callback) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly.trim()+yearOfBirth.trim()).child("allergies");
        attachSingleValueEventListener(elderlyRef, callback);
    }

    /**
     * Fetches all data for an elderly. This snapshot can be sent into convertSnapshotIntoJson function.
     *
     * @param usernameCaregiver Username of the caregiver.
     * @param callback Async. Add new ValueEventListener() {} and follow the automated functions.
     */
    public void getAssignedElderlyDataSnapshot(String usernameCaregiver, ValueEventListener callback) {
        DatabaseReference assignedRef = rootRef.child("caregiver-users").child(usernameCaregiver.trim()).child("assigned-elderly");
        attachSingleValueEventListener(assignedRef, callback);
    }

    /**
     * Get caregiver email by username.
     */
    public void getCaregiverEmailByUsername(String username, ValueEventListener callback) {
        DatabaseReference emailRef = rootRef.child("caregiver-users").child(username.trim()).child("email");
        attachSingleValueEventListener(emailRef, callback);
    }

    /**
     * Get firebaseMessaging token for caregiver
     * @param username Caregiver username
     * @param callback callback object
     */
    public void getCaregiverToken(String username, ValueEventListener callback) {
        DatabaseReference emailRef = rootRef.child("caregiver-users").child(username.trim()).child("token");
        attachSingleValueEventListener(emailRef, callback);
    }

    /**
     * Get elderly last name by elderly-id
     */
    public void getElderlyLastName(String elderlyId, ValueEventListener callback){
        DatabaseReference lastNameRef = rootRef.child("elderly-users").child(elderlyId).child("lastname");
        attachSingleValueEventListener(lastNameRef, callback);
    }

    /**
     * Get elderly-id by email.
     */
    public void getElderlyIdByEmail(final String email, final StringCallback callback) {
        DatabaseReference databaseReference = rootRef.child("elderly-users");
        Query query = databaseReference.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String elderlyId = snapshot.getKey();
                        callback.onFound(elderlyId);
                        break;
                    }
                } else {
                    callback.onNotFound();
                }
            }

           @Override
           public void onCancelled(DatabaseError databaseError) {
                // Handle any errors here
                callback.onError(databaseError.getMessage());
            }
        });
    }

    /**
     * Returns a list of caregiver usernames with given assigned elderly.
     */
    public void getCaregiverUsernamesByElderlyId(String elderlyId, ListCallback callback) {
        DatabaseReference caregiversRef = rootRef.child("caregiver-users");
        final List<String> caregiverUsernames = new ArrayList<>();

        caregiversRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot caregiverSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot assignedElderlyNode = caregiverSnapshot.child("assigned-elderly");
                    if (assignedElderlyNode.child(elderlyId).exists()) {
                        caregiverUsernames.add(caregiverSnapshot.getKey());
                    }
                }

                if (!caregiverUsernames.isEmpty()) {
                    callback.onFound(caregiverUsernames);
                } else {
                    callback.onNotFound();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    /************************************CHAPTER 2**************************************/
    /***********************************************************************************/
    /****************************** Utility Functions **********************************/
    /***********************************************************************************/

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

    private void attachSingleValueEventListener(DatabaseReference ref, ValueEventListener callback) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                callback.onDataChange(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onCancelled(databaseError);
            }
        });
    }

    /************************************CHAPTER 3**************************************/
    /***********************************************************************************/
    /****************************** Assign Functions ***********************************/
    /***********************************************************************************/

    /**
     * Adds an existing elderly to an existing caregiver in the database.
     *
     * @param elderlyId Dag1930 example
     * @param usernameCaregiver Username of the caregiver in the database.
     */
    public void assignElderlyToCaregiver(String elderlyId, String usernameCaregiver, SuccessCallback callback) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(elderlyId.trim());
        DatabaseReference caregiverRef = rootRef.child("caregiver-users").child(usernameCaregiver);

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    caregiverRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot caregiverSnapshot) {
                            if (caregiverSnapshot.exists()) {
                                caregiverRef.child("assigned-elderly").child(elderlyId.trim()).setValue(true);
                                callback.onSuccess();
                            } else {
                                Toast.makeText(context, "Enter valid caregiver.", Toast.LENGTH_SHORT).show();
                                callback.onFailure("Enter valid caregiver");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any database errors here
                        }
                    });
                } else {
                    Toast.makeText(context, "Invalid elder ID. Try again", Toast.LENGTH_SHORT).show();
                    callback.onFailure("Invalid elder ID. Try again");
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
     * @param callback
     */
    public void assignAndCreateNewElderlyToCaregiver(String firstNameElderly, String lastNameElderly, String usernameCaregiver, String username, String pin, String phoneNumber, String yearOfBirth, SuccessCallback callback) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly.trim()+yearOfBirth.trim());
        DatabaseReference caregiverRef = rootRef.child("caregiver-users").child(usernameCaregiver);

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    Toast.makeText(context, "Elderly already exists", Toast.LENGTH_SHORT).show();
                    callback.onFailure("Elderly already exists");
                } else {
                    caregiverRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot caregiverSnapshot) {
                            if (caregiverSnapshot.exists()) {
                                String email = username.trim() + "@elderly.eldercare.com";
                                String pinCode = pin.trim() + "00";
                                caregiverRef.child("assigned-elderly").child(firstNameElderly.trim()+yearOfBirth.trim()).setValue(true);

                                registerUser(username, firstNameElderly, lastNameElderly, email, pinCode, phoneNumber, yearOfBirth, "elderly", "EMPTY-TOKEN", new SuccessCallback() {
                                    @Override
                                    public void onSuccess() {
                                        Toast.makeText(context, "Successfully added!", Toast.LENGTH_SHORT).show();
                                        callback.onSuccess();
                                    }

                                    @Override
                                    public void onFailure(String error) {
                                        callback.onFailure("Error");
                                    }
                                });
                            } else {
                                Toast.makeText(context, "Enter valid caregiver.", Toast.LENGTH_SHORT).show();
                                callback.onFailure("Enter valid caregiver");
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

    /************************************CHAPTER 4**************************************/
    /***********************************************************************************/
    /****************************** Remove Functions ***********************************/
    /***********************************************************************************/

    /**
     * Removes an existing elderly from an existing caregiver in the database.
     *
     * @param elderlyId Dag1930 example
     * @param usernameCaregiver Username of the caregiver in the database.
     */
    public void removeElderlyFromCaregiver(String elderlyId, String usernameCaregiver, SuccessCallback callback) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(elderlyId.trim());
        DatabaseReference caregiverRef = rootRef.child("caregiver-users").child(usernameCaregiver);

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {
                    caregiverRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot caregiverSnapshot) {
                            if (caregiverSnapshot.exists()) {
                                caregiverRef.child("assigned-elderly").child(elderlyId.trim()).removeValue()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                callback.onSuccess();
                                            } else {
                                                callback.onFailure("Error removing elderly.");
                                            }
                                        });
                            } else {
                                callback.onFailure("Enter valid caregiver.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            callback.onFailure("Database error: " + databaseError.getMessage());
                        }
                    });
                } else {
                    callback.onFailure("Enter valid elderly.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any database errors here
                callback.onFailure("Database error: " + databaseError.getMessage());
            }
        });
    }


    /************************************CHAPTER 5**************************************/
    /***********************************************************************************/
    /******************************** Meal Functions ***********************************/
    /***********************************************************************************/

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
                    Toast.makeText(context, "Elderly does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
                    Toast.makeText(context, "Elderly does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
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
     * Sets meal eaten true/false in database.
     *
     * @param firstNameElderly
     * @param yearOfBirthElderly
     * @param mealType
     * @param setEaten Set meal eaten to either true or false
     */
    public void setMealEaten(String firstNameElderly, String yearOfBirthElderly, String mealType, boolean setEaten) {
        if(!isMealParamFormattedCorrectly(mealType)) {
            Toast.makeText(context, "Not right formatting on parameters", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference mealEatenRef = rootRef.child("elderly-users")
                .child(firstNameElderly.trim()+yearOfBirthElderly.trim())
                .child("meals").child(mealType).child("eaten");

        mealEatenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().setValue(setEaten);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    /************************************CHAPTER 6**************************************/
    /***********************************************************************************/
    /******************************** Allergy Functions ********************************/
    /***********************************************************************************/

    public void addAllergyToElderly(String allergy, String firstNameElderly, String yearOfBirthElderly) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly.trim() + yearOfBirthElderly.trim());
        DatabaseReference allergiesRef = elderlyRef.child("allergies");

        DatabaseReference newAllergyRef = allergiesRef.push();
        newAllergyRef.setValue(allergy, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    // Allergy added successfully
                } else {
                    // An error occurred
                }
            }
        });
    }

    public void removeAllergyFromElderly(String allergy, String firstNameElderly, String yearOfBirthElderly, SuccessCallback callback) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly.trim() + yearOfBirthElderly.trim());
        DatabaseReference allergiesRef = elderlyRef.child("allergies");

        Query query = allergiesRef.orderByValue().equalTo(allergy);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    childSnapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error == null) {
                                callback.onSuccess();
                            } else {
                                callback.onFailure(error.getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    /************************************CHAPTER 7**************************************/
    /***********************************************************************************/
    /***************************** Notification Functions ******************************/
    /***********************************************************************************/

    public void addNotificationHistoryElderly(String firstNameElderly, String yearOfBirthElderly, String notificationTitle, String notificationText, String dateAndTime) {
        DatabaseReference notificationRef = rootRef.child("elderly-users").child(firstNameElderly.trim()+yearOfBirthElderly.trim()).child("notification-history");
        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().child(dateAndTime).child("title").setValue(notificationTitle);
                snapshot.getRef().child(dateAndTime).child("text").setValue(notificationText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void addMealHistoryElderly(String firstNameElderly, String yearOfBirthElderly, Meal meal, String dateAndTime) {
        DatabaseReference mealRef = rootRef.child("elderly-users").child(firstNameElderly.trim()+yearOfBirthElderly.trim()).child("meal-history");

        mealRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getRef().child(dateAndTime).child("meal-type").setValue(meal.getMealType());
                snapshot.getRef().child(dateAndTime).child("meal-toEat").setValue(meal.getToEat());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getNotificationHistoryElderly(String firstNameElderly, String yearOfBirthElderly, ArrayListStringCallback callback){
        DatabaseReference notificationRef = rootRef.child("elderly-users")
                .child(firstNameElderly.trim()+yearOfBirthElderly.trim())
                .child("notification-history");
        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> notificationList = new ArrayList<>();
                for (DataSnapshot notificationSnapshot : snapshot.getChildren()) {
                    notificationList.add(notificationSnapshot.getKey());
                    notificationList.add(notificationSnapshot.child("title").getValue(String.class));
                    notificationList.add(notificationSnapshot.child("text").getValue(String.class));
                }

                callback.onFound(notificationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getMealHistoryElderly(String firstNameElderly, String yearOfBirthElderly, ArrayListStringCallback callback){
        DatabaseReference notificationRef = rootRef.child("elderly-users")
                .child(firstNameElderly.trim()+yearOfBirthElderly.trim())
                .child("meal-history");
        notificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> notificationList = new ArrayList<>();
                for (DataSnapshot notificationSnapshot : snapshot.getChildren()) {
                    notificationList.add(notificationSnapshot.getKey());
                    notificationList.add(notificationSnapshot.child("meal-toEat").getValue(String.class));
                    notificationList.add(notificationSnapshot.child("meal-type").getValue(String.class));
                }

                callback.onFound(notificationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context, "Elderly does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getMeals(String elderlyId, ArrayListMealCallback callback) {
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

                callback.onFound(mealList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle the error
            }
        });
    }

    /************************************CHAPTER 8**************************************/
    /***********************************************************************************/
    /******************************** Account Functions ********************************/
    /***********************************************************************************/

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
     * @param token fireBaseMessagingToken
     */
    public void registerUser(String username, String firstName, String lastName, String email, String password, String phoneNumber, String yearOfBirth, String userType, String token, SuccessCallback callback) {
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

        mAuth.createUserWithEmailAndPassword(emailUser, passwordUser)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
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
                    userReference.child("token").setValue(token);

                    Toast.makeText(context, "Registration Successful.", Toast.LENGTH_SHORT).show();
                    callback.onSuccess();
                } else {
                    Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show();
                    callback.onFailure("Registration failed");
                }
            }
        });
    }

    /**
     * Login a user with LoginCallback. Used if you want to achieve something only if it's success or not within the app.
     *
     * @param email Email user
     * @param password 6-digit PIN for elderly
     * @param userType "elderly" || "caregiver"
     */
    public void loginUser(String username, String email, String password, String userType, final SuccessCallback callback) {
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
                                getElderlyIdByEmail(email, new StringCallback() {
                                    @Override
                                    public void onFound(String elderlyId) {
                                        Intent intent = new Intent(context, ElderlyOverview.class);
                                        intent.putExtra("usernameElderly", elderlyId);
                                        startActivityAndFinishCurrent(intent);
                                    }

                                    @Override
                                    public void onNotFound() {

                                    }

                                    @Override
                                    public void onError(String errorMessage) {

                                    }
                                });

                            } else {
                                intent = new Intent(context, CaregiverMainActivity.class);
                                intent.putExtra("usernameCaregiver", username.trim());
                                startActivityAndFinishCurrent(intent);
                            }
                            callback.onSuccess();
                        } else {
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();

                            // Notify the caller that login failed
                            callback.onFailure("Error");
                        }
                    }
                });
    }

    /***********************************************************************************/
    /***********************************************************************************/
    /***********************************************************************************/

    public void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email.trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Reset mail sent", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, Login.class);
                    startActivityAndFinishCurrent(intent);
                } else {
                    Toast.makeText(context, "Enter a valid email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    /***********************************************************************************/
    /****************************PRIVATE FUNCTIONS**************************************/
    /***********************************************************************************/


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

    private void startActivityAndFinishCurrent(Intent intent) {
        if (intent != null) {
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        }
    }

    /***********************************************************************************/
    /***********************************************************************************/
    /***********************************************************************************/
}
