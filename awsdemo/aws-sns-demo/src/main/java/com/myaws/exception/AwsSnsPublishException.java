package com.myaws.exception;

public class AwsSnsPublishException extends RuntimeException {
    public AwsSnsPublishException(String message) {
        super(message);
    }

    public AwsSnsPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}