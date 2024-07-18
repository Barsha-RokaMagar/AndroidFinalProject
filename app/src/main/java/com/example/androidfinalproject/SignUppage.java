//package com.example.androidfinalproject;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.androidfinalproject.MainActivity;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class SignUppage extends AppCompatActivity {
//
//    EditText name, username, password, email;
//    TextView loginlink;
//    Button signupbtn;
//    RadioGroup userTypeRadioGroup, genderRadioGroup;
//    RadioButton radioDoctor, radioPatient, radioMale, radioFemale, radioOthers;
//    FirebaseDatabase database;
//    DatabaseReference reference;
//    FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_uppage);
//
//
//        mAuth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//        reference = database.getReference("users");
//
//        name = findViewById(R.id.name);
//        username = findViewById(R.id.username);
//        password = findViewById(R.id.password);
//        email = findViewById(R.id.email);
//        loginlink = findViewById(R.id.loginlink);
//        signupbtn = findViewById(R.id.signup);
//
//        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
//        genderRadioGroup = findViewById(R.id.genderRadioGroup);
//        radioDoctor = findViewById(R.id.radio_doctor);
//        radioPatient = findViewById(R.id.radio_patient);
//        radioMale = findViewById(R.id.radio_male);
//        radioFemale = findViewById(R.id.radio_female);
//        radioOthers = findViewById(R.id.radio_others);
//
//        loginlink.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SignUppage.this, Loginpage.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//        signupbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final String nameuser = name.getText().toString().trim();
//                final String emailuser = email.getText().toString().trim();
//                final String userusername = username.getText().toString().trim();
//                final String passuser = password.getText().toString().trim();
//
//                if (nameuser.isEmpty() || emailuser.isEmpty() || userusername.isEmpty() || passuser.isEmpty()) {
//                    Toast.makeText(SignUppage.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                String userType = "";
//                int selectedUserTypeId = userTypeRadioGroup.getCheckedRadioButtonId();
//                if (selectedUserTypeId == R.id.radio_doctor) {
//                    userType = "Doctor";
//                } else if (selectedUserTypeId == R.id.radio_patient) {
//                    userType = "Patient";
//                }
//
//                String gender = "";
//                int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
//                if (selectedGenderId == R.id.radio_male) {
//                    gender = "Male";
//                } else if (selectedGenderId == R.id.radio_female) {
//                    gender = "Female";
//                } else if (selectedGenderId == R.id.radio_others) {
//                    gender = "Others";
//                }
//
//
//                String finalUserType = userType;
//                String finalGender = gender;
//                mAuth.createUserWithEmailAndPassword(emailuser, passuser)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    // Sign up successful, update Realtime Database with user data
//                                    String userId = mAuth.getCurrentUser().getUid();
//                                    Model user = new Model(nameuser, emailuser, userusername, passuser, finalUserType, finalGender);
//                                    reference.child(userId).setValue(user)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()) {
//                                                        Toast.makeText(SignUppage.this, "Signup successful", Toast.LENGTH_SHORT).show();
//                                                        Intent intent = new Intent(SignUppage.this, MainActivity.class);
//                                                        intent.putExtra("name", nameuser);
//                                                        intent.putExtra("email", emailuser);
//                                                        intent.putExtra("password", passuser);
//                                                        intent.putExtra("username", userusername);
//                                                        intent.putExtra("gender", finalGender);
//                                                        intent.putExtra("userType", finalUserType);
//                                                        startActivity(intent);
//                                                        finish();
//                                                    } else {
//                                                        Toast.makeText(SignUppage.this, "Failed to update user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                    }
//                                                }
//                                            });
//                                } else {
//                                    Toast.makeText(SignUppage.this, "Failed to create user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//            }
//        });
//    }
//}

package com.example.androidfinalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUppage extends AppCompatActivity {

    EditText name, username, password, email, specialty;
    TextView loginlink;
    Button signupbtn;
    RadioGroup userTypeRadioGroup, genderRadioGroup;
    RadioButton radioDoctor, radioPatient, radioMale, radioFemale, radioOthers;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_uppage);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        loginlink = findViewById(R.id.loginlink);
        signupbtn = findViewById(R.id.signup);
        specialty = findViewById(R.id.specialty);

        userTypeRadioGroup = findViewById(R.id.userTypeRadioGroup);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        radioDoctor = findViewById(R.id.radio_doctor);
        radioPatient = findViewById(R.id.radio_patient);
        radioMale = findViewById(R.id.radio_male);
        radioFemale = findViewById(R.id.radio_female);
        radioOthers = findViewById(R.id.radio_others);

        userTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_doctor) {
                    specialty.setVisibility(View.VISIBLE);
                } else {
                    specialty.setVisibility(View.GONE);
                }
            }
        });

        loginlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUppage.this, Loginpage.class);
                startActivity(intent);
                finish();
            }
        });

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String nameuser = name.getText().toString().trim();
                final String emailuser = email.getText().toString().trim();
                final String userusername = username.getText().toString().trim();
                final String passuser = password.getText().toString().trim();
                final String userSpecialty = specialty.getText().toString().trim();

                if (nameuser.isEmpty() || emailuser.isEmpty() || userusername.isEmpty() || passuser.isEmpty()) {
                    Toast.makeText(SignUppage.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedGender = findViewById(selectedGenderId);
                String gender = selectedGender == null ? "" : selectedGender.getText().toString();

                int selectedUserTypeId = userTypeRadioGroup.getCheckedRadioButtonId();
                RadioButton selectedUserType = findViewById(selectedUserTypeId);
                String userType = selectedUserType == null ? "" : selectedUserType.getText().toString();

                // Check if Doctor is selected and validate specialty field
                if (userType.equals("Doctor") && userSpecialty.isEmpty()) {
                    Toast.makeText(SignUppage.this, "Please enter your specialty", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(emailuser, passuser)
                        .addOnCompleteListener(SignUppage.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String uid = mAuth.getCurrentUser().getUid();
                                    Model user = new Model(nameuser, emailuser, userusername, passuser, userType, gender, userSpecialty);
                                    reference.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUppage.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(SignUppage.this, Loginpage.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(SignUppage.this, "User registration failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(SignUppage.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
