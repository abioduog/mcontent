package com.mnewservice.mcontent.domain;

import com.mnewservice.mcontent.repository.entity.SubscriptionEntity;
import java.util.Date;

public class SubscriptionLog {

    private Long id;
    private Date timeStamp;
    private Long subscriptionId;
    private Long serviceId;
    private Long subscriberId;
    private SubscriptionLogAction action;

    public SubscriptionLog() {
    }

    public SubscriptionLog(SubscriptionEntity subscription, SubscriptionLogAction action) {
        this.action = action;
        this.timeStamp = new Date();
        this.subscriptionId = subscription.getId();
        this.serviceId = subscription.getService().getId();
        this.subscriberId = subscription.getSubscriber().getId();
    }

//<editor-fold defaultstate="collapsed" desc="getters/setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(Long subscriberId) {
        this.subscriberId = subscriberId;
    }

    public SubscriptionLogAction getAction() {
        return action;
    }

    public void setAction(SubscriptionLogAction action) {
        this.action = action;
    }

//</editor-fold>
}
