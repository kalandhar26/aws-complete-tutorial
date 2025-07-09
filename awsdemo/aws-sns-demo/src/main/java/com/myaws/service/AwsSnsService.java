package com.myaws.service;

import com.myaws.exception.AwsSnsPublishException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SnsException;

@Service
@RequiredArgsConstructor
public class AwsSnsService {

    private static final Logger log = LoggerFactory.getLogger(AwsSnsService.class);

    private final SnsClient snsClient;

    @Value("${topic.arn}")
    private String topicArn;

    public void publishMessage(String subject, String message) {
        try {
            log.info("Publishing message to SNS topic: {}", topicArn);

            PublishRequest request = PublishRequest.builder()
                    .topicArn(topicArn)
                    .subject(subject)
                    .message(message)
                    .build();

            snsClient.publish(request);
            log.info("Message published successfully");

        } catch (SnsException e) {
            log.error("AWS SNS error occurred while publishing message: {}", e.getMessage());
            throw new AwsSnsPublishException("Failed to publish message to SNS", e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while publishing message: {}", e.getMessage());
            throw new AwsSnsPublishException("Unexpected error occurred while publishing message", e);
        }
    }
}