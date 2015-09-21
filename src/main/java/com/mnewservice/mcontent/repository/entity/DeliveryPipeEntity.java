package com.mnewservice.mcontent.repository.entity;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(name = "delivery_pipes")
public class DeliveryPipeEntity extends AbstractEntity { // aka Devil Pipe

    public enum DeliverableTypeEnum {

        SERIES(Values.SERIES),
        SCHEDULED(Values.SCHEDULED);

        private DeliverableTypeEnum(String value) {
            if (!this.name().equals(value)) {
                throw new IllegalArgumentException("Incorrect use");
            }
        }

        public static class Values {

            public static final String SERIES = "SERIES";
            public static final String SCHEDULED = "SCHEDULED";
        }
    }

    private String name;

    @Enumerated(EnumType.STRING)
    private DeliverableTypeEnum deliverableType;

    @ManyToMany
    private Set<UserEntity> providers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeliverableTypeEnum getDeliverableType() {
        return deliverableType;
    }

    public void setDeliverableType(DeliverableTypeEnum deliverableType) {
        this.deliverableType = deliverableType;
    }

    public Set<UserEntity> getProviders() {
        return providers;
    }

    public void setProviders(Set<UserEntity> providers) {
        this.providers = providers;
    }

}
