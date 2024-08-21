package com.example.androidfinalproject;

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

    private TextView patientName, selectedDoctor, appointmentDate, appointmentTime, status;
    private Button goBackButton;
    private DatabaseReference appointmentRef;
    private String patientId, appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_profile);

        // Initialize views
        patientName = findViewById(R.id.patientName);
        selectedDoctor = findViewById(R.id.selectedDoctor);
        appointmentDate = findViewById(R.id.appointmentDate);
        appointmentTime = findViewById(R.id.appointmentTime);
        status = findViewById(R.id.status);
        goBackButton = findViewById(R.id.gobacktopatient);

        // Get patient and appointment IDs from the intent
        patientId = getIntent().getStringExtra("patientId");
        appointmentId = getIntent().getStringExtra("appointmentId");

        if (patientId == null || appointmentId == null) {
            Toast.makeText(this, "No appointment data available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        loadAppointmentDetails();

        goBackButton.setOnClickListener(v -> finish());
    }

    private void loadAppointmentDetails() {
        FirebaseDatabase.getInstance().getReference().child("appointments").child(appointmentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Access the appointments node
                //    DataSnapshot appointmentsSnapshot = dataSnapshot.child("appointments");

                    // Iterate over each appointment
                    for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                        String doctorName = appointmentSnapshot.child("cardiologist").getValue(String.class);
                        String date = appointmentSnapshot.child("date").getValue(String.class);
                        String time = appointmentSnapshot.child("time").getValue(String.class);
                        String appointmentStatus = appointmentSnapshot.child("status").getValue(String.class);

                        patientName.setText("Name: " + getIntent().getStringExtra("patientName")); // Assuming patientName is passed from previous activity
                        selectedDoctor.setText("Doctor: " + doctorName);
                        appointmentDate.setText("Date: " + date);
                        appointmentTime.setText("Time: " + time);
                        status.setText("Status: " + (appointmentStatus != null ? appointmentStatus : "Not Available"));
                    }
                } else {
                    Toast.makeText(PatientsProfile.this, "No appointment details found", Toast.LENGTH_SHORT).show();
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
