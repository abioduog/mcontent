package com.mnewservice.mcontent.domain;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public abstract class AbstractMessage {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
