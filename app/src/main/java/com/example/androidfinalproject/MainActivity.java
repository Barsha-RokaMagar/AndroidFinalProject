package com.example.androidfinalproject;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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


public class MainActivity extends AppCompatActivity {
    TextView profileusername, profilename, profilepass, profileemail, profilegender, profileusertype;
    TextView titlename, titleusername;
    Button signout, editprofile, deleteprofile;

    private static final int REQUEST_CODE_NOTIFICATION_PERMISSION = 1001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);


        profilename = findViewById(R.id.profilename);
        profileusername = findViewById(R.id.profileusername);
        profileemail = findViewById(R.id.profileemail);
        profilepass = findViewById(R.id.profilepass);
        profilegender = findViewById(R.id.profilegender);
        profileusertype = findViewById(R.id.profileusertype);

        titlename = findViewById(R.id.titlename);
        titleusername = findViewById(R.id.titleusername);

        signout = findViewById(R.id.SignOut);
        editprofile = findViewById(R.id.editprofile);
        deleteprofile = findViewById(R.id.deleteprofile);

        showDataUser();

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Loginpage.class);
                startActivity(intent);
                finish();
            }
        });

        deleteprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mainusername = profilename.getText().toString().trim();
                DatabaseReference reference = FirebaseDatabase.getInstance()
                        .getReference("users");

                Query checkuserdata = reference.orderByChild("username")
                        .equalTo(mainusername);
                checkuserdata.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            snapshot.child(mainusername).getRef().removeValue();
                            Toast.makeText(MainActivity.this, "Profile deleted", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, SignUppage.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passDataUser();
            }
        });
    }



    public void passDataUser() {
        String mainusername = profilename.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("users");

        Query checkuserdata = reference.orderByChild("username")
                .equalTo(mainusername);
        checkuserdata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String usernameDB = snapshot.child(mainusername).child("username")
                            .getValue(String.class);
                    String passDB = snapshot.child(mainusername).child("password")
                            .getValue(String.class);
                    String nameDB = snapshot.child(mainusername).child("name")
                            .getValue(String.class);
                    String emailDB = snapshot.child(mainusername).child("email")
                            .getValue(String.class);
                    String genderDB = snapshot.child(mainusername).child("gender")
                            .getValue(String.class);
                    String userTypeDB = snapshot.child(mainusername).child("userType")
                            .getValue(String.class);

                    Intent intent = new Intent(MainActivity.this, EditProfilePage.class);
                    intent.putExtra("name", nameDB);
                    intent.putExtra("email", emailDB);
                    intent.putExtra("pass", passDB);
                    intent.putExtra("username", usernameDB);
                    intent.putExtra("gender", genderDB);
                    intent.putExtra("userType", userTypeDB);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showDataUser() {
        Intent intent = getIntent();
        titlename.setText("Welcome " + intent.getStringExtra("name"));
        titleusername.setText(intent.getStringExtra("username"));
        profileusername.setText(intent.getStringExtra("username"));
        profileemail.setText(intent.getStringExtra("email"));
        profilename.setText(intent.getStringExtra("name"));
        profilepass.setText(intent.getStringExtra("password"));


        String gender = intent.getStringExtra("gender");
        if (gender != null) {
            profilegender.setText(gender);
        } else {
            profilegender.setText("Not specified");
        }

        String userType = intent.getStringExtra("userType");
        if (userType != null) {
            profileusertype.setText(userType);
        } else {
            profileusertype.setText("Not specified");
        }

        NotificationPermissionHelper.requestNotificationPermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        NotificationPermissionHelper.handlePermissionResult(requestCode, permissions, grantResults, this);
    }
}




