package com.mnewservice.mcontent.util;

import java.util.Random;

public class ShortUrlUtils {
    
    private final static int DESIRED_SHORTID_CHARLENGTH = 4;
    
    private final static long MAX_RANDOM = (long) Math.pow(BaseN.CHARSET.length(), DESIRED_SHORTID_CHARLENGTH);
    private final static Random RANDOMGEN = new Random();
    
    public static String getRandomShortIdentifier() {
        // generate a random number that can be represented in certain number of characters using BaseN.CHARSET
        // please take note of the maximum number of different IDs:
        // = pow(BaseN.CHARSET.length(), desired_shortid_charlength)
        
        long randomNum = (long)(RANDOMGEN.nextDouble()* MAX_RANDOM); // safe, because random is always <1 and (int) cast floors
        return BaseN.encode(randomNum);
    }
}
