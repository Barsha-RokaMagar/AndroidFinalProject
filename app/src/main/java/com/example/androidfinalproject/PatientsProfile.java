package com.example.androidfinalproject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private TextView txtpatientname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_profile);


        appointmentsListView = findViewById(R.id.appointmentsListView);
        goBackButton = findViewById(R.id.gobacktopatient);
        txtpatientname = findViewById(R.id.txtpatientname);


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            patientId = currentUser.getUid();
        } else {
            Toast.makeText(this, "No patient is logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        appointmentList = new ArrayList<>();
        appointmentAdapter = new AppointmentAdapter(this, R.layout.appointment_patient_list, appointmentList);
        appointmentsListView.setAdapter(appointmentAdapter);
        loadPatientDetails();
        loadAppointmentDetails();

        goBackButton.setOnClickListener(v -> finish());
    }
    private void loadPatientDetails() {
        FirebaseDatabase.getInstance().getReference().child("users").child(patientId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            txtpatientname.setText(dataSnapshot.child("name").getValue(String.class).toString());
                        } else {
                            Toast.makeText(PatientsProfile.this, "Patient not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("PatientsProfile", "Failed to load patient details", databaseError.toException());
                        Toast.makeText(PatientsProfile.this, "Failed to load patient details", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void loadAppointmentDetails() {
//        patientId = "x3OgdFKSFfUFq2pCqlhGU3vt7ZR2";
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
