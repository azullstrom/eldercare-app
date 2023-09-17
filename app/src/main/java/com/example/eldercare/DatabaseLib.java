package com.example.eldercare;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.eldercare.CaregiverMainActivity;
import com.example.eldercare.Login;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DatabaseLib {
    private DatabaseReference rootRef;
    private Context context;

    public DatabaseLib(Context context) {
        this.context = context;
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Adds an existing elderly to an existing caregiver in the database.
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

    public void addMealToElderly(String firstNameElderly, String time) {
        DatabaseReference elderlyRef = rootRef.child("elderly-users").child(firstNameElderly);

        elderlyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot elderlySnapshot) {
                if (elderlySnapshot.exists()) {

                } else {
                    // If the elderly user doesn't exist
                    Toast.makeText(context, "Elderly does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any database errors here
            }
        });
    }
}
