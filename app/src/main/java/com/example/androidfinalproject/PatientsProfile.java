package com.example.androidfinalproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PatientsProfile extends AppCompatActivity {

    private ListView appointmentsListView;
    private Button goBackButton;
    private DatabaseReference appointmentRef;
    private String patientId;
    private AppointmentAdapter appointmentAdapter;
    private List<Appointment> appointmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_profile);


        appointmentsListView = findViewById(R.id.appointmentsListView);
        goBackButton = findViewById(R.id.gobacktopatient);


        patientId = getIntent().getStringExtra("patientId");

        if (patientId == null) {
            Toast.makeText(this, "No patient data available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(this, R.layout.appointment_patient_list, appointmentList);
        appointmentsListView.setAdapter(appointmentAdapter);

        loadAppointmentDetails();

        goBackButton.setOnClickListener(v -> finish());
    }

    private void loadAppointmentDetails() {
        patientId = "x3OgdFKSFfUFq2pCqlhGU3vt7ZR2";
        Log.d("patint","patient" + patientId);

        FirebaseDatabase.getInstance().getReference().child("appointments")
                .orderByChild("patientId").equalTo(patientId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        appointmentList.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                                Appointment appointment = appointmentSnapshot.getValue(Appointment.class);
                                appointment.setDoctorName(appointmentSnapshot.child("cardiologist").getValue(String.class));
                                appointmentList.add(appointment);
                            }
                            appointmentAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(PatientsProfile.this, "No appointments found", Toast.LENGTH_SHORT).show();
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
