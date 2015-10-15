package com.mnewservice.mcontent.repository.entity;

import javax.persistence.*;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(
        name = "custom_contents"
)
@DiscriminatorValue("CUSTOM")
public class CustomContentEntity extends AbstractContentEntity {

    private String title;

    @Column(length=Integer.MAX_VALUE)
    private String content; // html

    @Override
    public String getSummary() {
        // TODO: how to really get summary out of HTML page: page title? description?
        if(content == null) {
            return "";
        }
        return content.substring(
                0,
                Math.min(content.length(), MESSAGE_MAX_LENGTH)
        );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
