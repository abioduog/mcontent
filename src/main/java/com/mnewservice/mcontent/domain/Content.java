package com.mnewservice.mcontent.domain;

import com.mnewservice.mcontent.util.ContentUtils;

public class Content {

    private Long id;
    private String uuid;
    private String title;
    private String message;
    private String content;

    public String getContentUrl() {
        return ContentUtils.getContentUrl() + uuid;
    }

    public static String createContentImageUrl(String imagePath) {
        return ContentUtils.createContentImageUrl(imagePath);
    }

    public String getSmsMessageContent() {
        return message + getContentUrl();
    }

    // <editor-fold defaultstate="collapsed" desc="autogenerated getters / setters">

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    // </editor-fold>
}
