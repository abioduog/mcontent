package com.mnewservice.mcontent.domain;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class Subscriber {

    private Long id;
    private PhoneNumber phone;

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

}
