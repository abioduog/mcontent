package com.mnewservice.mcontent.repository.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(
        name = "phonenumbers",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"number"}
        )
)
public class PhoneNumberEntity extends AbstractEntity {

    private String number;

    // <editor-fold defaultstate="collapsed" desc="autogenerated getters / setters">
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    // </editor-fold>

}
