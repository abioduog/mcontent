package com.mnewservice.mcontent.util;

import java.io.InputStream;
import java.util.Scanner;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class StreamUtils {

    private static final String DEFAULT_CHARSET = "UTF-8";

    public static String convertStreamToString(InputStream is) {
        return convertStreamToString(is, DEFAULT_CHARSET);
    }

    public static String convertStreamToString(
            InputStream is, String charsetName) {
        Scanner scanner = new Scanner(is, charsetName);
        /*one token for the entire contents of the stream*/
        scanner.useDelimiter("\\A");

        return scanner.hasNext() ? scanner.next() : "";
    }
}
