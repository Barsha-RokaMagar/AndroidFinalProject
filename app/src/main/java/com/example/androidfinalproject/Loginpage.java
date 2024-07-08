package com.example.androidfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Loginpage extends AppCompatActivity {

    EditText loginusername, loginpass;
    Button loginbtn;
    TextView signuplink;
    CheckBox rememberMeCheckbox;
    TextView forgotPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);

        FirebaseApp.initializeApp(this);

        loginusername = findViewById(R.id.usernamelogin);
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

                checkuser();
            }
        });
    }


    public void checkuser() {
        String usernamelogin = loginusername.getText().toString().trim();
        String passlogin = loginpass.getText().toString().trim();

        if (usernamelogin.isEmpty() || passlogin.isEmpty()) {
            Toast.makeText(Loginpage.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkuserdata = reference.orderByChild("username").equalTo(usernamelogin);

        checkuserdata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    String passDB = userSnapshot.child("password").getValue(String.class);

                    if (passlogin.equals(passDB)) {
                        String usernameDB = userSnapshot.child("username").getValue(String.class);
                        String nameDB = userSnapshot.child("name").getValue(String.class);
                        String emailDB = userSnapshot.child("email").getValue(String.class);
                        String userTypeDB = userSnapshot.child("userType").getValue(String.class);


                        if ("doctor".equals(userTypeDB.toLowerCase())) {
                            Intent intent = new Intent(Loginpage.this, DoctorsPage.class);
                            intent.putExtra("name", nameDB);
                            intent.putExtra("email", emailDB);
                            intent.putExtra("username", usernameDB);
                            startActivity(intent);
                        } else if ("patient".equals(userTypeDB.toLowerCase())) {
                            Intent intent = new Intent(Loginpage.this, PatientPage.class);
                            intent.putExtra("name", nameDB);
                            intent.putExtra("email", emailDB);
                            intent.putExtra("username", usernameDB);
                            startActivity(intent);
                        } else {
                            Toast.makeText(Loginpage.this, "Unsupported user type", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Loginpage.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Loginpage.this, "User does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Loginpage.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
