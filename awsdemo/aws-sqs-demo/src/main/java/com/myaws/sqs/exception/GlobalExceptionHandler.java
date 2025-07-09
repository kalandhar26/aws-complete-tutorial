package com.myaws.sqs.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SqsPublishingException.class)
    public ResponseEntity<String> handleSqsPublishingException(SqsPublishingException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> handleGlobalException(Exception ex){

        Map<String,String> errors = new HashMap<>();

        errors.put("error", "An unexpected error occurred");
        errors.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);

    }
}
