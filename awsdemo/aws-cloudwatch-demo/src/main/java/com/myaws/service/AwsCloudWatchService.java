package com.myaws.service;

import com.myaws.exception.CloudWatchLoggingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsCloudWatchService {

    private final CloudWatchLogsClient cloudWatchLogsClient;

    @Value("${log.group.name}")
    private String logGroupName;

    @Value("${log.stream.name}")
    private String logStreamName;

    /**
     * Method that logs message to CloudWatch using existing LogGroupName and LogStreamName
     *
     * @param message The message to be logged
     */
    public void logMessageToCloudWatch(String message) {
        try {
            InputLogEvent logEvent = InputLogEvent.builder()
                    .timestamp(Instant.now().toEpochMilli())
                    .message(message)
                    .build();

            List<InputLogEvent> logEvents = new ArrayList<>();
            logEvents.add(logEvent);

            String sequenceToken = getSequenceToken();

            PutLogEventsRequest.Builder requestBuilder = PutLogEventsRequest.builder()
                    .logGroupName(logGroupName)
                    .logStreamName(logStreamName)
                    .logEvents(logEvents);

            if (sequenceToken != null) {
                requestBuilder.sequenceToken(sequenceToken);
            }

            cloudWatchLogsClient.putLogEvents(requestBuilder.build());
            log.info("Successfully logged message to CloudWatch");
        } catch (CloudWatchLogsException e) {
            log.error("Error logging to CloudWatch: {}", e.getMessage(), e);
            throw new CloudWatchLoggingException("Failed to log message to CloudWatch", e);
        }
    }

    /**
     * Retrieves the sequence token for the log stream
     *
     * @return The sequence token, or null if not found
     */
    private String getSequenceToken() {
        try {
            DescribeLogStreamsRequest request = DescribeLogStreamsRequest.builder()
                    .logGroupName(logGroupName)
                    .logStreamNamePrefix(logStreamName)
                    .limit(1)
                    .build();

            DescribeLogStreamsResponse response = cloudWatchLogsClient.describeLogStreams(request);

            if (!response.logStreams().isEmpty()) {
                return response.logStreams().getFirst().uploadSequenceToken();
            }
            return null;
        } catch (CloudWatchLogsException e) {
            log.error("Error getting sequence token: {}", e.getMessage(), e);
            return null;
        }
    }
}

