package com.mnewservice.mcontent.security;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
@WebAppConfiguration
public class PasswordEncrypterTest {

    public PasswordEncrypterTest() {
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
     * Test of encrypt method, of class PasswordEncrypter.
     */
    @Test
    public void testEncrypt() {
        System.out.println("encrypt test omitted");
        /*
         String pw = "test1234";
         PasswordEncrypter instance = PasswordEncrypter.getInstance();
         String expResult = "";
         String result = instance.encrypt(pw);
         System.out.println("result: " + result);

         assertEquals(expResult, result);
         */
    }

}
