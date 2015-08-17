package com.mnewservice.mcontent.repository.entity;

import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(name = "subscriptions")
public class SubscriptionEntity extends AbstractEntity {

    @Version
    private Long version;
    /*
     @ManyToOne
     private ServiceEntity service;

     @ManyToOne
     private PhoneNumberEntity subscriber;

     @OneToMany
     private Set<SubscriptionPeriodEntity> periods;
     */
}
