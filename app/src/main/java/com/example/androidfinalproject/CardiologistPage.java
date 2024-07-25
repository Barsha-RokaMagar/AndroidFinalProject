package com.example.androidfinalproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;

public class CardiologistPage extends AppCompatActivity {

    private EditText dateInput, timeInput;
    private Button selectDateButton, makeAppointmentButton;
    private RadioGroup doctorRadioGroup;

    private FirebaseAuth mAuth;
    private DatabaseReference cardiologistRef, appointmentRef;
    private String selectedDate;
    private String selectedCardiologist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardiologist_page);

        mAuth = FirebaseAuth.getInstance();
        cardiologistRef = FirebaseDatabase.getInstance().getReference().child("cardiologists");
        appointmentRef = FirebaseDatabase.getInstance().getReference().child("appointments");

        // Initialize views
        dateInput = findViewById(R.id.et_date);
        timeInput = findViewById(R.id.et_time);
        selectDateButton = findViewById(R.id.btn_date_picker);
        makeAppointmentButton = findViewById(R.id.btn_book_appointment);
        doctorRadioGroup = findViewById(R.id.rg_doctors);

        // Set click listeners
        selectDateButton.setOnClickListener(v -> showDatePicker());
        makeAppointmentButton.setOnClickListener(v -> bookAppointment());

        // Load cardiologists
        loadCardiologists();

        // Set item click listener for RadioGroup
        doctorRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedButton = findViewById(checkedId);
            selectedCardiologist = selectedButton.getText().toString();
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

    private void bookAppointment() {
        String date = dateInput.getText().toString().trim();
        String time = timeInput.getText().toString().trim();

        if (date.isEmpty() || time.isEmpty() || selectedCardiologist == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String patientId = mAuth.getCurrentUser().getUid();
        DatabaseReference newAppointment = appointmentRef.push();
        newAppointment.child("date").setValue(date);
        newAppointment.child("time").setValue(time);
        newAppointment.child("cardiologist").setValue(selectedCardiologist);
        newAppointment.child("patientId").setValue(patientId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CardiologistPage.this, "Appointment booked", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CardiologistPage.this, "Failed to book appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCardiologists() {
        cardiologistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorRadioGroup.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    RadioButton radioButton = new RadioButton(CardiologistPage.this);
                    radioButton.setText(name);
                    doctorRadioGroup.addView(radioButton);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CardiologistPage.this, "Failed to load cardiologists", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
