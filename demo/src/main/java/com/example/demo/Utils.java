package com.example.demo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Utils {

    public static final DateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Utils(){}

    public static long truncate(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long truncate(long inDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(inDate);
        return Utils.truncate(cal);
    }

    public static List<Long> getDateList(long startDate, long departDate) {
        List<Long> dates = new ArrayList<>();
        long reserveDate = Utils.truncate(startDate);
        long truncateDepart = Utils.truncate(departDate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(reserveDate);
        while (reserveDate < truncateDepart) {
            dates.add(reserveDate);
            calendar.add(Calendar.DATE, 1);
            reserveDate = calendar.getTimeInMillis();
        }
        return dates;
    }


    public static List<Long> getDateList(Calendar calendar, int dateRange) {
        List<Long> dates = new ArrayList<>();
        long date = Utils.truncate(calendar);
        while (dateRange-- > 0) {
            dates.add(date);
            calendar.add(Calendar.DATE, 1);
            date = calendar.getTimeInMillis();
        }
        return dates;
    }
}
