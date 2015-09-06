package com.mnewservice.mcontent.repository.entity;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(name = "subscribers")
public class SubscriberEntity extends AbstractEntity {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private PhoneNumberEntity phone;

    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<SubscriptionEntity> subscriptions;

    public PhoneNumberEntity getPhone() {
        return phone;
    }

    public void setPhone(PhoneNumberEntity phone) {
        this.phone = phone;
    }

    public Set<SubscriptionEntity> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<SubscriptionEntity> subscriptions) {
        this.subscriptions = subscriptions;
    }
}
