package com.myaws.controller;

import com.myaws.dto.ApiResponse;
import com.myaws.dto.MessageDTO;
import com.myaws.exception.SqsPublishingException;
import com.myaws.producer.AwsSqsProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/sqs")
@RequiredArgsConstructor
public class TestAwsSqsController {
    private static final Logger logger = LoggerFactory.getLogger(TestAwsSqsController.class);

    private final AwsSqsProducer producer;

    @PostMapping("/publish")
    public ResponseEntity<ApiResponse<String>> publishMessage(@RequestBody MessageDTO messageDto) {
        try {
            logger.info("Received request to publish message: {}", messageDto.message());
            producer.publishSqsMessage(messageDto.message());

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            "Message published successfully",
                            HttpStatus.OK.value(),
                            messageDto.message()
                    )
            );

        } catch (SqsPublishingException e) {
            logger.error("SQS publishing failed for message: {}", messageDto.message(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ApiResponse<>(
                            "Message publishing failed",
                            HttpStatus.SERVICE_UNAVAILABLE.value(),
                            null
                    ));
        }
    }

}