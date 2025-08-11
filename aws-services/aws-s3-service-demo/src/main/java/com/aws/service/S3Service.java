package com.aws.service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.file.Paths;

public class S3Service {
    private final S3Client s3Client;
    private final IamService iamService;

    public S3Service(IamService iamService) {
        this.s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .build();
        this.iamService = iamService;
    }

    // Key functionality: Create an S3 bucket
    public String createBucket(String bucketName) {
        CreateBucketRequest request = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build();
        return s3Client.createBucket(request).location();
    }

    // Key functionality: Upload an object to S3
    public void uploadObject(String bucketName, String key, String filePath) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.putObject(request, Paths.get(filePath));
    }

    // Key functionality: Download an object from S3
    public void downloadObject(String bucketName, String key, String downloadPath) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3Client.getObject(request, Paths.get(downloadPath));
    }

    // New functionality: Grant user access to a specific bucket
    public void grantUserBucketAccess(String userName, String bucketName, boolean useManagedPolicy) {
        if (useManagedPolicy) {
            // Attach AWS managed policy for S3 full access
            iamService.attachUserPolicy(userName, "arn:aws:iam::aws:policy/AmazonS3FullAccess");
        } else {
            // Attach inline policy for specific bucket access
            String policyDocument = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Action\":[\"s3:GetObject\",\"s3:PutObject\"],\"Resource\":\"arn:aws:s3:::" + bucketName + "/*\"}]}";
            iamService.attachInlineUserPolicy(userName, "S3BucketAccess-" + bucketName, policyDocument);
        }
    }

    // Key functionality: Delete an S3 bucket
    public void deleteBucket(String bucketName) {
        DeleteBucketRequest request = DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build();
        s3Client.deleteBucket(request);
    }
}
