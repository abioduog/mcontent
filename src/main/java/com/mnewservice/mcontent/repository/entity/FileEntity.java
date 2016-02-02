/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.repository.entity;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 *
 */

@Entity
@Table(
        name = "files",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"uuid"}
        )
)
public class FileEntity extends AbstractEntity {

    private UUID uuid;

    @Column(columnDefinition = "LONGBLOB")
    private byte[] thumbImage;

//    @Column(length = 256)
//    private String filename;

    @Column(length = 256)
    private String originalFilename;

    @Column(length = 512)
    private String path;

    private String mimeType;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public byte[] getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(byte[] thumbImage) {
        this.thumbImage = thumbImage;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
