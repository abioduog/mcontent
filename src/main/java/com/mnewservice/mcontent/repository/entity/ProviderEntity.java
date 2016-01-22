package com.mnewservice.mcontent.repository.entity;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(name = "providers")
public class ProviderEntity extends AbstractEntity {

    // ** basic info **
    private String name;

    // address
    private String state;
    private String country;

    @OneToOne(cascade = CascadeType.ALL)
    private PhoneNumberEntity phone;
    @OneToOne(cascade = CascadeType.ALL)
    private EmailEntity email;

    // ** login info **
    @OneToOne(cascade = CascadeType.ALL)
    private UserEntity user;

    // ** contact info **
    private String nameOfContactPerson;
    private String positionOfContactPerson;
    @OneToOne(cascade = CascadeType.ALL)
    private PhoneNumberEntity phoneOfContactPerson;
    @OneToOne(cascade = CascadeType.ALL)
    private EmailEntity emailOfContactPerson;

    // ** service info **
    private String contentName;
    private String contentDescription;

    // ** correspondence **
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<BinaryContentEntity> correspondences;

// <editor-fold defaultstate="collapsed" desc="autogenerated getters / setters">
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public PhoneNumberEntity getPhone() {
        return phone;
    }

    public void setPhone(PhoneNumberEntity phone) {
        this.phone = phone;
    }

    public EmailEntity getEmail() {
        return email;
    }

    public void setEmail(EmailEntity email) {
        this.email = email;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getNameOfContactPerson() {
        return nameOfContactPerson;
    }

    public void setNameOfContactPerson(String nameOfContactPerson) {
        this.nameOfContactPerson = nameOfContactPerson;
    }

    public String getPositionOfContactPerson() {
        return positionOfContactPerson;
    }

    public void setPositionOfContactPerson(String positionOfContactPerson) {
        this.positionOfContactPerson = positionOfContactPerson;
    }

    public PhoneNumberEntity getPhoneOfContactPerson() {
        return phoneOfContactPerson;
    }

    public void setPhoneOfContactPerson(PhoneNumberEntity phoneOfContactPerson) {
        this.phoneOfContactPerson = phoneOfContactPerson;
    }

    public EmailEntity getEmailOfContactPerson() {
        return emailOfContactPerson;
    }

    public void setEmailOfContactPerson(EmailEntity emailOfContactPerson) {
        this.emailOfContactPerson = emailOfContactPerson;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    public Set<BinaryContentEntity> getCorrespondences() {
        return correspondences;
    }

    public void setCorrespondences(Set<BinaryContentEntity> correspondences) {
        this.correspondences = correspondences;
    }
// </editor-fold>

}