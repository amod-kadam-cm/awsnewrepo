package com.cloudmanthan.aws.cleanup;

import java.util.List;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AccessKeyMetadata;
import com.amazonaws.services.identitymanagement.model.AttachedPolicy;
import com.amazonaws.services.identitymanagement.model.DeleteAccessKeyRequest;
import com.amazonaws.services.identitymanagement.model.DeleteLoginProfileRequest;
import com.amazonaws.services.identitymanagement.model.DeleteUserRequest;
import com.amazonaws.services.identitymanagement.model.DetachUserPolicyRequest;
import com.amazonaws.services.identitymanagement.model.GetUserPolicyRequest;
import com.amazonaws.services.identitymanagement.model.GetUserPolicyResult;
import com.amazonaws.services.identitymanagement.model.ListAccessKeysRequest;
import com.amazonaws.services.identitymanagement.model.ListAccessKeysResult;
import com.amazonaws.services.identitymanagement.model.ListAttachedUserPoliciesRequest;
import com.amazonaws.services.identitymanagement.model.ListAttachedUserPoliciesResult;
import com.amazonaws.services.identitymanagement.model.ListUserPoliciesRequest;
import com.amazonaws.services.identitymanagement.model.ListUserPoliciesResult;
import com.amazonaws.services.identitymanagement.model.ListUsersResult;
import com.amazonaws.services.identitymanagement.model.User;

public class IAMCleanup {
	
	static AmazonIdentityManagement iamClient = null;
	
	static String userName = null;
	
	static String clientInitials = "JD"; // John Deere
	
	static int EQUAL = 0;
	
	static Logger LOGGER = Logger.getLogger(ELBCleanup.class.getName());
	

	public static void main(String[] args) {
		
		initClient();	
		cleanup(clientInitials);
	}

	private static void initClient() {
		
		String profile = "amod_cmworkshop";
		AWSCredentialsProvider awsCreds = new ProfileCredentialsProvider(profile);	
		iamClient = AmazonIdentityManagementClientBuilder.standard().withCredentials(awsCreds).build();
	
	}

	/*
	 * 1) Deletes the user which contains the specific character sequence. This should be the client initials.
	 * For e.g. for John Deere it is JD. 
	 * 2) Users access keys are  deleted first before deleting user otherwise it throws exception
	 * 3) Also detachAnyPolicies associated with user before deleting user otherwise it throws exception
	 * "com.amazonaws.services.identitymanagement.model.DeleteConflictException: Cannot delete entity, must detach all policies first"
	 * 
	 */
	private static void cleanup(String clientInitials) {
		
		ListUsersResult listUsers = iamClient.listUsers();
		
		List<User> list = listUsers.getUsers();
		
		for (User iamUser: list ) {
			userName = iamUser.getUserName();
		
			String compareUserName = userName.toLowerCase();
			clientInitials = clientInitials.toLowerCase();
			if ( compareUserName.contains(clientInitials) ) {
				
					LOGGER.info("Found User with" +  iamUser.getUserName());
					
					ListAccessKeysRequest listAccessKeyRequest = new ListAccessKeysRequest().withUserName(userName);
					
					ListAccessKeysResult result = iamClient.listAccessKeys(listAccessKeyRequest);
					
					List<AccessKeyMetadata> accessKeyMetadataList = result.getAccessKeyMetadata();
					
					for (AccessKeyMetadata accessKeyMetadata: accessKeyMetadataList ) {
						String accessKeyId = accessKeyMetadata.getAccessKeyId();	
						
						if (null  != accessKeyId ) {
							
							DeleteAccessKeyRequest arg0 = new DeleteAccessKeyRequest()
									.withUserName(userName)
									.withAccessKeyId(accessKeyId);
							
							LOGGER.info("Deleting Access Key for user  " + userName);
							
							
							iamClient.deleteAccessKey(arg0 );
							LOGGER.info("DELETED  Access Key for user  " + userName);
							
							
						}
					}
				
					detachUserPolicies();
											
					DeleteUserRequest request = new DeleteUserRequest().withUserName(userName);
					
					DeleteLoginProfileRequest deleteLoginProfileRequest = new DeleteLoginProfileRequest().withUserName(userName);
					iamClient.deleteLoginProfile(deleteLoginProfileRequest);
					iamClient.deleteUser(request);
				}
			
			}
		
	}

	private static void detachUserPolicies() {
		ListAttachedUserPoliciesRequest listAttachedUserPoliciesRequest = new ListAttachedUserPoliciesRequest().withUserName(userName);
		
		ListAttachedUserPoliciesResult userPoliciesResult = iamClient.listAttachedUserPolicies(listAttachedUserPoliciesRequest);
		
		List<AttachedPolicy> attachedPolicyList = userPoliciesResult.getAttachedPolicies();
		
		for (AttachedPolicy attachedPolicy : attachedPolicyList) {
			
			String policyArn = attachedPolicy.getPolicyArn();

			DetachUserPolicyRequest detachUserPolicyRequest = new DetachUserPolicyRequest()
					.withUserName(userName)
					.withPolicyArn(policyArn) ;
			iamClient.detachUserPolicy(detachUserPolicyRequest );
			
		}
	
	}

}
