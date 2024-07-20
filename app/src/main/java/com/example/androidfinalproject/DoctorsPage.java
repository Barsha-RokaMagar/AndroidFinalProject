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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;

public class DoctorsPage extends AppCompatActivity {

    private EditText dateInput, startTimeInput, endTimeInput;
    private TextView currentAvailability;
    private TableLayout appointmentList;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_page);

        dateInput = findViewById(R.id.dateInput);
        startTimeInput = findViewById(R.id.startTimeInput);
        endTimeInput = findViewById(R.id.endTimeInput);
        currentAvailability = findViewById(R.id.currentAvailability);
        appointmentList = findViewById(R.id.appointmentList);

        Button saveButton = findViewById(R.id.saveButton);
        Button clearButton = findViewById(R.id.clearButton);
        ImageButton datePickerButton = findViewById(R.id.datePickerButton);
        ImageButton startTimePickerButton = findViewById(R.id.startTimePickerButton);
        ImageButton endTimePickerButton = findViewById(R.id.endTimePickerButton);


        databaseReference = FirebaseDatabase.getInstance().getReference("appointments");


        loadAppointments();

        datePickerButton.setOnClickListener(v -> showDatePickerDialog());
        startTimePickerButton.setOnClickListener(v -> showTimePickerDialog(startTimeInput));
        endTimePickerButton.setOnClickListener(v -> showTimePickerDialog(endTimeInput));

        saveButton.setOnClickListener(v -> saveAvailability());
        clearButton.setOnClickListener(v -> clearAvailability());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> dateInput.setText((month + 1) + "/" + dayOfMonth + "/" + year),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePickerDialog(final EditText timeInput) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this,
                (view, hourOfDay, minute) -> timeInput.setText(hourOfDay + ":" + String.format("%02d", minute)),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
    }

    private void saveAvailability() {
        String date = dateInput.getText().toString();
        String startTime = startTimeInput.getText().toString();
        String endTime = endTimeInput.getText().toString();
        String availability = "Available on: " + date + " from " + startTime + " to " + endTime;
        currentAvailability.setText(availability);
    }

    private void clearAvailability() {
        dateInput.setText("");
        startTimeInput.setText("");
        endTimeInput.setText("");
        currentAvailability.setText("");
    }

    private void loadAppointments() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                appointmentList.removeAllViews(); // Clear existing rows
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String date = snapshot.child("date").getValue(String.class);
                    String time = snapshot.child("time").getValue(String.class);
                    String patient = snapshot.child("patient").getValue(String.class);


                    TableRow tableRow = new TableRow(DoctorsPage.this);
                    tableRow.setLayoutParams(new TableRow.LayoutParams(
                            TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.WRAP_CONTENT));


                    TextView dateTextView = new TextView(DoctorsPage.this);
                    dateTextView.setText(date);
                    tableRow.addView(dateTextView);

                    TextView timeTextView = new TextView(DoctorsPage.this);
                    timeTextView.setText(time);
                    tableRow.addView(timeTextView);

                    TextView patientTextView = new TextView(DoctorsPage.this);
                    patientTextView.setText(patient);
                    tableRow.addView(patientTextView);


                    Button actionsButton = new Button(DoctorsPage.this);
                    actionsButton.setText("Actions");
                    tableRow.addView(actionsButton);


                    appointmentList.addView(tableRow);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
