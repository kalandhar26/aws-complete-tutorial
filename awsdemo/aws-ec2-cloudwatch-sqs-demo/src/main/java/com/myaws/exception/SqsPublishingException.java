package com.myaws.exception;

public class SqsPublishingException extends RuntimeException {

    // Standard constructor with message
    public SqsPublishingException(String message) {
        super(message);
    }

    // Constructor with message and cause
    public SqsPublishingException(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor with cause only
    public SqsPublishingException(Throwable cause) {
        super(cause);
    }

    // Your existing format-supporting constructor
    public SqsPublishingException(String message, String eMessage) {
        super(String.format(message, eMessage));
    }

    // Additional constructor with format args
    public SqsPublishingException(String format, Object... args) {
        super(String.format(format, args));
    }
}