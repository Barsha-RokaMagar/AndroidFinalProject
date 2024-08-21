package com.example.androidfinalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AppointmentAdapter extends ArrayAdapter<Appointment> {

    private Context context;
    private int resource;
    private List<Appointment> appointmentList;

    public AppointmentAdapter(Context context, int resource, List<Appointment> appointmentList) {
        super(context, resource, appointmentList);
        this.context = context;
        this.resource = resource;
        this.appointmentList = appointmentList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        Appointment appointment = appointmentList.get(position);

       // TextView patientName = convertView.findViewById(R.id.patientName);
        TextView selectedDoctor = convertView.findViewById(R.id.selectedDoctor);
        TextView appointmentDate = convertView.findViewById(R.id.appointmentDate);
        TextView appointmentTime = convertView.findViewById(R.id.appointmentTime);
        TextView status = convertView.findViewById(R.id.status);

       // patientName.setText("Name: " + appointment.getPatientName());
        selectedDoctor.setText("Doctor: " + appointment.getDoctorName());
        appointmentDate.setText("Date: " + appointment.getDate().toString());
        appointmentTime.setText("Time: " + appointment.getTime());
        status.setText("Status: " + appointment.getStatus());

        return convertView;
    }
}
