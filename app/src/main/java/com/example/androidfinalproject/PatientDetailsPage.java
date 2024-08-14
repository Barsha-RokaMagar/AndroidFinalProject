package com.example.androidfinalproject;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

public class PatientDetailsPage extends AppCompatActivity {

    private static final String TAG = "PatientDetailActivity";
    private static final String CHANNEL_ID = "default_channel_id";
    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 1001;

    private TextView patientName, patientEmail;
    private Button confirmButton, cancelButton, gobackButton;

    private DatabaseReference usersRef, appointmentRef;
    private String patientId, appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details_page);

        usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        appointmentRef = FirebaseDatabase.getInstance().getReference().child("appointments");

        patientName = findViewById(R.id.patientName);
        patientEmail = findViewById(R.id.patientEmail);
        confirmButton = findViewById(R.id.confirmButton);
        cancelButton = findViewById(R.id.cancelButton);
        gobackButton = findViewById(R.id.gobackButton);

        patientId = getIntent().getStringExtra("patientId");
        appointmentId = getIntent().getStringExtra("appointmentId");

        if (patientId == null || appointmentId == null) {
            Toast.makeText(this, "No patient data available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPatientDetails();

        confirmButton.setOnClickListener(v -> confirmAppointment());
        cancelButton.setOnClickListener(v -> cancelAppointment());
        gobackButton.setOnClickListener(v -> finish());
    }

    private void loadPatientDetails() {
        usersRef.child(patientId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);

                if (name != null && email != null) {
                    patientName.setText("Name: " + name);
                    patientEmail.setText("Email: " + email);
                } else {
                    Toast.makeText(PatientDetailsPage.this, "Failed to load patient details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Failed to load patient details", databaseError.toException());
                Toast.makeText(PatientDetailsPage.this, "Failed to load patient details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmAppointment() {
        appointmentRef.child(appointmentId).child("status").setValue("Confirmed").addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PatientDetailsPage.this, "Appointment confirmed", Toast.LENGTH_SHORT).show();
                requestNotificationPermission("Appointment Confirmed", "Your appointment has been confirmed.");
            } else {
                Toast.makeText(PatientDetailsPage.this, "Failed to confirm appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelAppointment() {
        appointmentRef.child(appointmentId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(PatientDetailsPage.this, "Appointment cancelled", Toast.LENGTH_SHORT).show();
                requestNotificationPermission("Appointment Cancelled", "Your appointment has been cancelled.");
            } else {
                Toast.makeText(PatientDetailsPage.this, "Failed to cancel appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void requestNotificationPermission(String title, String messageBody) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATION_PERMISSION);
            } else {
                sendNotification(title, messageBody);
            }
        } else {
            sendNotification(title, messageBody);
        }
    }

    private void sendNotification(String title, String messageBody) {
        // Check if the permission is granted before sending notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Notification permission not granted", Toast.LENGTH_SHORT).show();
            return; // Exit if permission is not granted
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification) // Ensure this drawable exists
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.notification))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Default Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, send the notification
                sendNotification("Appointment Status", "Your appointment status has been updated.");
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
