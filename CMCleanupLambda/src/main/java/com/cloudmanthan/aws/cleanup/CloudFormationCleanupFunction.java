package com.cloudmanthan.aws.cleanup;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.DeleteStackRequest;
import com.amazonaws.services.cloudformation.model.ListStacksResult;
import com.amazonaws.services.cloudformation.model.StackSummary;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.cloudmanthan.aws.utils.CMDateUtils;

public class CloudFormationCleanupFunction implements RequestHandler<Object, String> {

	private AmazonCloudFormation cloudFormation;
	LambdaLogger LOGGER = null;

	@Override
	public String handleRequest(Object input, Context context) {

		LOGGER = context.getLogger();

		context.getLogger().log("Input: " + input);

		try {
			startCleanup();
		} catch (Exception e) {
			e.printStackTrace();
			String logMessage;
			logMessage = "Message is " + e.getMessage();
			context.getLogger().log(logMessage);
			context.getLogger().log("Stack trace is  " + e.getStackTrace().toString());
		}
		return "Hello from Lambda!";
	}

	private void startCleanup() {
		Set<String> regionSet = new HashSet<String>();
		regionSet.add("us-gov-west-1");
		regionSet.add("cn-north-1");
		regionSet.add("cn-northwest-1");

		// iterate over all regions except listed above as we don't have access to it.

		for (Regions region : Regions.values()) {

			LOGGER.log("Name of Region is " + region.getName());

			String regionName = region.getName();

			if (regionSet.contains(regionName) == false) {

				AWSCredentialsProvider credentialsProvider;

				initialze(regionName);

				ListStacksResult stackList = cloudFormation.listStacks();

				List<StackSummary> stackSummaries = stackList.getStackSummaries();

				for (StackSummary stackSummary : stackSummaries) {

					Date creatationTime = stackSummary.getCreationTime();

					String workShopStartDate = System.getenv("WORKSHOP_START_DATE"); // DD-MM-YYYY format

					String workShopEndDate = System.getenv("WORKSHOP_END_DATE"); // DD-MM-YYYY format

					if (workShopStartDate != null && workShopEndDate != null) {

						if (true == CMDateUtils.isDateWithinWorkShopDate(workShopStartDate, workShopEndDate,
								creatationTime)) {

							String stackName = stackSummary.getStackName();
							LOGGER.log("Deleting stack  with name : " + stackName);
							DeleteStackRequest req = new DeleteStackRequest().withStackName(stackName);
							cloudFormation.deleteStack(req);

							LOGGER.log("Deleted stack with name" + stackName);
						}

					} else {
						try {
							throw new Exception("Please specify WORKSHOP_START_DATE and WORKSHOP_END_DATE");
						} catch (Exception e) {
							LOGGER.log("Please specify WORKSHOP_START_DATE and WORKSHOP_END_DATE");
						}
					}
				}
			}

		}
	} // if region !=

	private void initialze(String regionName) {

		EnvironmentVariableCredentialsProvider envCredProvider = new EnvironmentVariableCredentialsProvider();

		AWSSecurityTokenService stsClient = AWSSecurityTokenServiceClientBuilder.standard()
				.withCredentials(envCredProvider).withRegion(regionName).build();

		AssumeRoleRequest req = new AssumeRoleRequest();

		String roleSessionName = "cleanuprole";

		String roleArn = null;

		roleArn = System.getenv("CM_MANAGEMENT_ROLE_ARN");

		req.setRoleArn(roleArn);

		req.setRoleSessionName(roleSessionName);

		AssumeRoleResult result = stsClient.assumeRole(req);

		Credentials worksShopCreds = result.getCredentials();

		worksShopCreds.getAccessKeyId();

		BasicSessionCredentials sessionCredentials = new BasicSessionCredentials(worksShopCreds.getAccessKeyId(),
				worksShopCreds.getSecretAccessKey(), worksShopCreds.getSessionToken());

		cloudFormation = AmazonCloudFormationClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(sessionCredentials)).withRegion(regionName).build();

	}

}
