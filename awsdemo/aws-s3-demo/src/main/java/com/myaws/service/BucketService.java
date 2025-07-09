package com.myaws.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class BucketService {

    private static final Logger logger = LogManager.getLogger(BucketService.class);


    private final FileStore fileStore;


    private S3Client s3Client;

    /**
     * Downloads file from S3 and logs content to CloudWatch
     * @param fileName Name of the file to download
     * @param bucketName Name of the S3 bucket
     */
    public void downloadFile(String fileName, String bucketName) {
        try {
            logger.info("Fetching file from S3: {}", fileName);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            try (ResponseInputStream<GetObjectResponse> responseInputStream =
                         s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream())) {

                String content = new String(responseInputStream.readAllBytes(), StandardCharsets.UTF_8);
                logger.info("File content: {}", content);
            }
        } catch (IOException e) {
            logger.error("Error reading file content: {}", e.getMessage());
            throw new RuntimeException("Failed to read file content", e);
        } catch (S3Exception e) {
            logger.error("S3 error occurred: {}", e.getMessage());
            throw new RuntimeException("Failed to download file from S3", e);
        }
    }

    /**
     * Creates a new S3 bucket
     * @param bucketName Name of the bucket to create
     * @return Status message
     */
    public String createBucket(String bucketName) {
        return fileStore.createBucket(bucketName);
    }

    /**
     * Uploads a file to S3 bucket
     * @param file Multipart file to upload
     * @param bucketName Target bucket name
     * @return Status message
     */
    public String uploadFile(MultipartFile file, String bucketName) {
        if (file.isEmpty()) {
            throw new IllegalStateException("Cannot upload empty file");
        }
        try {
            fileStore.uploadFileToBucket(file, bucketName);
            return "File uploaded successfully";
        } catch (Exception e) {
            logger.error("Failed to upload file: {}", e.getMessage());
            throw new IllegalStateException("Failed to upload file", e);
        }
    }

    /**
     * Deletes an S3 bucket
     * @param bucketName Name of the bucket to delete
     * @return Status message
     */
    public String deleteBucket(String bucketName) {
        fileStore.deleteBucketVersion(bucketName);
        return "Bucket deleted successfully";
    }

    /**
     * Deletes a file from S3 bucket
     * @param bucketName Name of the bucket
     * @param fileName Name of the file to delete
     * @return Status message
     */
    public String deleteFile(String bucketName, String fileName) {
        fileStore.deleteFile(bucketName, fileName);
        return "File deleted successfully";
    }
}