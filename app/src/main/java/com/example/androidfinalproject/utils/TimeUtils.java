package com.example.androidfinalproject.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());

    public static Date parseTime(String timeString) throws ParseException {
        return TIME_FORMAT.parse(timeString);
    }
    public static String formatDate(String day, String month, String year) {
        // Assuming you want to return the date in "dd/MM/yyyy" format
        return day + "/" + month + "/" + year;
    }

    public static String formatTime(Date time) {
        return TIME_FORMAT.format(time);
    }
}
