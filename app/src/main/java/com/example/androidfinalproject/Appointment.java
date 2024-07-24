package com.example.androidfinalproject;

public class Appointment {
    private String date;
    private String time;
    private String doctorName;
    private String doctorSpecialty;

    public Appointment() {
        // Default constructor required for Firebase
    }

    public Appointment(String date, String time, String doctorName, String doctorSpecialty) {
        this.date = date;
        this.time = time;
        this.doctorName = doctorName;
        this.doctorSpecialty = doctorSpecialty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecialty() {
        return doctorSpecialty;
    }

    public void setDoctorSpecialty(String doctorSpecialty) {
        this.doctorSpecialty = doctorSpecialty;
    }
}
