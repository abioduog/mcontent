package com.mnewservice.mcontent.domain;

import java.util.ArrayList;
import java.util.Collection;
import javax.validation.constraints.Size;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class Provider {

    private Long id;
    @Size(min=2, max=60)
    private String name;
    private String address;
    private String zipcode;
    private String city;
    private String state;
    private String country;
    private String phone;
    private String email;
    private String fax;
    private String companyname;
    private User user;
    private String nameOfContactPerson;
    private String positionOfContactPerson;
    private String phoneOfContactPerson;
    private String emailOfContactPerson;
    private String contentName;
    private String contentDescription;
    private Collection<BinaryContent> correspondences;

    public Provider() {
//        phone = new PhoneNumber();
//        email = new Email();
        user = new User();
//        phoneOfContactPerson = new PhoneNumber();
//        emailOfContactPerson = new Email();
        correspondences = new ArrayList<>();
    }

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
    
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }
    
    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
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

    public String getPhoneOfContactPerson() {
        return phoneOfContactPerson;
    }

    public void setPhoneOfContactPerson(String phoneOfContactPerson) {
        this.phoneOfContactPerson = phoneOfContactPerson;
    }

    public String getEmailOfContactPerson() {
        return emailOfContactPerson;
    }

    public void setEmailOfContactPerson(String emailOfContactPerson) {
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

    public Collection<BinaryContent> getCorrespondences() {
        return correspondences;
    }

    public void setCorrespondences(Collection<BinaryContent> correspondences) {
        this.correspondences = correspondences;
    }
}
