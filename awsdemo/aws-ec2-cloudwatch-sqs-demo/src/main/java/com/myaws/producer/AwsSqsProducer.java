package com.myaws.producer;


import com.myaws.exception.SqsPublishingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;


@Service
public class AwsSqsProducer {

    private static final Logger logger = LoggerFactory.getLogger(AwsSqsProducer.class);

    private final SqsAsyncClient sqsAsyncClient;
    private final String queueUrl;

    public AwsSqsProducer(
            SqsAsyncClient sqsAsyncClient,
            @Value("${sqs.url}") String queueUrl) {
        this.sqsAsyncClient = sqsAsyncClient;
        this.queueUrl = queueUrl;
    }

    public void publishSqsMessage(String message) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();

        sqsAsyncClient.sendMessage(request)
                .thenAccept(response -> logger.info("Message sent to SQS: {}", queueUrl))
                .exceptionally(e -> {
                    logger.error("Failed to send message to SQS: {}", queueUrl, e);
                    throw new SqsPublishingException("Failed to publish message: " + e.getMessage());
                });
    }
}