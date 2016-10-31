package com.mnewservice.mcontent.repository.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(
        name = "subscription_log",
        indexes = {
            @Index(columnList = "time_stamp"),
            @Index(columnList = "action")
        }
)
public class SubscriptionLogEntity extends AbstractEntity {

    public enum ActionTypeEnum {

        SUBSCRIPTION(Values.SUBSCRIPTION),
        RENEWAL(Values.RENEWAL),
        UNSUBSCRIPTION(Values.UNSUBSCRIPTION);

        private ActionTypeEnum(String value) {
            if (!this.name().equals(value)) {
                throw new IllegalArgumentException("Incorrect use");
            }
        }

        public static class Values {

            public static final String SUBSCRIPTION = "SUBSCRIPTION";
            public static final String RENEWAL = "RENEWAL";
            public static final String UNSUBSCRIPTION = "UNSUBSCRIPTION";

        }
    }

    @Column(name = "time_stamp")
    private Date timeStamp;

    @Column(name = "subscription_id")
    private Long subscriptionId;

    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "subscriber_id")
    private Long subscriberId;

    @Enumerated(EnumType.STRING)
    private ActionTypeEnum action;

//<editor-fold defaultstate="collapsed" desc="getters/setters">
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

    public ActionTypeEnum getAction() {
        return action;
    }

    public void setAction(ActionTypeEnum action) {
        this.action = action;
    }

//</editor-fold>
}
