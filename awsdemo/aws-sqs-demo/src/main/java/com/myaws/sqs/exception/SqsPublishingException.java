package com.myaws.sqs.exception;

public class SqsPublishingException extends RuntimeException {

    public SqsPublishingException(String message, String eMessage) {
        super(message);
    }
}
