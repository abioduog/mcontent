package com.mnewservice.mcontent.repository.entity;

import javax.persistence.*;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(name = "contents",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"shortUuid"}
        )
)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "contentType")
public abstract class AbstractContentEntity extends AbstractEntity {

    protected static final int MESSAGE_MAX_LENGTH = 160;

    @Column(updatable = false)
    private String shortUuid;

    public abstract String getSummary();

    public String getShortUuid() {
        return shortUuid;
    }

    public void setShortUuid(String shortUuid) {
        this.shortUuid = shortUuid;
    }
}
