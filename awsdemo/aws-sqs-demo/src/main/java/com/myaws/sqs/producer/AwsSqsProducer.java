package com.myaws.sqs.producer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.SendMessageRequest;

import com.myaws.sqs.exception.SqsPublishingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class AwsSqsProducer {

    private static final Logger logger = LoggerFactory.getLogger(AwsSqsProducer.class);

    private final AmazonSQSAsync amazonSQSAsync;
    private final String queueUrl;

    public AwsSqsProducer(
            AmazonSQSAsync amazonSQSAsync,
            @Value("${sqs.url}") String queueUrl) {
        this.amazonSQSAsync = amazonSQSAsync;
        this.queueUrl = queueUrl;
    }

    public void publishSqsMessage(String message) {
        try {
            SendMessageRequest request = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(message)
                    .withDelaySeconds(0); // Immediate delivery

            amazonSQSAsync.sendMessage(request);
            logger.info("Message sent to SQS queue: {}", queueUrl);

        } catch (AmazonClientException e) {
            logger.error("Failed to send message to SQS queue: {}", queueUrl, e);
            throw new SqsPublishingException("Failed to publish message to SQS: {}",e.getMessage());
        }
    }
}