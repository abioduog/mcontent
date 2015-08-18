package com.mnewservice.mcontent.repository.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(name = "subscribers")
public class SubscriberEntity extends AbstractEntity {

    @OneToOne(cascade = CascadeType.ALL)
    private PhoneNumberEntity phone;

    public PhoneNumberEntity getPhone() {
        return phone;
    }

    public void setPhone(PhoneNumberEntity phone) {
        this.phone = phone;
    }
}
