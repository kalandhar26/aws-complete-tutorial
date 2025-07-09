package com.myaws.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Service class that communicates with S3 using AWS SDK v2
 */
@Service
@RequiredArgsConstructor
public class FileStore {

    private static final Logger logger = LogManager.getLogger(FileStore.class);

    private final S3Client s3Client;

    public String createBucket(String bucketName) {
        logger.info("Creating bucket: {}", bucketName);
        try {
            if (bucketAlreadyExists(bucketName)) {
                logger.warn("Bucket {} already exists", bucketName);
                return "Bucket already exists: " + bucketName;
            }

            s3Client.createBucket(CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build());

            s3Client.putBucketVersioning(PutBucketVersioningRequest.builder()
                    .bucket(bucketName)
                    .versioningConfiguration(VersioningConfiguration.builder()
                            .status(BucketVersioningStatus.ENABLED)
                            .build())
                    .build());

            logger.info("Bucket created with versioning enabled: {}", bucketName);
            return "Bucket created with name: " + bucketName + " (versioning enabled)";

        } catch (S3Exception e) {
            logger.error("Unable to create bucket {}: {}", bucketName, e.getMessage());
            throw new RuntimeException("Failed to create bucket: " + e.getMessage(), e);
        }
    }

    private boolean bucketAlreadyExists(String bucketName) {
        logger.info("Checking if bucket exists: {}", bucketName);
        try {
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.headBucket(headBucketRequest);
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        } catch (S3Exception e) {
            logger.error("Error checking bucket existence: {}", e.getMessage());
            throw new RuntimeException("Failed to check bucket existence", e);
        }
    }

    public void uploadFileToBucket(MultipartFile multipartFile, String bucketName) throws IOException {
        logger.info("Uploading file to bucket: {}", bucketName);
        Path tempFilePath = Path.of(System.getProperty("java.io.tmpdir"), Objects.requireNonNull(multipartFile.getOriginalFilename()));
        multipartFile.transferTo(tempFilePath);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(tempFilePath.getFileName().toString())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromFile(tempFilePath));
            logger.info("File uploaded successfully to bucket: {}", bucketName);
        } catch (S3Exception e) {
            logger.error("Unable to upload file: {}", e.getMessage());
            throw new RuntimeException("Failed to upload file", e);
        } finally {
            // Clean up the temporary file
            java.nio.file.Files.deleteIfExists(tempFilePath);
        }
    }

    public void deleteBucket(String bucketName) {
        logger.info("Deleting bucket: {}", bucketName);
        try {
            // First empty the bucket
            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectsV2Response listObjectsResponse;
            do {
                listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

                for (S3Object s3Object : listObjectsResponse.contents()) {
                    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(s3Object.key())
                            .build();
                    s3Client.deleteObject(deleteObjectRequest);
                }
                listObjectsRequest = ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .continuationToken(listObjectsResponse.continuationToken())
                        .build();
            } while (listObjectsResponse.isTruncated());

            // Then delete the bucket
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.deleteBucket(deleteBucketRequest);
            logger.info("Bucket deleted successfully: {}", bucketName);
        } catch (S3Exception e) {
            logger.error("Unable to delete bucket {}: {}", bucketName, e.getMessage());
            throw new RuntimeException("Failed to delete bucket", e);
        }
    }

    public void deleteBucketVersion(String bucketName) {
        logger.info("Deleting bucket versions: {}", bucketName);
        try {
            // First, delete all object versions
            ListObjectVersionsRequest listVersionsRequest = ListObjectVersionsRequest.builder()
                    .bucket(bucketName)
                    .build();

            ListObjectVersionsResponse listVersionsResponse;
            do {
                listVersionsResponse = s3Client.listObjectVersions(listVersionsRequest);

                // Delete all versions for each object
                for (ObjectVersion version : listVersionsResponse.versions()) {
                    DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(version.key())
                            .versionId(version.versionId())
                            .build();
                    s3Client.deleteObject(deleteRequest);
                }

                // Delete all delete markers (if any)
                for (DeleteMarkerEntry marker : listVersionsResponse.deleteMarkers()) {
                    DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(marker.key())
                            .versionId(marker.versionId())
                            .build();
                    s3Client.deleteObject(deleteRequest);
                }

                // Set up for next page if results are truncated
                if (listVersionsResponse.isTruncated()) {
                    listVersionsRequest = ListObjectVersionsRequest.builder()
                            .bucket(bucketName)
                            .keyMarker(listVersionsResponse.nextKeyMarker())
                            .versionIdMarker(listVersionsResponse.nextVersionIdMarker())
                            .build();
                }

            } while (listVersionsResponse.isTruncated());

            // Now delete the empty bucket
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.deleteBucket(deleteBucketRequest);
            logger.info("Bucket deleted versions successfully: {}", bucketName);
        } catch (S3Exception e) {
            logger.error("Unable to delete bucket version{}: {}", bucketName, e.getMessage());
            throw new RuntimeException("Failed to delete bucket", e);
        }
    }

    public void deleteFile(String bucketName, String fileName) {
        logger.info("Deleting file {} from bucket {}", fileName, bucketName);
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            logger.info("File deleted successfully: {} from bucket {}", fileName, bucketName);
        } catch (S3Exception e) {
            logger.error("Unable to delete file {}: {}", fileName, e.getMessage());
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    /**
     * Delete Specific Version of a File
     */
    public void deleteFileVersion(String bucketName, String fileName, String versionId) {
        logger.info("Deleting version {} of file {} from bucket {}", versionId, fileName, bucketName);
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .versionId(versionId)
                    .build();

            s3Client.deleteObject(deleteRequest);
            logger.info("Version {} of file {} deleted successfully", versionId, fileName);
        } catch (S3Exception e) {
            logger.error("Unable to delete version {} of file {}: {}", versionId, fileName, e.getMessage());
            throw new RuntimeException("Failed to delete file version", e);
        }
    }

    /**
     * Delete Specific Version of a File
     */
    public void deleteAllFileVersions(String bucketName, String fileName) {
        logger.info("Deleting all versions of file {} from bucket {}", fileName, bucketName);
        try {
            // List all versions of the file
            ListObjectVersionsRequest listRequest = ListObjectVersionsRequest.builder()
                    .bucket(bucketName)
                    .prefix(fileName)  // Filter by this file name
                    .build();

            ListObjectVersionsResponse listResponse = s3Client.listObjectVersions(listRequest);

            // Delete all versions
            for (ObjectVersion version : listResponse.versions()) {
                if (version.key().equals(fileName)) {
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .versionId(version.versionId())
                            .build());
                }
            }

            // Delete all delete markers
            for (DeleteMarkerEntry marker : listResponse.deleteMarkers()) {
                if (marker.key().equals(fileName)) {
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .versionId(marker.versionId())
                            .build());
                }
            }

            logger.info("All versions of file {} deleted successfully", fileName);
        } catch (S3Exception e) {
            logger.error("Unable to delete all versions of file {}: {}", fileName, e.getMessage());
            throw new RuntimeException("Failed to delete all file versions", e);
        }
    }
}
