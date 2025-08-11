package com.aws.service;


import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;


import java.util.List;

@Service
public class Ec2Service {
    private final Ec2Client ec2Client;

    public Ec2Service() {
        ec2Client = Ec2Client.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    // Key functionality: Launch a new EC2 instance
    public String createInstance(String imageId, InstanceType instanceType) {
        RunInstancesRequest request = RunInstancesRequest.builder()
                .imageId(imageId)  // e.g., "ami-0abcdef1234567890"
                .instanceType(instanceType)
                .minCount(1)
                .maxCount(1)
                .build();
        RunInstancesResponse response = ec2Client.runInstances(request);
        return response.instances().getFirst().instanceId();
    }

    // Key functionality: Describe (list) instances
    public List<String> describeInstances() {
        DescribeInstancesResponse response = ec2Client.describeInstances(DescribeInstancesRequest.builder().build());
        return response.reservations().stream()
                .flatMap(res -> res.instances().stream())
                .map(Instance::instanceId)
                .toList();
    }

    // Key functionality: Terminate an instance
    public void terminateInstance(String instanceId) {
        ec2Client.terminateInstances(TerminateInstancesRequest.builder()
                .instanceIds(instanceId)
                .build());
    }
}
