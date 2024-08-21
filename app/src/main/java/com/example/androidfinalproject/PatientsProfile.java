package com.example.androidfinalproject;

import android.os.Bundle;
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

    private TextView statusMessage, patientName, patientEmail, selectedDoctor;
    private DatabaseReference patientProfileRef;
    private String patientId, appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_profile);

        statusMessage = findViewById(R.id.statusMessage);
        patientName = findViewById(R.id.patientName);
        patientEmail = findViewById(R.id.patientEmail);
        selectedDoctor = findViewById(R.id.selectedDoctor);

        // Retrieve patientId and appointmentId from Intent
        patientId = getIntent().getStringExtra("patientId");
        appointmentId = getIntent().getStringExtra("appointmentId");

        if (patientId == null || appointmentId == null) {
            Toast.makeText(this, "No patient or appointment data available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize Firebase reference
        patientProfileRef = FirebaseDatabase.getInstance().getReference().child("patients").child(patientId).child("appointments").child(appointmentId);

        // Load data
        loadPatientProfile();
    }

    private void loadPatientProfile() {
        patientProfileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String message = dataSnapshot.child("statusMessage").getValue(String.class);
                String name = dataSnapshot.child("name").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String doctor = dataSnapshot.child("doctorName").getValue(String.class);

                if (message != null) {
                    statusMessage.setText(message);
                }
                if (name != null) {
                    patientName.setText("Name: " + name);
                }
                if (email != null) {
                    patientEmail.setText("Email: " + email);
                }
                if (doctor != null) {
                    selectedDoctor.setText("Doctor: " + doctor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientsProfile.this, "Failed to load profile data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
