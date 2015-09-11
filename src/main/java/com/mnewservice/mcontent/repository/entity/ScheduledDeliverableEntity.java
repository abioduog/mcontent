package com.mnewservice.mcontent.repository.entity;

import java.util.Date;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(name = "scheduled_deliverables")
@DiscriminatorValue(value = DeliveryPipeEntity.DeliverableTypeEnum.Values.SCHEDULED)
public class ScheduledDeliverableEntity extends AbstractDeliverableEntity {

    private Date deliveryDate;

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

}
