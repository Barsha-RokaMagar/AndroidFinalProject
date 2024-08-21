package com.example.androidfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

public class PatientsProfile extends AppCompatActivity {

    private TextView patientName, patientEmail, selectedDoctor, statusMessage;
    private DatabaseReference patientsRef, appointmentsRef;
    private String patientId, appointmentId;
    private Button goBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_profile);

        // Initialize UI components
        patientName = findViewById(R.id.patientName);
        patientEmail = findViewById(R.id.patientEmail);
        selectedDoctor = findViewById(R.id.selectedDoctor);
        statusMessage = findViewById(R.id.statusMessage);
        goBackButton = findViewById(R.id.gobacktopatient);

        // Initialize Firebase database references
        patientsRef = FirebaseDatabase.getInstance().getReference().child("patients");
        appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");

        // Retrieve patientId and appointmentId from Intent
        Intent intent = getIntent();
        patientId = intent.getStringExtra("patientId");
        appointmentId = intent.getStringExtra("appointmentId");

        Log.d("PatientsProfile", "Patient ID: " + patientId);
        Log.d("PatientsProfile", "Appointment ID: " + appointmentId);

        // Ensure IDs are valid before loading data
        if (patientId != null && appointmentId != null) {
            loadPatientProfile();
            loadAppointmentDetails();
        } else {
            Toast.makeText(PatientsProfile.this, "No patient or appointment data available", Toast.LENGTH_SHORT).show();
        }

        // Set up button click listener
        goBackButton.setOnClickListener(v -> finish());
    }

    private void loadPatientProfile() {
        Log.d("PatientsProfile", "Loading patient profile for ID: " + patientId);

        patientsRef.child(patientId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("PatientsProfile", "Patient profile data retrieved");

                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);

                    if (name != null && email != null) {
                        patientName.setText("Name: " + name);
                        patientEmail.setText("Email: " + email);
                    } else {
                        Toast.makeText(PatientsProfile.this, "Patient details are missing", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PatientsProfile.this, "No data available for this patient ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PatientsProfile", "Failed to load patient details", databaseError.toException());
                Toast.makeText(PatientsProfile.this, "Failed to load patient details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAppointmentDetails() {
        Log.d("PatientsProfile", "Loading appointment details for ID: " + appointmentId);

        appointmentsRef.child(appointmentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("PatientsProfile", "Appointment details data retrieved");

                if (dataSnapshot.exists()) {
                    String doctorName = dataSnapshot.child("doctorName").getValue(String.class); // Adjust field name if necessary
                    String status = dataSnapshot.child("status").getValue(String.class);
                    String confirmationMessage = dataSnapshot.child("confirmationMessage").getValue(String.class);

                    if (doctorName != null && status != null && confirmationMessage != null) {
                        selectedDoctor.setText("Selected Doctor: " + doctorName);
                        statusMessage.setText("Status: " + status);
                        statusMessage.append("\nMessage: " + confirmationMessage);
                    } else {
                        Toast.makeText(PatientsProfile.this, "Appointment details are incomplete", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PatientsProfile.this, "No data available for this appointment ID", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PatientsProfile", "Failed to load appointment details", databaseError.toException());
                Toast.makeText(PatientsProfile.this, "Failed to load appointment details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
