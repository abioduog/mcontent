package com.mnewservice.mcontent.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
@Entity
@Table(name = "binary_contents")
public class BinaryContentEntity extends AbstractEntity {

    private String name;
    private String contentType;
    @Column(columnDefinition = "LONGBLOB")
    private byte[] content;

// <editor-fold defaultstate="collapsed" desc="autogenerated getters / setters">
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
// </editor-fold>
}
