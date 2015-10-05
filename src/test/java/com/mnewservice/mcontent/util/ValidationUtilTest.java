package com.mnewservice.mcontent.util;

import java.text.SimpleDateFormat;
import java.util.Locale;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class ValidationUtilTest {

    public ValidationUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of validateValueIgnoreCase method, of class ValidationUtils.
     */
    @Test
    public void testValidateValueIgnoreCase() {
        System.out.println("TEST: validateValueIgnoreCase");
        String expected = "READ";
        String actual = "read";
        ValidationUtils.validateValueIgnoreCase(expected, "", actual);
    }

    /**
     * Test of validateValueIgnoreCase method, of class ValidationUtils.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateValueIgnoreCaseFail() {
        System.out.println("TEST: validateValueIgnoreCase (fail)");
        String expected = "READ";
        String actual = "read ";
        ValidationUtils.validateValueIgnoreCase(expected, "", actual);
    }

    /**
     * Test of validateNumeric method, of class ValidationUtils.
     */
    @Test
    public void testValidateNumeric() {
        System.out.println("TEST: validateNumeric");
        String value = "123";
        ValidationUtils.validateNumeric("", value);
    }

    /**
     * Test of validateNumeric method, of class ValidationUtils.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateNumericFail() {
        System.out.println("TEST: validateNumeric (fail)");
        String value = "123x";
        ValidationUtils.validateNumeric("", value);
    }

    /**
     * Test of validateId method, of class ValidationUtils.
     */
    @Test
    public void testValidateId() {
        System.out.println("TEST: validateId");
        Long value = 123L;
        ValidationUtils.validateId("", value);
    }

    /**
     * Test of validateId method, of class ValidationUtils.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateIdFail() {
        System.out.println("TEST: validateId (fail)");
        Long value = -1L;
        ValidationUtils.validateId("", value);
    }

    /**
     * Test of validateTimestamp method, of class ValidationUtils.
     */
    @Test
    public void testValidateTimestamp() {
        System.out.println("TEST: validateTimestamp");
        SimpleDateFormat format
                = new SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.UK);
        String value = "2/16/2014 10:00 AM";
        ValidationUtils.validateTimestamp(format, "", value);
    }

    /**
     * Test of validateTimestamp method, of class ValidationUtils.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateTimestampFail() {
        System.out.println("TEST: validateTimestamp (fail)");
        SimpleDateFormat format
                = new SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.UK);
        String value = "x2/16/2014 10:00 AM";
        ValidationUtils.validateTimestamp(format, "", value);
    }

    /**
     * Test of validateNotNullOrEmpty method, of class ValidationUtils.
     */
    @Test
    public void testValidateNotNullOrEmpty1() {
        System.out.println("TEST: validateNotNullOrEmpty 1");
        Object value = new Object();
        ValidationUtils.validateNotNullOrEmpty("", value);
    }

    /**
     * Test of validateNotNullOrEmpty method, of class ValidationUtils.
     */
    @Test
    public void testValidateNotNullOrEmpty2() {
        System.out.println("TEST: validateNotNullOrEmpty 2");
        Object value = "abc";
        ValidationUtils.validateNotNullOrEmpty("", value);
    }

    /**
     * Test of validateNotNullOrEmpty method, of class ValidationUtils.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateNotNullOrEmptyFail1() {
        System.out.println("TEST: validateNotNullOrEmpty (fail 1)");
        Object value = null;
        ValidationUtils.validateNotNullOrEmpty("", value);
    }

    /**
     * Test of validateNotNullOrEmpty method, of class ValidationUtils.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateNotNullOrEmptyFail2() {
        System.out.println("TEST: validateNotNullOrEmpty (fail 2)");
        Object value = new String();
        ValidationUtils.validateNotNullOrEmpty("", value);
    }

}
