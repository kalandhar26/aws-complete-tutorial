package com.myaws;

import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;

/**
 * Hello world!
 */
public class MyAwsSecretsManagerService {

    public static void main(String[] args) {

        // Create Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .credentialsProvider(credentials())
                .region(Region.AP_SOUTH_1)
                .build();


        // Get secret value
        GetSecretValueRequest secretValueRequest = GetSecretValueRequest.builder()
                .secretId("MyProdSecret")
                .build();

        GetSecretValueResponse secretValueResponse = client.getSecretValue(secretValueRequest);
        String secretValue = secretValueResponse.secretString();
        System.out.println("secretValue: " + secretValue);

        // Parse JSON secret
        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(secretValue, Map.class);
        map.forEach((k, v) -> System.out.println("key: " + k + ", value: " + v));

        // Close the client
        client.close();
    }

    public static StaticCredentialsProvider credentials() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                        System.getenv("AWS_ACCESS_KEY_ID"),
                        System.getenv("AWS_SECRET_ACCESS_KEY"))
        );
    }
}
