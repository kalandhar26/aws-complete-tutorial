package com.aws.service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.DeleteSecretRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class SecretsManagerService {

    private final SecretsManagerClient secretsManagerClient;
    private final IamService iamService;

    public SecretsManagerService(IamService iamService) {
        this.secretsManagerClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        this.iamService = iamService;
    }


    // Key functionality: Create a secret
    public String createSecret(String secretName, String secretValue) {
        CreateSecretRequest request = CreateSecretRequest.builder()
                .name(secretName)
                .secretString(secretValue)  // e.g., JSON string
                .build();
        return secretsManagerClient.createSecret(request).arn();
    }

    // Key functionality: Retrieve a secret value
    public String getSecretValue(String secretId) {
        GetSecretValueRequest request = GetSecretValueRequest.builder()
                .secretId(secretId)
                .build();
        GetSecretValueResponse response = secretsManagerClient.getSecretValue(request);
        return response.secretString();
    }

    // New functionality: Store IAM user access key as a secret
    public String storeAccessKeyAsSecret(String userName, String secretName) {
        String accessKey = iamService.createAccessKey(userName);
        String secretValue = "{\"accessKeyId\":\"" + accessKey.split(":")[0] + "\",\"secretAccessKey\":\"" + accessKey.split(":")[1] + "\"}";
        return createSecret(secretName, secretValue);
    }

    // Key functionality: Delete a secret
    public void deleteSecret(String secretId) {
        DeleteSecretRequest request = DeleteSecretRequest.builder()
                .secretId(secretId)
                .forceDeleteWithoutRecovery(true)
                .build();
        secretsManagerClient.deleteSecret(request);
    }
}