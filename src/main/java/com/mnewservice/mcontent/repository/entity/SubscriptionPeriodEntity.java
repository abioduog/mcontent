package com.mnewservice.mcontent.repository.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(
        name = "subscriptionperiods",
        indexes = {
            @Index(columnList = "end"),
            @Index(columnList = "start, end")
        }
)
public class SubscriptionPeriodEntity extends AbstractEntity {

    private Date start;
    private Date end;
    // additional info
    private String message;
    private int shortCode;
    private String operator;
    private String originalTimestamp;
    private Long messageId;
    private String sender;

    // <editor-fold defaultstate="collapsed" desc="autogenerated getters / setters">
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

    public String getOriginalTimestamp() {
        return originalTimestamp;
    }

    public void setOriginalTimestamp(String originalTimestamp) {
        this.originalTimestamp = originalTimestamp;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
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
