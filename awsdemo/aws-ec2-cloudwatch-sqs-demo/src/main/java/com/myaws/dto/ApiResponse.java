package com.myaws.dto;

public record ApiResponse<T>(String message, int status, T data) {}