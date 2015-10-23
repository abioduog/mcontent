package com.mnewservice.mcontent.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduledDeliverable extends AbstractDeliverable {

    private Date deliveryDate;

    public ScheduledDeliverable() {
        super(DeliverableType.SCHEDULED);
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    //<editor-fold desc="Work around for the TimeZone issues of auto parsing that lives in some own timezone.">
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryDateString() {
        return new SimpleDateFormat("yyyy-MM-dd").format(deliveryDate);
    }

    public void setDeliveryDateString(String deliveryDateString) throws ParseException {
        this.deliveryDate = new SimpleDateFormat("yyyy-MM-dd").parse(deliveryDateString);
    }
    //</editor-fold>
}
