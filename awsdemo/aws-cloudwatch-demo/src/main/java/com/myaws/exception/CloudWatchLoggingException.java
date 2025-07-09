package com.myaws.exception;

public class CloudWatchLoggingException extends RuntimeException {

    public CloudWatchLoggingException(String message) {
        super(message);
    }

    public CloudWatchLoggingException(String message, Throwable cause) {
        super(message, cause);
    }
}
