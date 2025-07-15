package com.myaws;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerAsyncClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service class for asynchronously retrieving and parsing secrets from AWS Secrets Manager.
 * Uses AWS SDK v2 for non-blocking operations and Gson for JSON parsing.
 */
public class MyAwsSecretsManagerAsyncService {

    // JSON parser instance (thread-safe)
    private static final Gson gson = new Gson();

    // Type definition for Gson to parse JSON into Map<String, Object>
    private static final Type MAP_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();

    // Thread-safe AWS Secrets Manager async client
    private static SecretsManagerAsyncClient asyncClient;

    /**
     * Initializes the AWS Secrets Manager async client with hardcoded credentials.
     * Note: In production, use IAM roles or environment variables instead of hardcoded credentials.
     */
    public static void initClient() {
        asyncClient = SecretsManagerAsyncClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                System.getenv("AWS_ACCESS_KEY_ID"),
                                System.getenv("AWS_SECRET_ACCESS_KEY"))))
                .region(Region.AP_SOUTH_1)
                .build();
    }

    /**
     * Asynchronously retrieves a secret from AWS Secrets Manager
     *
     * @param secretId The secret identifier in AWS Secrets Manager
     * @return CompletableFuture containing the parsed secret as Map<String, Object>
     */
    public static CompletableFuture<Map<String, Object>> getSecretAsync(String secretId) {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretId)
                .build();

        return asyncClient.getSecretValue(request)
                .thenApply(response -> {
                    String secretString = response.secretString();
                    System.out.println("secretValue: " + secretString);
                    if (secretString == null) {
                        throw new IllegalStateException("Secret string is null");
                    }
                    return parseSecret(secretString);
                })
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        System.err.println("Error retrieving secret: " + ex.getMessage());
                    } else if (result != null) {
                        result.forEach((k, v) -> System.out.println("key: " + k + ", value: " + v));
                    }
                });
    }

    /**
     * Parses JSON secret string into a Map using Gson
     *
     * @param secretString JSON-formatted secret string
     * @return Parsed key-value pairs from the secret
     */
    public static Map<String, Object> parseSecret(String secretString) {
        return gson.fromJson(secretString, MAP_TYPE);
    }

    /**
     * Shuts down the AWS client to release resources
     */
    public static void shutdown() {
        if (asyncClient != null) {
            asyncClient.close();
        }
    }

    /**
     * Demonstration method for testing secret retrieval
     */
    public static void main(String[] args) {
        try {
            initClient();
            CompletableFuture<Map<String, Object>> secretFuture = getSecretAsync("MyProdSecret");

            // Block and wait for result (in production, use async callbacks)
            Map<String, Object> secret = secretFuture.join();
            System.out.println("Secret retrieval completed");
        } finally {
            shutdown();
        }
    }
}