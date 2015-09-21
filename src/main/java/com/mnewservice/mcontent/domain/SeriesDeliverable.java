package com.mnewservice.mcontent.domain;

public class SeriesDeliverable {

    private Long id;
    private Content content;
    private Integer deliveryDaysAfterSubscription;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Integer getDeliveryDaysAfterSubscription() {
        return deliveryDaysAfterSubscription;
    }

    public void setDeliveryDaysAfterSubscription(Integer deliveryDaysAfterSubscription) {
        this.deliveryDaysAfterSubscription = deliveryDaysAfterSubscription;
    }

}
