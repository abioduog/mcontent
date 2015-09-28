package com.mnewservice.mcontent.domain;

public class SeriesDeliverable extends AbstractDeliverable {

    private Integer deliveryDaysAfterSubscription;

    public SeriesDeliverable() {
        super(DeliverableType.SERIES);
    }

    public Integer getDeliveryDaysAfterSubscription() {
        return deliveryDaysAfterSubscription;
    }

    public void setDeliveryDaysAfterSubscription(Integer deliveryDaysAfterSubscription) {
        this.deliveryDaysAfterSubscription = deliveryDaysAfterSubscription;
    }

}
