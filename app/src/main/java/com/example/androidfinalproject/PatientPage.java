package com.example.androidfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;



public class PatientPage extends AppCompatActivity {
    Button logout, viewprofile;
    String patientId = "";
    String appointmentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_page);

        LinearLayout cardiologistLayout = findViewById(R.id.cardiologistLayout);
        LinearLayout dentistLayout = findViewById(R.id.dentistLayout);
        LinearLayout psychologistLayout = findViewById(R.id.psychologistLayout);
        LinearLayout dermatologistLayout = findViewById(R.id.dermatologistLayout);
        LinearLayout pediatricianLayout = findViewById(R.id.pediatricianLayout);
        LinearLayout neurologistLayout = findViewById(R.id.neurologistLayout);
        LinearLayout opthalmologistLayout = findViewById(R.id.opthalmologistLayout);
        LinearLayout gynecologistLayout = findViewById(R.id.gynecologistLayout);
        logout = findViewById(R.id.logoutBtn);
        viewprofile = findViewById(R.id.viewprofile);


        cardiologistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientPage.this, CardiologistPage.class);
                startActivity(intent);
            }
        });
        dentistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientPage.this, DentistPage.class);
                startActivity(intent);
            }
        });
        psychologistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientPage.this, PsychologistPage.class);
                startActivity(intent);
            }
        });
        dermatologistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientPage.this, DermatologistPage.class);
                startActivity(intent);
            }
        });
        pediatricianLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientPage.this, PediatricianPage.class);
                startActivity(intent);
            }
        });
        neurologistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientPage.this, NeurologistPage.class);
                startActivity(intent);
            }
        });
        opthalmologistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientPage.this, OpthalmologistPage.class);
                startActivity(intent);
            }
        });
        gynecologistLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientPage.this, GynecologistPage.class);
                startActivity(intent);
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientPage.this, Loginpage.class);
                startActivity(intent);


            }
        });

        viewprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("PatientPage", "View Profile button clicked");

                Intent intent = new Intent(PatientPage.this, PatientsProfile.class);
                intent.putExtra("patientId", patientId);
                intent.putExtra("appointmentId", appointmentId);
                startActivity(intent);
            }
        });




    }
}