package com.mnewservice.mcontent.domain;

import java.util.Date;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class SubscriptionPeriod {

    private Long id;
    private Date start;
    private Date end;

    // additional info
    private String message;
    private int shortCode;
    private String operator;
    private String originalTimeStamp;
    private String messageId;
    private String sender;

    // <editor-fold defaultstate="collapsed" desc="autogenerated getters / setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getShortCode() {
        return shortCode;
    }

    public void setShortCode(int shortCode) {
        this.shortCode = shortCode;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOriginalTimeStamp() {
        return originalTimeStamp;
    }

    public void setOriginalTimeStamp(String originalTimeStamp) {
        this.originalTimeStamp = originalTimeStamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    // </editor-fold>
}
