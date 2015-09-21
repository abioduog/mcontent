package com.mnewservice.mcontent.domain;

import java.util.Collection;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class DeliveryPipe {

    private Long id;

    private String name;
    private DeliverableType deliverableType;
    private Collection<User> providers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeliverableType getDeliverableType() {
        return deliverableType;
    }

    public void setDeliverableType(DeliverableType deliverableType) {
        this.deliverableType = deliverableType;
    }

    public Collection<User> getProviders() {
        return providers;
    }

    public void setProviders(Collection<User> providers) {
        this.providers = providers;
    }
}
