package com.mnewservice.mcontent.repository.entity;

import java.net.URI;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(name = "rss_contents")
@DiscriminatorValue("RSS")
public class RssContentEntity extends AbstractContentEntity {

    private String title;
    private String description;
    private String link;
    private static final String SUMMARY_TEMPLATE = "%s: %s (%s)";

    @Override
    public String getSummary() {
        int lengthWithoutDescription = String.format(
                SUMMARY_TEMPLATE,
                title,
                "",
                link
        ).length();

        int descriptionMaxLength
                = (MESSAGE_MAX_LENGTH - lengthWithoutDescription > 0)
                        ? MESSAGE_MAX_LENGTH - lengthWithoutDescription
                        : 0;

        return String.format(
                SUMMARY_TEMPLATE,
                title,
                description.substring(
                        0,
                        Math.min(description.length(), descriptionMaxLength)
                ),
                link
        );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
