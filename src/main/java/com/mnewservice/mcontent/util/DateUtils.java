package com.mnewservice.mcontent.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    public static Date getCurrentDateAtMidnight() {
        return getCurrentCalendarAtMidnight().getTime();
    }

    public static Date getCurrentDate() {
        return getCurrentCalendar().getTime();
    }

    public static Date getCurrentDatePlusNDays(int days) {
        Calendar cal = getCurrentCalendar();
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
}
