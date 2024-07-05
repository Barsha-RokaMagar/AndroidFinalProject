package com.example.androidfinalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResetpasswordPage extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button resetButton;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword_page);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirmpassword);
        resetButton = findViewById(R.id.save);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ResetpasswordPage.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(ResetpasswordPage.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(ResetpasswordPage.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    String userId = mAuth.getCurrentUser().getUid();
                                    usersRef.child(userId).child("email").setValue(email)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(ResetpasswordPage.this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    } else {
                                                        Toast.makeText(ResetpasswordPage.this, "Failed to update user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(ResetpasswordPage.this, "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
