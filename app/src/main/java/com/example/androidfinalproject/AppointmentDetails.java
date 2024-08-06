package com.example.androidfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AppointmentDetails extends AppCompatActivity {

    private TextView appointmentDetailsTextView;
    private Button goBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        // Initialize views
        appointmentDetailsTextView = findViewById(R.id.tv_appointment_details);
        goBackButton = findViewById(R.id.gobackBtn);

        // Retrieve appointment details from intent
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String doctorName = getIntent().getStringExtra("doctorName");
        String specialty = getIntent().getStringExtra("specialty");

        // Display the appointment details
        String details = "Date: " + date + "\nTime: " + time + "\nDoctor: " + doctorName + "\nSpecialty: " + specialty;
        appointmentDetailsTextView.setText(details);

        // Set up the Go Back button
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to the previous activity
                finish();
            }
        });
    }
}
