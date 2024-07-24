package com.example.androidfinalproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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

public class DoctorsPage extends AppCompatActivity {

    private TextView clinicName, welcomeText, currentAvailability;
    private EditText dateInput, startTimeInput, endTimeInput;
    private ImageButton datePickerButton, startTimePickerButton, endTimePickerButton;
    private Button saveButton, clearButton, logoutButton;
    private TableLayout appointmentList;

    private FirebaseAuth mAuth;
    private DatabaseReference appointmentsRef;

    private int selectedYear, selectedMonth, selectedDay, selectedStartHour, selectedStartMinute, selectedEndHour, selectedEndMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_page);

        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance();
        appointmentsRef = FirebaseDatabase.getInstance().getReference().child("appointments");

        // Initialize views
        clinicName = findViewById(R.id.clinicName);
        welcomeText = findViewById(R.id.welcomeText);
        currentAvailability = findViewById(R.id.currentAvailability);
        dateInput = findViewById(R.id.dateInput);
        startTimeInput = findViewById(R.id.startTimeInput);
        endTimeInput = findViewById(R.id.endTimeInput);
        datePickerButton = findViewById(R.id.datePickerButton);
        startTimePickerButton = findViewById(R.id.startTimePickerButton);
        endTimePickerButton = findViewById(R.id.endTimePickerButton);
        saveButton = findViewById(R.id.saveButton);
        clearButton = findViewById(R.id.clearButton);
        logoutButton = findViewById(R.id.logoutButton);
        appointmentList = findViewById(R.id.appointmentList);

        // Set clinic name
        clinicName.setText("Healthy Life Clinic");

        // Check if user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Set welcome text
            welcomeText.setText("Welcome, Doctor " + currentUser.getDisplayName());
        }

        // Set click listeners
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        startTimePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(true);
            }
        });

        endTimePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(false);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAvailability();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAvailability();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish(); // Close current activity
            }
        });

        // Load appointments for current doctor
        loadAppointments();
    }

    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> dateInput.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                selectedYear,
                selectedMonth,
                selectedDay
        );
        datePickerDialog.show();
    }

    private void showTimePicker(final boolean isStartTime) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    if (isStartTime) {
                        selectedStartHour = selectedHour;
                        selectedStartMinute = selectedMinute;
                        startTimeInput.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    } else {
                        selectedEndHour = selectedHour;
                        selectedEndMinute = selectedMinute;
                        endTimeInput.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                },
                hour,
                minute,
                true
        );
        timePickerDialog.show();
    }

    private void saveAvailability() {
        String date = dateInput.getText().toString().trim();
        String startTime = startTimeInput.getText().toString().trim();
        String endTime = endTimeInput.getText().toString().trim();

        if (date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save availability to Firebase
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String doctorId = currentUser.getUid();
        DatabaseReference availabilityRef = appointmentsRef.child(doctorId).child("availability").push();
        availabilityRef.child("date").setValue(date);
        availabilityRef.child("startTime").setValue(startTime);
        availabilityRef.child("endTime").setValue(endTime);

        Toast.makeText(this, "Availability saved", Toast.LENGTH_SHORT).show();
    }

    private void clearAvailability() {
        dateInput.setText("");
        startTimeInput.setText("");
        endTimeInput.setText("");
    }

    private void loadAppointments() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String doctorId = currentUser.getUid();

        appointmentsRef.child(doctorId).child("appointments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.removeAllViews();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String patientName = snapshot.child("patientName").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String time = snapshot.child("time").getValue(String.class);
                    String patientSpecialty = snapshot.child("patientSpecialty").getValue(String.class);

                    TableRow row = new TableRow(DoctorsPage.this);

                    TextView dateTextView = new TextView(DoctorsPage.this);
                    dateTextView.setText(date);
                    dateTextView.setPadding(8, 8, 8, 8);

                    TextView timeTextView = new TextView(DoctorsPage.this);
                    timeTextView.setText(time);
                    timeTextView.setPadding(8, 8, 8, 8);

                    TextView patientTextView = new TextView(DoctorsPage.this);
                    patientTextView.setText(patientName);
                    patientTextView.setPadding(8, 8, 8, 8);

                    TextView specialtyTextView = new TextView(DoctorsPage.this);
                    specialtyTextView.setText(patientSpecialty);
                    specialtyTextView.setPadding(8, 8, 8, 8);

                    row.addView(dateTextView);
                    row.addView(timeTextView);
                    row.addView(patientTextView);
                    row.addView(specialtyTextView);

                    appointmentList.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorsPage.this, "Failed to load appointments: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
