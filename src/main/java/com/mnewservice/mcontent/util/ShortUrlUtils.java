package com.mnewservice.mcontent.util;

import java.math.BigInteger;
import java.util.Random;

public class ShortUrlUtils {
    public static String getRandomShortIdentifier() {
        return new BigInteger(50, new Random()).toString(32);
    }
}
