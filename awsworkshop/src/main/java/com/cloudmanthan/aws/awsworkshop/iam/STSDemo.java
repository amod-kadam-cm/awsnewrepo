package com.cloudmanthan.aws.awsworkshop.iam;


import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.client.builder.AwsSyncClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;


public class STSDemo {

	public static void main(String[] args) {
		
		AWSSecurityTokenServiceClient sts_client = new AWSSecurityTokenServiceClient();
		
		sts_client.setEndpoint("sts-endpoint.amazonaws.com");
		
		GetSessionTokenRequest session_token_request = new GetSessionTokenRequest();
		session_token_request.setDurationSeconds(7200); // optional.
		
		
		GetSessionTokenResult session_token_result = sts_client.getSessionToken(session_token_request);
		
		Credentials session_creds = session_token_result.getCredentials();
		
		BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(
				   session_creds.getAccessKeyId(),
				   session_creds.getSecretAccessKey(),
				   session_creds.getSessionToken());

		AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
                .build();

}
}
