package com.mnewservice.mcontent.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Tommi Fredriksson <tommi.fredriksson at nolwenture.com>
 */
public class BaseNTest {

    /**
     * Test encoding
     */
    @Test
    public void testEncode() {
        Assert.assertEquals(""+BaseN.CHARSET.charAt(0), BaseN.encode(0));
        Assert.assertEquals(""+BaseN.CHARSET.charAt(1), BaseN.encode(1));
        Assert.assertEquals(""+BaseN.CHARSET.charAt(1) + BaseN.CHARSET.charAt(0), BaseN.encode(BaseN.CHARSET.length()));
    }
    
    /**
     * Test decoding
     */
    @Test
    public void testDecode() {
        Assert.assertEquals(0, BaseN.decode(""+BaseN.CHARSET.charAt(0)));
        Assert.assertEquals(1, BaseN.decode(""+BaseN.CHARSET.charAt(1)));
        Assert.assertEquals(BaseN.CHARSET.length(), BaseN.decode(""+BaseN.CHARSET.charAt(1) + BaseN.CHARSET.charAt(0)));
    }
    
    /**
     * Test encoding of negative number
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testNegativeEncode() {
        BaseN.encode(-1);
    }
    
    /**
     * Test decoding of negative input
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testNegativeDecode() {
        BaseN.decode("-");
    }
    
    /**
     * Test decoding of empty input
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testEmptyDecode() {
        BaseN.decode("");
    }
}
