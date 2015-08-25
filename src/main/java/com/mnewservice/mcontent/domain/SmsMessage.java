package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class SmsMessage extends AbstractMessage {

    private Collection<PhoneNumber> receivers;

    public SmsMessage() {
        this.receivers = new ArrayList<>();
    }

    public Collection<PhoneNumber> getReceivers() {
        return receivers;
    }

    public void setReceivers(Collection<PhoneNumber> receivers) {
        this.receivers = receivers;
    }
}
