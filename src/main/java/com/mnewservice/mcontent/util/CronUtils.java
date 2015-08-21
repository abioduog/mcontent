package com.mnewservice.mcontent.util;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class CronUtils {

    private static final String CRON_FORMAT
            = "%1$s %2$s %3$s %4$s %5$s %6$s %7$s";

    public static final String EVERY = "*";
    public static final String OMIT = "?";
    public static final String NOT_GIVEN = "";
    public static final String ZERO = "0";

    /**
     * Generate a CRON expression is a string comprising 6 or 7 fields separated
     * by white space. Source: http://stackoverflow.com/a/29628593
     *
     * @param seconds mandatory = yes. allowed values = {@code  0-59    * / , -}
     * @param minutes mandatory = yes. allowed values = {@code  0-59    * / , -}
     * @param hours mandatory = yes. allowed values = {@code 0-23   * / , -}
     * @param dayOfMonth mandatory = yes. allowed values =
     * {@code 1-31  * / , - ? L W}
     * @param month mandatory = yes. allowed values =
     * {@code 1-12 or JAN-DEC    * / , -}
     * @param dayOfWeek mandatory = yes. allowed values =
     * {@code 0-6 or SUN-SAT * / , - ? L #}
     * @param year mandatory = no. allowed values = {@code 1970–2099    * / , -}
     * @return a CRON Formatted String.
     */
    public static String generateCronExpression(
            final String seconds, final String minutes, final String hours,
            final String dayOfMonth, final String month, final String dayOfWeek,
            final String year) {
        return String.format(
                CRON_FORMAT, seconds, minutes, hours, dayOfMonth, month,
                dayOfWeek, year);
    }
}
