package com.myaws.controller;

import com.myaws.service.AwsCloudWatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.cloudwatchlogs.model.CloudWatchLogsException;

@RestController
@RequestMapping("/api/logs")
public class AwsCloudWatchController {

    private final AwsCloudWatchService cloudWatchService;

    public AwsCloudWatchController(AwsCloudWatchService cloudWatchService) {
        this.cloudWatchService = cloudWatchService;
    }

    @PostMapping("/publish/{message}")
    public ResponseEntity<?> logMessageToCloudWatch(@PathVariable("message") String message) {
        try {
            cloudWatchService.logMessageToCloudWatch(message);
            return ResponseEntity.ok("Message successfully logged to CloudWatch");
        } catch (CloudWatchLogsException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Failed to log to CloudWatch: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }
}