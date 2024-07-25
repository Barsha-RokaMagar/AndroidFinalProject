package com.example.androidfinalproject;

import java.util.Date;

public class Appointment {
    private String appointmentId;
    private String doctorName;
    private String patientName;
    private Date date;
    private String time;
    private String details;

    // Default constructor
    public Appointment() {
    }

    // Parameterized constructor
    public Appointment(String appointmentId, String doctorName, String patientName, Date date, String time, String details) {
        this.appointmentId = appointmentId;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.date = date;
        this.time = time;
        this.details = details;
    }

    // Getters and setters
    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "Appointment with Dr. " + doctorName + " on " + date.toString() + " at " + time;
    }
}
