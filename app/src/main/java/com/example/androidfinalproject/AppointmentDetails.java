package com.example.androidfinalproject;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AppointmentDetails extends AppCompatActivity {

    private TextView appointmentDetailsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);

        appointmentDetailsTextView = findViewById(R.id.tv_appointment_details);

        // Retrieve appointment details from intent
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String cardiologist = getIntent().getStringExtra("cardiologist");

        // Display the appointment details
        String details = "Date: " + date + "\nTime: " + time + "\nCardiologist: " + cardiologist;
        appointmentDetailsTextView.setText(details);
    }
}
