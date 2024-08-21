package com.example.androidfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PatientsProfile extends AppCompatActivity {

    private TextView patientName, patientEmail, selectedDoctor, statusMessage;
    private DatabaseReference usersRef, appointmentsRef;
    private String patientId, appointmentId;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_profile);

        patientName = findViewById(R.id.patientName);
        patientEmail = findViewById(R.id.patientEmail);
        selectedDoctor = findViewById(R.id.selectedDoctor);
        statusMessage = findViewById(R.id.statusMessage);
        btn = findViewById(R.id.gobacktopatient);

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");

        patientId = getIntent().getStringExtra("patientId");
        appointmentId = getIntent().getStringExtra("appointmentId");

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientsProfile.this, PatientPage.class);
                startActivity(intent);
                finish();
            }
        });

        if (patientId == null || appointmentId == null) {
            Toast.makeText(this, "No patient data available", Toast.LENGTH_SHORT).show();
            finish();  // Close the activity if no data is available
            return;
        }

        loadPatientDetails();
        loadAppointmentDetails();
    }

    private void loadPatientDetails() {
        usersRef.child(patientId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);

                if (name != null && email != null) {
                    patientName.setText("Name: " + name);
                    patientEmail.setText("Email: " + email);
                } else {
                    Toast.makeText(PatientsProfile.this, "Failed to load patient details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientsProfile.this, "Failed to load patient details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAppointmentDetails() {
        appointmentsRef.child(appointmentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String doctorName = dataSnapshot.child("doctorName").getValue(String.class);
                String status = dataSnapshot.child("status").getValue(String.class);
                String message = dataSnapshot.child("message").getValue(String.class);

                if (doctorName != null) {
                    selectedDoctor.setText("Selected Doctor: " + doctorName);
                }

                if (message != null) {
                    statusMessage.setText("Status: " + message);
                } else if (status != null) {
                    statusMessage.setText("Status: " + status);
                } else {
                    statusMessage.setText("Status: No updates yet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(PatientsProfile.this, "Failed to load appointment details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
