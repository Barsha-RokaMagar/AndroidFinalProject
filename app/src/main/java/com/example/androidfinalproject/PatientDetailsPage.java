package com.example.androidfinalproject;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

public class PatientDetailsPage extends AppCompatActivity {

    private static final String TAG = "PatientDetailActivity";
    private static final String CHANNEL_ID = "default_channel_id";
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 1001;

    private TextView patientName, patientEmail, appointmentDate, appointmentTime;
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
        appointmentDate = findViewById(R.id.appointmentDate);
        appointmentTime = findViewById(R.id.appointmentTime);


        patientId = getIntent().getStringExtra("patientId");
        appointmentId = getIntent().getStringExtra("appointmentId");

        if (patientId == null || appointmentId == null) {
            Toast.makeText(this, "No patient data available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPatientDetails();
        loadAppointmentDetails();

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

    private void loadAppointmentDetails() {
        appointmentRef.child(appointmentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String date = dataSnapshot.child("date").getValue(String.class);
                String time = dataSnapshot.child("time").getValue(String.class);

                if (date != null && time != null) {
                    appointmentDate.setText("Appointment Date: " + date);
                    appointmentTime.setText("Appointment Time: " + time);
                } else {
                    Toast.makeText(PatientDetailsPage.this, "Failed to load appointment details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load appointment details", databaseError.toException());
                Toast.makeText(PatientDetailsPage.this, "Failed to load appointment details", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void confirmAppointment() {
        appointmentRef.child(appointmentId).child("status").setValue("Confirmed").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updatePatientProfile("Appointment Confirmed", "Your appointment has been confirmed.");
                Toast.makeText(PatientDetailsPage.this, "Appointment confirmed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PatientDetailsPage.this, "Failed to confirm appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelAppointment() {
        appointmentRef.child(appointmentId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updatePatientProfile("Appointment Cancelled", "Your appointment has been cancelled.");
                Toast.makeText(PatientDetailsPage.this, "Appointment cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PatientDetailsPage.this, "Failed to cancel appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePatientProfile(String status, String message) {

        DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference().child("appointments").child(appointmentId);


        appointmentRef.child("status").setValue(status);
        appointmentRef.child("confirmationMessage").setValue(message);


        DatabaseReference patientProfileRef = FirebaseDatabase.getInstance().getReference().child("patients").child(patientId).child("appointments").child(appointmentId);
        patientProfileRef.child("status").setValue(status);
        patientProfileRef.child("confirmationMessage").setValue(message);
    }
}