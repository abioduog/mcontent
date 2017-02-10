package com.mnewservice.mcontent.repository.entity;

import com.mnewservice.mcontent.domain.PhoneNumber;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by Antti Vikman on 6.3.2016.
 */
@Entity
@Table(
        name = "sms_messages"
)
public class SmsMessageEntity extends AbstractEntity {

    private String fromNumber;
    private String message;
    private String receivers;

    @Column(nullable = false, updatable = false)
    private Date created;
    private Date sent;
    private Integer tries;
    @Column(columnDefinition = "LONGTEXT")
    private String log;

    @PrePersist
    protected void onCreate() {
        created = new Date();
    }

    public String getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Collection<PhoneNumber> getReceivers() {
        return Arrays.asList(receivers.split(",")).stream().map(x -> new PhoneNumber(x)).collect(Collectors.toList());
    }

    public void setReceivers(Collection<PhoneNumber> receivers) {
        this.receivers = receivers.stream().map(x -> x.getNumber()).collect(Collectors.joining(","));
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

    public void incrementTries() {
        this.tries++;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public void log(String logMessage) {
        this.log = (log == null ? new Date().toString() + ": " + logMessage : log + "\n" + new Date().toString() + ": " + logMessage);
    }

}
