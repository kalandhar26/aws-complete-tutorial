package com.aws.service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.rds.model.CreateDbInstanceRequest;
import software.amazon.awssdk.services.rds.model.DBInstance;
import software.amazon.awssdk.services.rds.model.DeleteDbInstanceRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesRequest;
import software.amazon.awssdk.services.rds.model.DescribeDbInstancesResponse;

import java.util.List;

public class RdsService {
    private final RdsClient rdsClient;

    public RdsService() {
        rdsClient = RdsClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    // Key functionality: Create a RDS DB instance
    public String createDbInstance(String dbInstanceIdentifier, String dbName, String masterUsername, String masterPassword) {
        CreateDbInstanceRequest request = CreateDbInstanceRequest.builder()
                .dbInstanceIdentifier(dbInstanceIdentifier)
                .dbName(dbName)
                .dbInstanceClass("db.t3.micro")
                .engine("mysql")
                .masterUsername(masterUsername)
                .masterUserPassword(masterPassword)
                .allocatedStorage(20)
                .build();
        return rdsClient.createDBInstance(request).dbInstance().dbInstanceArn();
    }

    // Key functionality: Describe (list) DB instances
    public List<String> describeDbInstances() {
        DescribeDbInstancesResponse response = rdsClient.describeDBInstances(DescribeDbInstancesRequest.builder().build());
        return response.dbInstances().stream()
                .map(DBInstance::dbInstanceIdentifier)
                .toList();
    }

    // Key functionality: Delete a DB instance
    public void deleteDbInstance(String dbInstanceIdentifier) {
        rdsClient.deleteDBInstance(DeleteDbInstanceRequest.builder()
                .dbInstanceIdentifier(dbInstanceIdentifier)
                .skipFinalSnapshot(true)
                .build());
    }
}