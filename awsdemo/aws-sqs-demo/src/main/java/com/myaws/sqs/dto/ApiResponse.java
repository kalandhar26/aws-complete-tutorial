package com.myaws.sqs.dto;

public record ApiResponse<T>(String message, int status, T data) {}