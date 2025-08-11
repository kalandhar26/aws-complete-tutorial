package com.aws.service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kafka.KafkaClient;
import software.amazon.awssdk.services.kafka.model.*;

import java.util.List;

public class MskService {
    private final KafkaClient kafkaClient;

    public MskService() {
        kafkaClient = KafkaClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    // Key functionality: Create an MSK cluster
    public String createCluster(String clusterName) {
        BrokerNodeGroupInfo brokerNodeGroupInfo = BrokerNodeGroupInfo.builder()
                .instanceType("kafka.m5.large")
                .clientSubnets(List.of("subnet-123", "subnet-456"))  // Replace with actual subnet IDs
                .securityGroups(List.of("sg-123"))  // Replace with actual security group
                .build();

        CreateClusterRequest request = CreateClusterRequest.builder()
                .clusterName(clusterName)
                .kafkaVersion("3.5.1")
                .numberOfBrokerNodes(3)
                .brokerNodeGroupInfo(brokerNodeGroupInfo)
                .clientAuthentication(ClientAuthentication.builder().build())  // Default
                .encryptionInfo(EncryptionInfo.builder().build())  // Default
                .build();
        return kafkaClient.createCluster(request).clusterArn();
    }

    // Key functionality: Describe a cluster
    public String describeCluster(String clusterArn) {
        DescribeClusterRequest request = DescribeClusterRequest.builder()
                .clusterArn(clusterArn)
                .build();
        DescribeClusterResponse response = kafkaClient.describeCluster(request);
        return response.clusterInfo().stateAsString();
    }

    // Key functionality: Delete a cluster
    public void deleteCluster(String clusterArn) {
        DeleteClusterRequest request = DeleteClusterRequest.builder()
                .clusterArn(clusterArn)
                .build();
        kafkaClient.deleteCluster(request);
    }
}