package com.example.androidfinalproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.Calendar;

public class PsychologistPage extends AppCompatActivity {

    private EditText dateInput, timeInput;
    private ImageButton selectDateButton, selectTimeButton;
    private Button makeAppointmentButton, goBackButton;
    private RadioGroup doctorRadioGroup;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef, appointmentRef;
    private String selectedPsychologist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psychologist_page); // Ensure layout file matches

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        appointmentRef = FirebaseDatabase.getInstance().getReference().child("appointments");

        dateInput = findViewById(R.id.et_date);
        timeInput = findViewById(R.id.et_time);
        selectDateButton = findViewById(R.id.btn_date_picker);
        selectTimeButton = findViewById(R.id.btn_time_picker);
        makeAppointmentButton = findViewById(R.id.btn_book_appointment);
        goBackButton = findViewById(R.id.btn_go_back);
        doctorRadioGroup = findViewById(R.id.rg_doctors);

        selectDateButton.setOnClickListener(v -> showDatePicker());
        selectTimeButton.setOnClickListener(v -> showTimePicker());
        makeAppointmentButton.setOnClickListener(v -> bookAppointment());
        goBackButton.setOnClickListener(v -> goBack());

        loadPsychologists();

        doctorRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = findViewById(checkedId);
            selectedPsychologist = selectedButton != null ? selectedButton.getText().toString() : null;
            Log.d("PsychologistPage", "Selected psychologist: " + selectedPsychologist);
        });
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) ->
                        dateInput.setText(selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear),
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) ->
                        timeInput.setText(String.format("%02d:%02d", selectedHour, selectedMinute)),
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }

    private void bookAppointment() {
        String date = dateInput.getText().toString().trim();
        String time = timeInput.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || selectedPsychologist == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String patientId = currentUser.getUid();

        DatabaseReference newAppointment = appointmentRef.push();
        newAppointment.child("date").setValue(date);
        newAppointment.child("time").setValue(time);
        newAppointment.child("psychologist").setValue(selectedPsychologist);
        newAppointment.child("patientId").setValue(patientId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PsychologistPage.this, "Appointment booked", Toast.LENGTH_SHORT).show();

                Log.d("PsychologistPage", "Appointment booked - Date: " + date + ", Time: " + time + ", Psychologist: " + selectedPsychologist);

                Intent intent = new Intent(PsychologistPage.this, AppointmentDetailsofPatients.class);
                intent.putExtra("date", date);
                intent.putExtra("time", time);
                intent.putExtra("psychologistName", selectedPsychologist); // Updated to psychologist
                intent.putExtra("specialty", "Psychologist"); // Updated to psychologist
                startActivity(intent);
            } else {
                Log.e("PsychologistPage", "Failed to book appointment", task.getException());
                Toast.makeText(PsychologistPage.this, "Failed to book appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goBack() {
        Intent intent = new Intent(PsychologistPage.this, PatientPage.class);
        startActivity(intent);
        finish();
    }

    private void loadPsychologists() {
        usersRef.orderByChild("specialty").equalTo("Psychologist").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("PsychologistPage", "Data snapshot received: " + dataSnapshot.toString());
                doctorRadioGroup.removeAllViews();

                if (!dataSnapshot.exists()) {
                    Log.d("PsychologistPage", "No psychologists found.");
                    Toast.makeText(PsychologistPage.this, "No psychologists available", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    if (name != null) {
                        RadioButton radioButton = new RadioButton(PsychologistPage.this);
                        radioButton.setText(name);
                        doctorRadioGroup.addView(radioButton);
                        Log.d("PsychologistPage", "Added psychologist: " + name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PsychologistPage", "Failed to load psychologists", databaseError.toException());
                Toast.makeText(PsychologistPage.this, "Failed to load psychologists", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
