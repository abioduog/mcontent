package com.mnewservice.mcontent.repository.entity;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(
        name = "subscriptions",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"service_id", "subscriber_id"}
        )
)
public class SubscriptionEntity extends AbstractEntity {

    @ManyToOne
    private ServiceEntity service;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private SubscriberEntity subscriber;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<SubscriptionPeriodEntity> periods;

    // <editor-fold defaultstate="collapsed" desc="autogenerated getters / setters">
    public ServiceEntity getService() {
        return service;
    }

    public void setService(ServiceEntity service) {
        this.service = service;
    }

    public SubscriberEntity getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(SubscriberEntity subscriber) {
        this.subscriber = subscriber;
    }

    public Set<SubscriptionPeriodEntity> getPeriods() {
        return periods;
    }

    public void setPeriods(Set<SubscriptionPeriodEntity> periods) {
        this.periods = periods;
    }
    // </editor-fold>
}
