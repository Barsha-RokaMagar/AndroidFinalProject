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
    private DatabaseReference availabilityRef, appointmentRef;

    private int selectedYear, selectedMonth, selectedDay, selectedStartHour, selectedStartMinute, selectedEndHour, selectedEndMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_page);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

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

        clinicName.setText("Healthy Life Clinic");

        datePickerButton.setOnClickListener(v -> showDatePicker());
        startTimePickerButton.setOnClickListener(v -> showTimePicker(true));
        endTimePickerButton.setOnClickListener(v -> showTimePicker(false));
        saveButton.setOnClickListener(v -> saveAvailability());
        clearButton.setOnClickListener(v -> clearAvailability());
        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            finish(); // Close current activity
        });

        String doctorId = currentUser.getUid();
        availabilityRef = FirebaseDatabase.getInstance().getReference().child("availability").child(doctorId);
        appointmentRef = FirebaseDatabase.getInstance().getReference().child("appointments");

        loadAvailability();
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
                (view, hourOfDay, minuteOfHour) -> {
                    if (isStartTime) {
                        selectedStartHour = hourOfDay;
                        selectedStartMinute = minuteOfHour;
                        startTimeInput.setText(String.format("%02d:%02d", hourOfDay, minuteOfHour));
                    } else {
                        selectedEndHour = hourOfDay;
                        selectedEndMinute = minuteOfHour;
                        endTimeInput.setText(String.format("%02d:%02d", hourOfDay, minuteOfHour));
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

        String doctorId = mAuth.getCurrentUser().getUid();
        availabilityRef.child(date).child("startTime").setValue(startTime);
        availabilityRef.child(date).child("endTime").setValue(endTime);
        Toast.makeText(this, "Availability saved", Toast.LENGTH_SHORT).show();
    }

    private void clearAvailability() {
        dateInput.setText("");
        startTimeInput.setText("");
        endTimeInput.setText("");
    }

    private void loadAvailability() {
        String doctorId = mAuth.getCurrentUser().getUid();
        availabilityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder builder = new StringBuilder();
                for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                    String date = dateSnapshot.getKey();
                    String startTime = dateSnapshot.child("startTime").getValue(String.class);
                    String endTime = dateSnapshot.child("endTime").getValue(String.class);
                    builder.append(date).append(": ").append(startTime).append(" - ").append(endTime).append("\n");
                }
                currentAvailability.setText(builder.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorsPage.this, "Failed to load availability", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAppointments() {
        appointmentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String date = snapshot.child("date").getValue(String.class);
                    String time = snapshot.child("time").getValue(String.class);
                    String cardiologist = snapshot.child("cardiologist").getValue(String.class);
                    String patientId = snapshot.child("patientId").getValue(String.class);

                    TableRow row = new TableRow(DoctorsPage.this);
                    TextView dateView = new TextView(DoctorsPage.this);
                    dateView.setText(date);
                    dateView.setPadding(8, 8, 8, 8);
                    row.addView(dateView);

                    TextView timeView = new TextView(DoctorsPage.this);
                    timeView.setText(time);
                    timeView.setPadding(8, 8, 8, 8);
                    row.addView(timeView);

                    TextView cardiologistView = new TextView(DoctorsPage.this);
                    cardiologistView.setText(cardiologist);
                    cardiologistView.setPadding(8, 8, 8, 8);
                    row.addView(cardiologistView);

                    TextView actionsView = new TextView(DoctorsPage.this);
                    actionsView.setText("View Patient");
                    actionsView.setPadding(8, 8, 8, 8);
                    actionsView.setOnClickListener(v -> viewPatient(patientId));
                    row.addView(actionsView);

                    appointmentList.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DoctorsPage.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void viewPatient(String patientId) {
        // Add functionality to view patient details
        Toast.makeText(this, "Viewing details for patient: " + patientId, Toast.LENGTH_SHORT).show();
    }
}
