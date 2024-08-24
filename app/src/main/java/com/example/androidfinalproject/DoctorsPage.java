package com.example.androidfinalproject;

import com.example.androidfinalproject.utils.TimeUtils;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.Calendar;

public class DoctorsPage extends AppCompatActivity {

    private static final String TAG = "DoctorsPage";
    private String appointmentId;
    public String day , year,month;

    private TextView clinicName, welcomeText, currentAvailability;
    private EditText dateInput, startTimeInput, endTimeInput;
    private ImageButton datePickerButton, startTimePickerButton, endTimePickerButton;
    private Button saveButton, clearButton, logoutButton;
    private TableLayout appointmentList;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference availabilityRef, appointmentRef;
    String doctorName;
    private int selectedYear, selectedMonth, selectedDay, selectedStartHour, selectedStartMinute, selectedEndHour, selectedEndMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_page);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            loadPatientDetails();
            Log.d(TAG, "User is logged in: " + currentUser.getUid());
        } else {
            Log.d(TAG, "User not logged in");
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


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
            finish();
        });


        String doctorId = currentUser.getUid();
        availabilityRef = FirebaseDatabase.getInstance().getReference().child("availability").child(doctorId);
        appointmentRef = FirebaseDatabase.getInstance().getReference().child("appointments");

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


        currentAvailability.setText("");

        String doctorId = mAuth.getCurrentUser().getUid();
        availabilityRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Availability cleared", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to clear availability", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Failed to clear availability", task.getException());
            }
        });
    }


    private void loadAvailability() {
        String doctorId = mAuth.getCurrentUser().getUid();

        availabilityRef = FirebaseDatabase.getInstance().getReference("availability").child(doctorId);

        availabilityRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    StringBuilder builder = new StringBuilder();

                    for (DataSnapshot daySnapshot : dataSnapshot.getChildren()) {
                        day = daySnapshot.getKey();

                        for (DataSnapshot monthSnapshot : daySnapshot.getChildren()) {
                            month = monthSnapshot.getKey();

                            for (DataSnapshot yearSnapshot : monthSnapshot.getChildren()) {
                                year = yearSnapshot.getKey();

                                String startTimeString = yearSnapshot.child("startTime").getValue(String.class);
                                String endTimeString = yearSnapshot.child("endTime").getValue(String.class);

                                Log.d(TAG, "Date: " + day + "/" + month + "/" + year);
                                Log.d(TAG, "Start Time String: " + startTimeString);
                                Log.d(TAG, "End Time String: " + endTimeString);

                                if (startTimeString != null && endTimeString != null) {
                                    try {

                                        String formattedDate = TimeUtils.formatDate(day, month, year);
                                        String formattedStartTime = formatTime(startTimeString);
                                        String formattedEndTime = formatTime(endTimeString);

                                        builder.append(formattedDate)
                                                .append(", ")
                                                .append(formattedStartTime)
                                                .append(" - ")
                                                .append(formattedEndTime)
                                                .append("\n");
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error formatting time", e);
                                        builder.append(day + "/" + month + "/" + year)
                                                .append(": Error formatting time\n");
                                    }
                                } else {
                                    builder.append(day + "/" + month + "/" + year)
                                            .append(": Start time or End time missing\n");
                                }
                            }
                        }
                    }

                    if (builder.length() > 0) {
                        currentAvailability.setText(builder.toString());
                    } else {
                        currentAvailability.setText("No availability data found.");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading availability", e);
                    Toast.makeText(DoctorsPage.this, "Error loading availability", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load availability", databaseError.toException());
                Toast.makeText(DoctorsPage.this, "Failed to load availability", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private String formatDate(String dateString) {

        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat targetFormat = new SimpleDateFormat("MMMM d", Locale.getDefault()); // "August 29"
        try {
            Date date = originalFormat.parse(dateString);
            return targetFormat.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing date", e);
            return dateString;
        }
    }

    private String formatTime(String timeString) {

        SimpleDateFormat originalFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat targetFormat = new SimpleDateFormat("h:mm a", Locale.getDefault()); // "9:30 AM"
        try {
            Date time = originalFormat.parse(timeString);
            return targetFormat.format(time);
        } catch (ParseException e) {
            Log.e(TAG, "Error parsing time", e);
            return timeString;
        }
    }
    private void loadPatientDetails() {
        FirebaseDatabase.getInstance().getReference().child("users").child( mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            doctorName = dataSnapshot.child("name").getValue(String.class).toString();
                            loadAppointments();
                        } else {
                            Toast.makeText(DoctorsPage.this, "doctor not found", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("PatientsProfile", "Failed to load doctor details", databaseError.toException());
                        Toast.makeText(DoctorsPage.this, "Failed to load doctor details", Toast.LENGTH_SHORT).show();
                    }
                });
    }



    private void loadAppointments() {
        Log.d("doctor", "doctorname: " + doctorName);

        // Query for all appointments
        Query queryAll = appointmentRef;

        // Create a listener for the query
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    appointmentList.removeAllViews();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Check each field for the doctorName
                        boolean isMatch = false;
                        for (String specialty : new String[] {"cardiologist", "dentist", "dermatologist", "gynecologist", "neurologist", "ophthalmologist", "pediatrician", "psychologist"}) {
                            String value = snapshot.child(specialty).getValue(String.class);
                            if (doctorName.equals(value)) {
                                isMatch = true;
                                break;
                            }
                        }

                        // If there's a match, add the appointment to the view
                        if (isMatch) {
                            // Fetch appointment details
                            String date = snapshot.child("date").getValue(String.class);
                            String time = snapshot.child("time").getValue(String.class);
                            String endTime = snapshot.child("endTime").getValue(String.class);
                            String patientId = snapshot.child("patientId").getValue(String.class);
                            String appointmentId = snapshot.getKey();

                            // Create a new TableRow
                            TableRow row = new TableRow(DoctorsPage.this);

                            // Add Date TextView
                            TextView dateView = new TextView(DoctorsPage.this);
                            dateView.setText("Date: " + date);
                            dateView.setPadding(8, 8, 8, 8);
                            row.addView(dateView);

                            // Add Time TextView
                            TextView timeView = new TextView(DoctorsPage.this);
                            timeView.setText("Time: " + time);
                            timeView.setPadding(8, 8, 8, 8);
                            row.addView(timeView);

                            // Add Actions TextView with OnClickListener
                            TextView actionsView = new TextView(DoctorsPage.this);
                            actionsView.setText("View Patient");
                            actionsView.setPadding(8, 8, 8, 8);
                            actionsView.setOnClickListener(v -> {
                                Intent intent = new Intent(DoctorsPage.this, PatientDetailsPage.class);
                                intent.putExtra("patientId", patientId);
                                intent.putExtra("appointmentId", appointmentId);
                                startActivity(intent);
                            });
                            row.addView(actionsView);

                            // Add the row to the TableLayout
                            appointmentList.addView(row);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error loading appointments", e);
                    Toast.makeText(DoctorsPage.this, "Error loading appointments", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load appointments", databaseError.toException());
                Toast.makeText(DoctorsPage.this, "Failed to load appointments", Toast.LENGTH_SHORT).show();
            }
        };

        // Attach listener to the query
        queryAll.addValueEventListener(listener);
    }



    private void viewPatient(String patientId) {
        Intent intent = new Intent(DoctorsPage.this, PatientDetailsPage.class);
        intent.putExtra("patientId", patientId);
        intent.putExtra("appointmentId", appointmentId);
        startActivity(intent);
    }
}

