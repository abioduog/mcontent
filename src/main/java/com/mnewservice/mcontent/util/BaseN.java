package com.mnewservice.mcontent.util;

/**
 * Encode and decode positive numbers between decimal integers and
 * BASE-[{@link #CHARSET}'s length] (represented in {@link #CHARSET} characters).
 * 
 * @author Tommi Fredriksson <tommi.fredriksson at nolwenture.com>
 */
public class BaseN {

    /**
     * Charset used for encoding and decoding.
     */
    public static final String CHARSET = "BCDFGHJKLMNPQRSTVWXYZ23456789";

    /**
     * Encode decimal numbers to BASE-[{@link #CHARSET}].
     * @throws UnsupportedOperationException Negative numbers are not supported
     */
    public static String encode(long number) {
        if (number < 0) {
            throw new UnsupportedOperationException(String.format(
                    "Negative numbers (%d) are not supported",
                    number
            ));
        }

        if (number == 0) return ""+CHARSET.charAt(0);
        
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            result.append(CHARSET.charAt((int)(number % CHARSET.length())));
            number = number / CHARSET.length(); // floor
        }
        return result.reverse().toString();
    }

    /**
     * Decode BASE-[{@link #CHARSET}] to decimal number.
     * @throws UnsupportedOperationException Negative and empty inputs are not supported
     */
    public static long decode(String encodedNumber) {
        if (encodedNumber.length() == 0
                || encodedNumber.charAt(0) == '-'
        ) {
            throw new UnsupportedOperationException(String.format(
                    "Empty or negative numbers (%s) are not supported.",
                    encodedNumber
            ));
        }
        long result = 0;
        for (int i=0; i<encodedNumber.length(); i++) {
            int posInCharset = CHARSET.indexOf(encodedNumber.charAt(i));
            result += i * (CHARSET.length()-1) + posInCharset;
        }
        return result;
    }

}
