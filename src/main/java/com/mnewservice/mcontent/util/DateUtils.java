package com.mnewservice.mcontent.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class DateUtils {

    public static Calendar getCurrentCalendar() {
        return new GregorianCalendar();
    }

    public static Calendar getCurrentCalendarAtMidnight() {
        Calendar cal = new GregorianCalendar();
        setMidnight(cal);
        return cal;
    }

    private static void setMidnight(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    public static Date getCurrentDateAtMidnight() {
        return getCurrentCalendarAtMidnight().getTime();
    }

    public static Date getCurrentDate() {
        return getCurrentCalendar().getTime();
    }

    public static Date addDaysToMidnight(Date date, int days) {
        Calendar cal = getCalendar(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        int seconds = days * 24 * 60 *60;
        cal.add(Calendar.SECOND, seconds - 1);
        return cal.getTime();
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = getCalendar(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    public static Date getCurrentDatePlusNDays(int days) {
        return addDays(getCurrentDate(), days);
    }

    /* calculate difference d1-d2 in days */
    public static int calculateDifferenceInDays(Date d1, Date d2) {
        Calendar c1 = getCalendar(d1);
        Calendar c2 = getCalendar(d2);

        setMidnight(c1);
        setMidnight(c2);

        return (int) TimeUnit.DAYS.convert(
                (long) c1.getTimeInMillis() - (long) c2.getTimeInMillis(),
                TimeUnit.MILLISECONDS
        );

    }

    public static Calendar getCalendar(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }
}
