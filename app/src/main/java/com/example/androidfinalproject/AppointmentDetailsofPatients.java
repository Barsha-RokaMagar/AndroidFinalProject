package com.example.androidfinalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AppointmentDetailsofPatients extends AppCompatActivity {

    private TextView appointmentDetailsTextView;
    private Button goBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details_ofpatients);


        appointmentDetailsTextView = findViewById(R.id.tv_appointment_details);
        goBackButton = findViewById(R.id.gobackBtn);


        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String doctorName = getIntent().getStringExtra("doctorName");
        String specialty = getIntent().getStringExtra("specialty");


        String details = "Date: " + date + "\nTime: " + time + "\nDoctor: " + doctorName + "\nSpecialty: " + specialty;
        appointmentDetailsTextView.setText(details);


        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }
}
