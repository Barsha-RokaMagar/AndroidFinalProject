package com.example.androidfinalproject;

import java.util.List;

public class Model {
    private String name;
    private String email;
    private String username;
    private String password;
    private String userType;
    private String gender;
    private String specialty;
    private List<Appointment> appointments;

    // Default constructor required for Firebase
    public Model() {}

    // Constructor with all parameters
    public Model(String name, String email, String username, String password, String userType, String gender, String specialty, List<Appointment> appointments) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.gender = gender;
        this.specialty = specialty;
        this.appointments = appointments;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public List<Appointment> getAppointments() { return appointments; }
    public void setAppointments(List<Appointment> appointments) { this.appointments = appointments; }
}
