package com.veritas.veritas.Util;

import java.util.Calendar;

public class CurrentTime {
    public static String getCurrentTimeStamp() {
        Calendar now = Calendar.getInstance();
        Integer hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        if (minute / 10 == 0) return hour + ":0" + minute;
        return hour + ":" + minute;
    }
}
