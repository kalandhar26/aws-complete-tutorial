package com.aws;

import com.aws.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.model.InstanceType;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

public class AwsDemoWorkflow {
    private static final Logger log = LoggerFactory.getLogger(AwsDemoWorkflow.class);

    public static void main(String[] args) {
        // Initialize services
        IamService iamService = new IamService();
        LambdaService lambdaService = new LambdaService(iamService);
        S3Service s3Service = new S3Service(iamService);
        SecretsManagerService secretsManagerService = new SecretsManagerService(iamService);
        SqsService sqsService = new SqsService();
        SnsService snsService = new SnsService();
        Ec2Service ec2Service = new Ec2Service();
        RdsService rdsService = new RdsService();
        MskService mskService = new MskService();

        try {
            // Step 1: Create an IAM user
            String userName = "demo-user";
            String userArn = iamService.createUser(userName);
            System.out.println("Created IAM User: " + userArn);

            // Step 2: Create an S3 bucket and grant user access
            String bucketName = "demo-bucket-" + System.currentTimeMillis();
            s3Service.createBucket(bucketName);
            s3Service.grantUserBucketAccess(userName, bucketName, false); // Inline policy
            System.out.println("Created S3 Bucket: " + bucketName);

            // Step 3: Upload Lambda function code to S3
            String lambdaKey = "lambda/function.zip";
            s3Service.uploadObject(bucketName, lambdaKey, "/path/to/local/function.zip");
            System.out.println("Uploaded Lambda code to S3: " + lambdaKey);

            // Step 4: Store user access key in Secrets Manager
            String secretName = "demo-user-access-key";
            String secretArn = secretsManagerService.storeAccessKeyAsSecret(userName, secretName);
            System.out.println("Stored access key in Secrets Manager: " + secretArn);
            String secretValue = secretsManagerService.getSecretValue(secretArn);
            System.out.println("Retrieved secret: " + secretValue);

            // Step 5: Create a Lambda function with a role
            String functionName = "demo-function";
            String functionArn = lambdaService.createFunction(functionName, bucketName, lambdaKey);
            System.out.println("Created Lambda function: " + functionArn);

            // Step 6: Invoke the Lambda function
            String payload = "{\"key\":\"value\"}";
            String lambdaResult = lambdaService.invokeFunction(functionName, payload);
            System.out.println("Lambda invocation result: " + lambdaResult);

            // Step 7: Create an SQS queue and send a message
            String queueName = "demo-queue";
            String queueUrl = sqsService.createQueue(queueName);
            sqsService.sendMessage(queueUrl, "Hello from SQS!");
            List<Message> messages = sqsService.receiveMessages(queueUrl);
            System.out.println("Received SQS messages: " + messages);

            // Step 8: Create an SNS topic and publish a message
            String topicName = "demo-topic";
            String topicArn = snsService.createTopic(topicName);
            String messageId = snsService.publishMessage(topicArn, "Hello from SNS!");
            System.out.println("Published SNS message: " + messageId);

            // Step 9: Create an EC2 instance
            String instanceId = ec2Service.createInstance("ami-0abcdef1234567890", InstanceType.T2_MICRO);
            System.out.println("Created EC2 instance: " + instanceId);

            // Step 10: Create an RDS instance
            String dbInstanceId = "demo-db";
            String dbArn = rdsService.createDbInstance(dbInstanceId, "demo", "admin", "password123");
            System.out.println("Created RDS instance: " + dbArn);

            // Step 11: Create an MSK cluster
            String clusterName = "demo-cluster";
            String clusterArn = mskService.createCluster(clusterName);
            System.out.println("Created MSK cluster: " + clusterArn);

            // Cleanup
            System.out.println("Cleaning up resources...");
            mskService.deleteCluster(clusterArn);
            rdsService.deleteDbInstance(dbInstanceId);
            ec2Service.terminateInstance(instanceId);
            snsService.deleteTopic(topicArn); // Note: Add deleteTopic to SnsService
            sqsService.deleteQueue(queueUrl); // Note: Add deleteQueue to SqsService
            lambdaService.deleteFunction(functionName);
            secretsManagerService.deleteSecret(secretArn);
            iamService.deleteInlineUserPolicy(userName, "S3BucketAccess-" + bucketName);
            iamService.deleteUser(userName, null);
            s3Service.deleteBucket(bucketName); // Note: Add deleteBucket to S3Service
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }
}