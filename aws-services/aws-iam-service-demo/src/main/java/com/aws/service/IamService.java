package com.aws.service;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.*;

import java.util.List;

public class IamService {
    private final IamClient iamClient;

    public IamService() {
        iamClient = IamClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }

    // Key functionality: Create an IAM user
    public String createUser(String userName) {
        CreateUserRequest request = CreateUserRequest.builder()
                .userName(userName)
                .build();
        CreateUserResponse response = iamClient.createUser(request);
        return response.user().arn();
    }

    // Key functionality: Attach a managed policy to a user
    public void attachUserPolicy(String userName, String policyArn) {
        AttachUserPolicyRequest request = AttachUserPolicyRequest.builder()
                .userName(userName)
                .policyArn(policyArn) // e.g., "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
                .build();
        iamClient.attachUserPolicy(request);
    }

    // Key functionality: List IAM users
    public List<String> listUsers() {
        ListUsersRequest request = ListUsersRequest.builder()
                .maxItems(10)
                .build();
        ListUsersResponse response = iamClient.listUsers(request);
        return response.users().stream()
                .map(User::userName)
                .toList();
    }

    // Key functionality: Delete an IAM user (detach policies first)
    public void deleteUser(String userName, String policyArn) {
        // Detach policy before deleting user
        if (policyArn != null) {
            DetachUserPolicyRequest detachRequest = DetachUserPolicyRequest.builder()
                    .userName(userName)
                    .policyArn(policyArn)
                    .build();
            iamClient.detachUserPolicy(detachRequest);
        }
        // Delete the user
        DeleteUserRequest deleteRequest = DeleteUserRequest.builder()
                .userName(userName)
                .build();
        iamClient.deleteUser(deleteRequest);
    }

    // New functionality: Create an IAM role with a trust policy (e.g., for Lambda)
    public String createRole(String roleName, String trustPolicyJson) {
        CreateRoleRequest request = CreateRoleRequest.builder()
                .roleName(roleName)
                .assumeRolePolicyDocument(trustPolicyJson) // JSON trust policy
                .build();
        CreateRoleResponse response = iamClient.createRole(request);
        return response.role().arn();
    }

    // New functionality: Attach an inline policy to a user
    public void attachInlineUserPolicy(String userName, String policyName, String policyDocumentJson) {
        PutUserPolicyRequest request = PutUserPolicyRequest.builder()
                .userName(userName)
                .policyName(policyName)
                .policyDocument(policyDocumentJson) // JSON policy document
                .build();
        iamClient.putUserPolicy(request);
    }

    // New functionality: Attach an inline policy to a role
    public void attachInlineRolePolicy(String roleName, String policyName, String policyDocumentJson) {
        PutRolePolicyRequest request = PutRolePolicyRequest.builder()
                .roleName(roleName)
                .policyName(policyName)
                .policyDocument(policyDocumentJson) // JSON policy document
                .build();
        iamClient.putRolePolicy(request);
    }

    // New functionality: Create an access key for a user
    public String createAccessKey(String userName) {
        CreateAccessKeyRequest request = CreateAccessKeyRequest.builder()
                .userName(userName)
                .build();
        CreateAccessKeyResponse response = iamClient.createAccessKey(request);
        return response.accessKey().accessKeyId() + ":" + response.accessKey().secretAccessKey();
    }

    // New functionality: List access keys for a user
    public List<String> listAccessKeys(String userName) {
        ListAccessKeysRequest request = ListAccessKeysRequest.builder()
                .userName(userName)
                .build();
        ListAccessKeysResponse response = iamClient.listAccessKeys(request);
        return response.accessKeyMetadata().stream()
                .map(metadata -> metadata.accessKeyId())
                .toList();
    }

    // New functionality: Delete an access key for a user
    public void deleteAccessKey(String userName, String accessKeyId) {
        DeleteAccessKeyRequest request = DeleteAccessKeyRequest.builder()
                .userName(userName)
                .accessKeyId(accessKeyId)
                .build();
        iamClient.deleteAccessKey(request);
    }

    // New functionality: Delete an inline policy from a user
    public void deleteInlineUserPolicy(String userName, String policyName) {
        DeleteUserPolicyRequest request = DeleteUserPolicyRequest.builder()
                .userName(userName)
                .policyName(policyName)
                .build();
        iamClient.deleteUserPolicy(request);
    }
}