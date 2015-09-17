package com.mnewservice.mcontent.repository.entity;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(name = "contents")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "contentType")
public abstract class AbstractContentEntity extends AbstractEntity {

    protected static final int MESSAGE_MAX_LENGTH = 160;

    private String contentType;

    public abstract String getSummary();

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

}
