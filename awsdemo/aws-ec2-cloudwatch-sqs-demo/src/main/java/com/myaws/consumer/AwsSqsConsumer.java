package com.myaws.consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import io.awspring.cloud.sqs.annotation.SqsListener;

@Service
public class AwsSqsConsumer {

    Logger logger = LogManager.getLogger(this.getClass().getName());

    @SqsListener(value = "testsqskalandhar")
    public void consumeSqsMessages(String message) {
        logger.info("message received from queue /'testsqskalandhar/' and message {}", message);
        System.out.println("Message received:" + message);
    }
}
