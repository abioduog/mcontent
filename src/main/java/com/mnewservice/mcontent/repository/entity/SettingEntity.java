package com.mnewservice.mcontent.repository.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(
        name = "settings",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"name"})
        }
)
public class SettingEntity extends AbstractEntity {

    public enum SettingNameEnum {

        DELIVERY_JOB_PAGE_SIZE,
        DELIVERY_JOB_SEND_SIZE,
        DELIVERY_JOB_EXPIRATION_LEAD
    }

    @Enumerated(EnumType.STRING)
    private SettingNameEnum name;
    private String value;

    public SettingNameEnum getName() {
        return name;
    }

    public void setName(SettingNameEnum name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
