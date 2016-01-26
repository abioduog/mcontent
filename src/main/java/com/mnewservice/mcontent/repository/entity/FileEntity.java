/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.repository.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

/**
 *
 */

@Entity
public class FileEntity {

    public FileEntity(String filename, byte[] file, String mimeType) {
        this.file = file;
        this.filename = filename;
        this.mimeType = mimeType;
    }

    public FileEntity() {
        // Default Constructor
    }

    @Id
    private String filename;

    @Lob
    private byte[] file;

    private String mimeType;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
