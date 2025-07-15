package com.myaws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class SNSEventHandler implements RequestHandler<SNSEvent, String> {

    @Override
    public String handleRequest(SNSEvent event, Context context) {
        try {
            if (event.getRecords().isEmpty()) {
                throw new IllegalArgumentException("No records found in SNS event");
            }

            SNSEvent.SNSRecord snsRecord = event.getRecords().getFirst();

            // Log all available information
            context.getLogger().log("Received SNS Event:");
            context.getLogger().log("Message ID: " + snsRecord.getSNS().getMessageId());
            context.getLogger().log("Timestamp: " + snsRecord.getSNS().getTimestamp());
            context.getLogger().log("Topic ARN: " + snsRecord.getSNS().getTopicArn());
            context.getLogger().log("Subject: " + snsRecord.getSNS().getSubject());
            context.getLogger().log("Message: " + snsRecord.getSNS().getMessage());

            // Process the message (add your business logic here)

            return "Successfully processed SNS message: " + snsRecord.getSNS().getMessageId();

        } catch (Exception e) {
            context.getLogger().log("Error processing SNS event: " + e.getMessage());
            throw new RuntimeException("Failed to process SNS event", e);
        }
    }
}