package com.mnewservice.mcontent.util;

import java.util.Calendar;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class DateUtilsTest {

    /**
     * Test of calculateDifferenceInDays method, of class DateUtils.
     */
    @Test
    public void testCalculateDifferenceInDaysPositive() {
        System.out.println("calculateDifferenceInDays");
        Date d1 = new Date(1440604904000L); // 2015-08-26 16:01:44 GMT
        Date d2 = new Date(1440436866000L); // 2015-08-24 17:21:06 GMT
        int expected = 2;
        int actual = DateUtils.calculateDifferenceInDays(d1, d2);
        assertEquals(expected, actual);
    }

    /**
     * Test of calculateDifferenceInDays method, of class DateUtils.
     */
    @Test
    public void testCalculateDifferenceInDaysNegative() {
        System.out.println("calculateDifferenceInDays");
        Date d1 = new Date(1440436866000L); // 2015-08-24 17:21:06 GMT
        Date d2 = new Date(1440604904000L); // 2015-08-26 16:01:44 GMT
        int expected = -2;
        int actual = DateUtils.calculateDifferenceInDays(d1, d2);
        assertEquals(expected, actual);
    }

    /**
     * Test of calculateDifferenceInDays method, of class DateUtils.
     */
    @Test
    public void testCalculateDifferenceInDaysZero() {
        System.out.println("calculateDifferenceInDays");
        Date d1 = new Date(1440436866000L); // 2015-08-24 17:21:06 GMT
        Date d2 = new Date(1440440122000L); // 2015-08-24 18:15:22 GMT
        int expected = 0;
        int actual = DateUtils.calculateDifferenceInDays(d1, d2);
        assertEquals(expected, actual);
    }

}
