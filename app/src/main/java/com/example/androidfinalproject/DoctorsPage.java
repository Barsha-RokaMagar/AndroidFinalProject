package com.example.androidfinalproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
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

    private static final String TAG = "DoctorsPage";

    private TextView clinicName, welcomeText, currentAvailability;
    private EditText dateInput, startTimeInput, endTimeInput;
    private ImageButton datePickerButton, startTimePickerButton, endTimePickerButton;
    private Button saveButton, clearButton, logoutButton;
    private TableLayout appointmentList;

    private FirebaseAuth mAuth;
    private DatabaseReference availabilityRef;

    private int selectedYear, selectedMonth, selectedDay, selectedStartHour, selectedStartMinute, selectedEndHour, selectedEndMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_page);

        // Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if user is logged in
        if (currentUser != null) {
            Log.d(TAG, "User is logged in: " + currentUser.getUid());
            welcomeText.setText("Welcome, Doctor " + (currentUser.getDisplayName() != null ? currentUser.getDisplayName() : ""));
        } else {
            Log.d(TAG, "User not logged in");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if the user is not logged in
            return;
        }

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

        // Initialize Firebase database reference for availability
        availabilityRef = FirebaseDatabase.getInstance().getReference().child("availability").child(currentUser.getUid());

        // Load availability for current doctor
        loadAvailability();
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Save button clicked but user is null");
            return;
        }

        String date = dateInput.getText().toString().trim();
        String startTime = startTimeInput.getText().toString().trim();
        String endTime = endTimeInput.getText().toString().trim();

        if (date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String doctorId = currentUser.getUid();
        DatabaseReference availabilityEntry = availabilityRef.push();
        availabilityEntry.child("date").setValue(date);
        availabilityEntry.child("startTime").setValue(startTime);
        availabilityEntry.child("endTime").setValue(endTime).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(DoctorsPage.this, "Availability saved", Toast.LENGTH_SHORT).show();
                loadAvailability(); // Reload to display updated availability
            } else {
                Toast.makeText(DoctorsPage.this, "Failed to save availability", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error saving availability", task.getException());
            }
        });
    }

    private void clearAvailability() {
        dateInput.setText("");
        startTimeInput.setText("");
        endTimeInput.setText("");
    }

    private void loadAvailability() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            return;
        }

        String doctorId = currentUser.getUid();

        availabilityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.removeAllViews();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String date = snapshot.child("date").getValue(String.class);
                    String startTime = snapshot.child("startTime").getValue(String.class);
                    String endTime = snapshot.child("endTime").getValue(String.class);

                    TableRow row = new TableRow(DoctorsPage.this);

                    TextView dateTextView = new TextView(DoctorsPage.this);
                    dateTextView.setText(date);
                    dateTextView.setPadding(8, 8, 8, 8);

                    TextView timeTextView = new TextView(DoctorsPage.this);
                    timeTextView.setText(startTime + " - " + endTime);
                    timeTextView.setPadding(8, 8, 8, 8);

                    row.addView(dateTextView);
                    row.addView(timeTextView);

                    appointmentList.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorsPage.this, "Failed to load availability: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error loading availability", databaseError.toException());
            }
        });
    }
}
