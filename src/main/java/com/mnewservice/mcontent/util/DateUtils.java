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

    public static Date getCurrentDate() {
        return getCurrentCalendar().getTime();
    }

    public static Date getCurrentDatePlusNDays(int days) {
        Calendar cal = getCurrentCalendar();
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
}
