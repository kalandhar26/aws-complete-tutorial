package com.aws.service;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;
import software.amazon.awssdk.services.lambda.model.Runtime;

public class LambdaService {
    private final LambdaClient lambdaClient;
    private final IamService iamService;

    public LambdaService(IamService iamService) {
        this.lambdaClient = LambdaClient.builder()
                .region(Region.US_EAST_1)
                .build();
        this.iamService = iamService;
    }

    // Key functionality: Create a Lambda function (code from S3)
    public String createFunction(String functionName, String s3Bucket, String s3Key) {

        // Create a role for Lambda
        String trustPolicy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"Service\":\"lambda.amazonaws.com\"},\"Action\":\"sts:AssumeRole\"}]}";
        String roleArn = iamService.createRole("lambda-" + functionName + "-role", trustPolicy);

        // Attach inline policy for S3 and SQS access
        String policyDocument = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"s3:PutObject\",\"s3:GetObject\"],\"Resource\":\"*\"},{\"Effect\":\"Allow\",\"Action\":[\"sqs:SendMessage\",\"sqs:ReceiveMessage\"],\"Resource\":\"*\"}]}";
        iamService.attachInlineRolePolicy("lambda-" + functionName + "-role", "LambdaAccessPolicy", policyDocument);

        FunctionCode code = FunctionCode.builder()
                .s3Bucket(s3Bucket)
                .s3Key(s3Key)  // e.g., ZIP file key
                .build();
        CreateFunctionRequest request = CreateFunctionRequest.builder()
                .functionName(functionName)
                .runtime(Runtime.JAVA17)
                .role(roleArn)  // e.g., "arn:aws:iam::123456789012:role/lambda-role"
                .handler("com.example.Handler::handleRequest")
                .code(code)
                .build();
        return lambdaClient.createFunction(request).functionArn();
    }

    // Key functionality: Invoke a Lambda function
    public String invokeFunction(String functionName, String payload) {
        InvokeRequest request = InvokeRequest.builder()
                .functionName(functionName)
                .payload(SdkBytes.fromUtf8String(payload))  // JSON string
                .build();
        InvokeResponse response = lambdaClient.invoke(request);
        return response.payload().asUtf8String();
    }

    // Key functionality: Delete a Lambda function
    public void deleteFunction(String functionName) {
        lambdaClient.deleteFunction(DeleteFunctionRequest.builder()
                .functionName(functionName)
                .build());
    }
}
