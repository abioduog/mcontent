package com.mnewservice.mcontent.repository.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(
        name = "custom_contents",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"shortUuid"}
        )
)
@DiscriminatorValue("CUSTOM")
public class CustomContentEntity extends AbstractContentEntity {

    private String shortUuid;
    private String content; // html

    @Override
    public String getSummary() {
        // TODO: how to get summary out of HTML page: page title? description?
        return content;
    }

    public String getShortUuid() {
        return shortUuid;
    }

    public void setShortUuid(String shortUuid) {
        this.shortUuid = shortUuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
