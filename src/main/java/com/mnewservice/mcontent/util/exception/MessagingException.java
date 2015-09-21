package com.mnewservice.mcontent.util.exception;

/**
 *
 * @author Marko Tuononen <marko.tuononen at nolwenture.com>
 */
public class MessagingException extends Exception {

    public MessagingException(String message) {
        super(message);
    }

    public MessagingException(Throwable cause) {
        super(cause);
    }

    public MessagingException(String message, Throwable cause) {
        super(message, cause);
    }
}
