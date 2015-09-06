package com.mnewservice.mcontent.domain;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class Subscriber {

    private Long id;
    private PhoneNumber phone;
    private int activeSubscriptionCount;
    private int inactiveSubscriptionCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PhoneNumber getPhone() {
        return phone;
    }

    public void setPhone(PhoneNumber phone) {
        this.phone = phone;
    }

    public int getActiveSubscriptionCount() {
        return activeSubscriptionCount;
    }

    public void setActiveSubscriptionCount(int activeSubscriptionCount) {
        this.activeSubscriptionCount = activeSubscriptionCount;
    }

    public int getInactiveSubscriptionCount() {
        return inactiveSubscriptionCount;
    }

    public void setInactiveSubscriptionCount(int inactiveSubscriptionCount) {
        this.inactiveSubscriptionCount = inactiveSubscriptionCount;
    }
}
