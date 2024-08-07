package com.example.androidfinalproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


public class PatientPage extends AppCompatActivity {
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_page);

        LinearLayout cardiologistLayout = findViewById(R.id.cardiologistLayout);
        LinearLayout dentistLayout = findViewById(R.id.dentistLayout);
        LinearLayout psychologistLayout = findViewById(R.id.psychologistLayout);
        LinearLayout dermatologistLayout = findViewById(R.id.dermatologistLayout);
        btn = findViewById(R.id.logoutBtn);


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

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PatientPage.this, Loginpage.class);
                startActivity(intent);


            }
        });



    }
}