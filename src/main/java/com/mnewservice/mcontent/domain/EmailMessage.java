package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class EmailMessage extends AbstractMessage {

    private String subject;
    private Email sender;
    private Collection<Email> receivers;

    public EmailMessage() {
        this.receivers = new ArrayList<>();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Email getSender() {
        return sender;
    }

    public void setSender(Email sender) {
        this.sender = sender;
    }

    public Collection<Email> getReceivers() {
        return receivers;
    }

    public void setReceivers(Collection<Email> receivers) {
        this.receivers = receivers;
    }

}
