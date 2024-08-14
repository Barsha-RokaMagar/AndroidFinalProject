package com.example.androidfinalproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class NotificationPermissionHelper {

    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 1001;

    public static void requestNotificationPermission(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_NOTIFICATION_PERMISSION);
            }
        }
    }

    public static void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, AppCompatActivity activity) {
        if (requestCode == REQUEST_CODE_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
