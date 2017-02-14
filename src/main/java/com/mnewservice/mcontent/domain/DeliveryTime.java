package com.mnewservice.mcontent.domain;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public enum DeliveryTime {

    T0800("08:00"),
    T1000("10:00"),
    T1200("12:00"),
    T1400("14:00"),
    T1600("16:00"),
    T1800("18:00"),
    T2000("20:00"),
    T2200("22:00");

    private final String text;

    private DeliveryTime(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
