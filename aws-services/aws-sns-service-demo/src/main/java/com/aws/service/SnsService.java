package com.aws.service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.DeleteTopicRequest;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;

public class SnsService {
    private final SnsClient snsClient;

    public SnsService() {
        snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    // Key functionality: Create an SNS topic
    public String createTopic(String topicName) {
        CreateTopicRequest request = CreateTopicRequest.builder()
                .name(topicName)
                .build();
        return snsClient.createTopic(request).topicArn();
    }

    // Key functionality: Publish a message to a topic
    public String publishMessage(String topicArn, String message) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)
                .build();
        return snsClient.publish(request).messageId();
    }

    // Key functionality: Subscribe to a topic (e.g., email endpoint)
    public String subscribeToTopic(String topicArn, String email) {
        SubscribeRequest request = SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol("email")
                .endpoint(email)
                .build();
        return snsClient.subscribe(request).subscriptionArn();
    }

    public void deleteTopic(String topicArn) {
        DeleteTopicRequest request = DeleteTopicRequest.builder()
                .topicArn(topicArn)
                .build();
        snsClient.deleteTopic(request);
    }
}