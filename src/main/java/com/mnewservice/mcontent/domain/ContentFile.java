/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mnewservice.mcontent.domain;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import static org.apache.commons.lang3.StringEscapeUtils.escapeHtml4;

/**
 *
 */
public class ContentFile {

    private Long id;

    private UUID uuid;
    private byte[] thumbImage; // png format
//    private String filename;
    private String originalFilename;
    private String path;
    private String mimeType;

    // Only DTO level
    private String errorMessage;
    private boolean accepted;

    public ContentFile() {
        this.errorMessage = "";
        this.accepted = true;
        this.uuid = UUID.randomUUID();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public byte[] getThumbImage() {
        return thumbImage;
    }

    public String getThumbImageBase64String() {
        try {
            return new String(Base64.encodeBase64(thumbImage), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

    public String getImageHtmlBlock(String theme) {
        theme = theme.toLowerCase().trim();
        theme.replaceAll("/([\\t\\n\\r\\s])+/g", "-");
        theme.replaceAll("/([^A-Za-z0-9\\-])+/g", "");
        return "<div class=\"content-image " + (theme.length() > 0 ? "content-theme-" + theme : "") + "\">" // class = content-image [content-theme-{theme}]
                + "<img src=\"" + Content.getContentImageUrl(generateFilename()) + "\" alt=\"" + escapeHtml4(getOriginalFilename()) + "\" /></div>";
        // use Thumbimage and load it
        //return "<div class=\"content-" + theme + "-image\">"
        //        + "<img src=\"data:image/png;base64," + getThumbImageBase64String() + "\" alt=\"" + escapeHtml4(getOriginalFilename()) + "\" ></div>";
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String generateFilename() {
        String[] parts = this.getOriginalFilename().split("\\.");
        String name = uuid + "." + parts[parts.length - 1];
        try {
            return URLEncoder.encode(name, "UTF-8");
        } catch (Exception ex) {
            return name;
        }
    }
}
