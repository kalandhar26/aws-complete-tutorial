package com.aws.service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

public class SqsService {
    private final SqsClient sqsClient;

    public SqsService() {
        sqsClient = SqsClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    // Key functionality: Create an SQS queue
    public String createQueue(String queueName) {
        CreateQueueRequest request = CreateQueueRequest.builder()
                .queueName(queueName)
                .build();
        return sqsClient.createQueue(request).queueUrl();
    }

    // Key functionality: Send a message to a queue
    public String sendMessage(String queueUrl, String messageBody) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();
        return sqsClient.sendMessage(request).messageId();
    }

    // Key functionality: Receive messages from a queue
    public List<Message> receiveMessages(String queueUrl) {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .build();
        return sqsClient.receiveMessage(request).messages();
    }

    // New functionality: Delete an SQS queue
    public void deleteQueue(String queueUrl) {
        DeleteQueueRequest request = DeleteQueueRequest.builder()
                .queueUrl(queueUrl)
                .build();
        sqsClient.deleteQueue(request);
    }
}