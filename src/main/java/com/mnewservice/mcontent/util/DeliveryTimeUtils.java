package com.mnewservice.mcontent.util;

import com.mnewservice.mcontent.domain.DeliveryTime;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class DeliveryTimeUtils {

    public static final String ZERO = "0";

    public static String[] parseDeliveryTimeAsStringArray(DeliveryTime deliveryTime) {
        String[] retVal = new String[3];
        retVal[0] = deliveryTime.name().substring(1, 3); // hours
        retVal[1] = deliveryTime.name().substring(3, 5); // minutes
        retVal[2] = ZERO; // seconds
        return retVal;
    }

    public static int[] parseDeliveryTimeAsIntArray(DeliveryTime deliveryTime) {
        String[] parsed = parseDeliveryTimeAsStringArray(deliveryTime);

        int[] retVal = new int[3];
        retVal[0] = Integer.parseInt(parsed[0]); // hours
        retVal[1] = Integer.parseInt(parsed[1]); // minutes
        retVal[2] = Integer.parseInt(parsed[2]); // seconds
        return retVal;
    }

}
