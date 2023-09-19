package com.example.eldercare;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseLib {
    private DatabaseReference rootRef;
    private Context context;

    /**
     * Constructor
     *
     * @param context Just write the class you're in: <code><b>this</b></code> should be fine.
     */
    public DatabaseLib(Context context) {
        this.context = context;
        rootRef = FirebaseDatabase.getInstance().getReference();
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
