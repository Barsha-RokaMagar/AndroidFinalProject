package com.example.androidfinalproject;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

//import com.google.firebase.FirebaseApp;

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

    }
}



