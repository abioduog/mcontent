package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class EmailMessage extends AbstractMessage {

    private Collection<Email> receivers;

    public EmailMessage() {
        this.receivers = new ArrayList<>();
    }

    public Collection<Email> getReceivers() {
        return receivers;
    }

    public void setReceivers(Collection<Email> receivers) {
        this.receivers = receivers;
    }

}
