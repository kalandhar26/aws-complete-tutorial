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
}
