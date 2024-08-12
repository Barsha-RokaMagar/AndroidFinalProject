package com.example.androidfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Loginpage extends AppCompatActivity {

    EditText loginusername, loginpass;
    Button loginbtn;
    TextView signuplink, forgotPasswordLink;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);

        mAuth = FirebaseAuth.getInstance();

        loginusername = findViewById(R.id.emaillogin);
        loginpass = findViewById(R.id.passwordlogin);
        loginbtn = findViewById(R.id.loginbtn);
        signuplink = findViewById(R.id.signuplink);
        forgotPasswordLink = findViewById(R.id.forgot_password_link);

        forgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Loginpage.this, ResetpasswordPage.class);
                startActivity(intent);
            }
        });

        signuplink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Loginpage.this, SignUppage.class);
                startActivity(intent);
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUser();
            }
        });
    }

    public void checkUser() {
        String email = loginusername.getText().toString().trim();
        String password = loginpass.getText().toString().trim();
      //  Toast.makeText(Loginpage.this, "Please fill all fields", Toast.LENGTH_LONG).show();
        Log.d("Loginpage", "Email: " + email);
        Log.d("Loginpage", "Password: " + password);

        if (email.isEmpty() || password.isEmpty()) {
            Log.d("Loginpage", "Empty fields detected");
            Toast.makeText(Loginpage.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidEmail(email)) {
            Log.d("Loginpage", "Invalid email detected");
            Toast.makeText(Loginpage.this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("Loginpage", "Proceeding with Firebase authentication");
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkUserType(user.getUid());
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception != null) {
                            Log.e("Loginpage", "Firebase Authentication error: " + exception.getMessage());
                            Toast.makeText(Loginpage.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Loginpage", "Unknown authentication error");
                            Toast.makeText(Loginpage.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean isValidEmail(String email) {
        boolean isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (!isValid) {
            Log.d("Loginpage", "Invalid email: " + email);
        }
        return isValid;
    }


    private void checkUserType(String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nameDB = snapshot.child("name").getValue(String.class);
                    String emailDB = snapshot.child("email").getValue(String.class);
                    String userTypeDB = snapshot.child("userType").getValue(String.class);
                    String specialtyDB = snapshot.child("specialty").getValue(String.class);

                    if ("doctor".equals(userTypeDB.toLowerCase())) {
                        Intent intent = new Intent(Loginpage.this, DoctorsPage.class);
                        intent.putExtra("name", nameDB);
                        intent.putExtra("email", emailDB);
                        intent.putExtra("specialty", specialtyDB);
                        startActivity(intent);
                    } else if ("patient".equals(userTypeDB.toLowerCase())) {
                        Intent intent = new Intent(Loginpage.this, PatientPage.class);
                        intent.putExtra("name", nameDB);
                        intent.putExtra("email", emailDB);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Loginpage.this, "Unsupported user type", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Loginpage.this, "User data does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Loginpage.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
