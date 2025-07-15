package com.myaws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;

public class S3EventHandler implements RequestHandler<S3Event, String> {

    @Override
    public String handleRequest(S3Event event, Context context) {
        // Get the first record from the S3 event
        S3EventNotification.S3EventNotificationRecord record = event.getRecords().getFirst();

        // Get bucket name
        String bucketName = record.getS3().getBucket().getName();
        System.out.println("Bucket name is: " + bucketName);

        // Get file name (object key)
        String fileName = record.getS3().getObject().getKey();

        if (fileName.contains("success")) {
            System.out.println("Inside success case");
            return "Your event is triggered. Check Lambda function logs for more details";
        } else {
            System.out.println("Inside failure case");
            throw new RuntimeException("Throwing exception to check failure case");
        }
    }
}