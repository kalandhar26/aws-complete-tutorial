package com.aws;

import com.aws.service.IamService;
import com.aws.service.LambdaService;
import com.aws.service.S3Service;
import com.aws.service.SecretsManagerService;

public class TestAwsServices {


    public static void main(String[] args) {

        IamService iamService = new IamService();
        LambdaService lambdaService = new LambdaService(iamService);
        S3Service s3Service = new S3Service(iamService);
        SecretsManagerService secretsManagerService = new SecretsManagerService(iamService);

        // Create a user
        String userArn = iamService.createUser("test-user");

        // Grant user access to an S3 bucket
        String bucketName = "my-example-bucket";
        s3Service.createBucket(bucketName);
        s3Service.grantUserBucketAccess("test-user", bucketName, false); // Use inline policy

        // Create a Lambda function with a role
        String functionArn = lambdaService.createFunction("myFunction", bucketName, "function.zip");

        // Store the user's access key in Secrets Manager
        String secretArn = secretsManagerService.storeAccessKeyAsSecret("test-user", "test-user-access-key");
        System.out.println("Secret ARN: " + secretArn);
        String secretValue = secretsManagerService.getSecretValue(secretArn);
        System.out.println("Secret Value: " + secretValue);

        // Cleanup (optional)
        secretsManagerService.deleteSecret(secretArn);
        iamService.deleteInlineUserPolicy("test-user", "S3BucketAccess-" + bucketName);
        iamService.deleteUser("test-user", null);
        lambdaService.deleteFunction("myFunction");
    }
}
