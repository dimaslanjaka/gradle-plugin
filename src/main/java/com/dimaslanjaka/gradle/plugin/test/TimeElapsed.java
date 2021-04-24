package com.dimaslanjaka.gradle.plugin.test;

import com.dimaslanjaka.gradle.plugin.Utils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.dimaslanjaka.kotlin.ConsoleColors.println;
import static com.dimaslanjaka.kotlin.Date.isMoreThanHourAgo;
import static com.dimaslanjaka.kotlin.Date.isMoreThanMinuteAgo;

public class TimeElapsed {
    static long ONE_SECOND = 60;
    static long ONE_MINUTE = ONE_SECOND * 1000;
    public static long ONE_HOUR = 60 * ONE_MINUTE;

    public static long getTimestamp() {
        return new java.util.Date(System.currentTimeMillis()).getTime();
    }

    static Date getDate() {
        return new java.util.Date(System.currentTimeMillis());
    }

    public static void main(String[] args) {
        // Read the file "application.properties" into a PropertyFile
        final File timefile = new com.dimaslanjaka.gradle.plugin.File(Utils.getTempDir("gradle"), "timestamp.txt");
        try {
            String completionDate1 = "04/02/2011 20:27:05";
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = df.parse(completionDate1);
            String format = df.format(date);
            println("Hour", isMoreThanHourAgo(getDate(), 1), isMoreThanHourAgo(date, 1));
            println("Mins", isMoreThanMinuteAgo(date, 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getDateTime() {

        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());

        Date date = new Date();

        return dateFormat.format(date);
    }

    static long getHourMilis(int n) {
        return ((long) n * 60 * 60 * 1000);
    }
}
