package com.myaws.controller;

import com.myaws.service.AwsSnsService;
import com.myaws.exception.AwsSnsPublishException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/sns")
@RequiredArgsConstructor
public class AwsSnsController {

    private final AwsSnsService service;

    @PostMapping("/publish")
    public ResponseEntity<?> publishMessage(@RequestBody List<String> payload) {
        if (payload == null || payload.size() < 2) {
            return ResponseEntity.badRequest()
                    .body("Payload must contain subject and message");
        }

        try {
            service.publishMessage(payload.get(0), payload.get(1));
            return ResponseEntity.ok("Message successfully published");
        } catch (AwsSnsPublishException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Failed to publish message: " + e.getMessage());
        }
    }
}