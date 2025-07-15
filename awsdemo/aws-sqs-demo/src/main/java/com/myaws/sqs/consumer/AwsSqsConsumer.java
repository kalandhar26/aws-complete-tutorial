package com.myaws.sqs.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AwsSqsConsumer {

    @SqsListener(value = "testsqskalandhar", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void consumeSqsMessages(String message) {
        log.info("message received from queue /'testsqskalandhar/' and message{}", message);
        System.out.println("Message received:" + message);
    }

    // Lamda Trigger Destination Example
    @SqsListener(value = "success-queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void mySuccessConsumer(String message) {
        log.info("message received from queue /'success-queue/' and message{}", message);
        System.out.println("Success Message received:" + message);
    }

    // Lamda Trigger Destination Example
    @SqsListener(value = "failure-queue", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void myFailureConsumer(String message) {
        log.info("message received from queue /'failure-queue/' and message{}", message);
        System.out.println("Failed Message received:" + message);
    }
}
