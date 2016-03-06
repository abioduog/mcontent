package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class SmsMessage extends AbstractMessage {

    private Collection<PhoneNumber> receivers;
    private String fromNumber;

    private Date created;
    private Date sent;
    private Integer tries = 0;
    private String log;

    public SmsMessage() {
        this.receivers = new ArrayList<>();
    }

    public Collection<PhoneNumber> getReceivers() {
        return receivers;
    }

    public void setReceivers(Collection<PhoneNumber> receivers) {
        this.receivers = receivers;
    }

    public String getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getSent() {
        return sent;
    }

    public void setSent(Date sent) {
        this.sent = sent;
    }

    public Integer getTries() {
        return tries;
    }

    public void setTries(Integer tries) {
        this.tries = tries;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
