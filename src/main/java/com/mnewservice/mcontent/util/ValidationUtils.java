package com.mnewservice.mcontent.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.log4j.Logger;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class ValidationUtils {

    private static final Logger LOG = Logger.getLogger(ValidationUtils.class);

    public static final SimpleDateFormat FORMAT_MMDDYYYY_HHMM_AA
            = new SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.UK);

    private static final String ERROR_PARAM_EMPTY
            = "The '%s' parameter must not be null or empty";
    private static final String ERROR_PARAM_INVALID_VALUE
            = "The '%s' parameter has invalid value '%s'";
    private static final String NUMERIC_REGEXP = "[0-9]+";

    public static void validateValueIgnoreCase(String expected,
            String name, String actual) {
        validateNotNullOrEmpty(name, actual);
        if (!expected.equalsIgnoreCase(actual)) {
            String msg = String.format(ERROR_PARAM_INVALID_VALUE, name, actual);
            throw new IllegalArgumentException(msg);
        }
    }

    public static void validateNumeric(String name, String value) {
        validateNotNullOrEmpty(name, value);
        if (!value.matches(NUMERIC_REGEXP)) {
            String msg = String.format(ERROR_PARAM_INVALID_VALUE, name, value);
            throw new IllegalArgumentException(msg);
        }
    }

    public static void validateId(String name, Long value) {
        validateNotNullOrEmpty(name, value);
        validatePositive(name, value);
        if (value < 0) {
            String msg = String.format(ERROR_PARAM_INVALID_VALUE, name, value);
            throw new IllegalArgumentException(msg);
        }
    }

    public static void validateTimestamp(SimpleDateFormat format,
            String name, String value) {
        validateNotNullOrEmpty(name, value);
        try {
            format.parse(value);
        } catch (ParseException pe) {
            LOG.info(pe);
            String msg = String.format(ERROR_PARAM_INVALID_VALUE, name, value);
            throw new IllegalArgumentException(msg);
        }
    }

    public static void validateNotNullOrEmpty(String name, Object value)
            throws IllegalArgumentException {
        if (value == null
                || (value instanceof String && ((String) value).isEmpty())) {
            String msg = String.format(ERROR_PARAM_EMPTY, name);
            throw new IllegalArgumentException(msg);
        }
    }

    public static void validatePositive(String name, long value) {
        if (value < 0) {
            String msg = String.format(ERROR_PARAM_INVALID_VALUE, name, value);
            throw new IllegalArgumentException(msg);
        }
    }

}
