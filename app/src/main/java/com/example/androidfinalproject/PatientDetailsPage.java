package com.example.androidfinalproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class PatientDetailsPage extends AppCompatActivity {

    private static final String TAG = "PatientDetailActivity";

    private TextView patientName, patientEmail;
    private Button confirmButton, cancelButton, gobackButton;

    private DatabaseReference usersRef, appointmentRef;
    private String patientId, appointmentId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details_page);


        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        appointmentRef = FirebaseDatabase.getInstance().getReference().child("appointments");


        patientName = findViewById(R.id.patientName);
        patientEmail = findViewById(R.id.patientEmail);
        confirmButton = findViewById(R.id.confirmButton);
        cancelButton = findViewById(R.id.cancelButton);
        gobackButton = findViewById(R.id.gobackButton);


        patientId = getIntent().getStringExtra("patientId");
        appointmentId = getIntent().getStringExtra("appointmentId");

        if (patientId == null || appointmentId == null) {
            Toast.makeText(this, "No patient data available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        loadPatientDetails();


        confirmButton.setOnClickListener(v -> confirmAppointment());
        cancelButton.setOnClickListener(v -> cancelAppointment());
        gobackButton.setOnClickListener(v -> finish());
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
                    Toast.makeText(PatientDetailsPage.this, "Failed to load patient details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load patient details", databaseError.toException());
                Toast.makeText(PatientDetailsPage.this, "Failed to load patient details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmAppointment() {
        appointmentRef.child(appointmentId).child("status").setValue("Confirmed").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PatientDetailsPage.this, "Appointment confirmed", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(PatientDetailsPage.this, "Failed to confirm appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelAppointment() {
        appointmentRef.child(appointmentId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PatientDetailsPage.this, "Appointment cancelled", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(PatientDetailsPage.this, "Failed to cancel appointment", Toast.LENGTH_SHORT).show();
            }
        });




    }
}
