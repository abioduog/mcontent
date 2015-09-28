package com.mnewservice.mcontent.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ScheduledDeliverable extends AbstractDeliverable {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date deliveryDate;

    public ScheduledDeliverable() {
        super(DeliverableType.SCHEDULED);
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
