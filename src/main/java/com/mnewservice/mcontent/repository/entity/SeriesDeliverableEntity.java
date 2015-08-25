package com.mnewservice.mcontent.repository.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(name = "series_deliverables")
@DiscriminatorValue("SERIES")
public class SeriesDeliverableEntity extends AbstractDeliverableEntity {

    private Integer deliveryDaysAfterSubscription;

    public Integer getDeliveryDaysAfterSubscription() {
        return deliveryDaysAfterSubscription;
    }

    public void setDeliveryDaysAfterSubscription(Integer deliveryDaysAfterSubscription) {
        this.deliveryDaysAfterSubscription = deliveryDaysAfterSubscription;
    }
}
