package com.cloudmanthan.aws.cleanup;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.DeleteFunctionRequest;
import com.amazonaws.services.lambda.model.FunctionConfiguration;
import com.amazonaws.services.lambda.model.ListFunctionsResult;
import com.cloudmanthan.aws.utils.CMDateUtils;

public class LambdaCleanup  {
	static Logger LOGGER = Logger.getLogger(RDSCleanup.class.getName());
	private static AWSLambda lambdaClient;
	static AWSCredentialsProvider awsCreds;
	static String profile = "amod_cmworkshop";
	
	public static void main(String[] args) {

		initialze();
		startCleanup();

	}


	private static void initialze() {
		 awsCreds = new ProfileCredentialsProvider(profile);
	}

	private static void startCleanup() {
		
		Set<String> regionSet = new HashSet<String>();
		regionSet.add("us-gov-west-1");
		regionSet.add("cn-north-1");
		regionSet.add("cn-northwest-1");
		regionSet.add("ap-south-1");
		
		// iterate over all regions

		for (Regions region : Regions.values()) {

			LOGGER.info("Name of Region is " + region.getName());

			String regionName = region.getName();
			
		
			if (regionSet.contains(regionName) == false) {
		               
				// String region = regions.g
				lambdaClient  = AWSLambdaClientBuilder.standard().withCredentials(awsCreds)
						.withRegion(region.getName()).build();
				
				ListFunctionsResult functionResult = lambdaClient.listFunctions();
				
				List<FunctionConfiguration> functionList = functionResult.getFunctions();
				
			
				for (FunctionConfiguration functionConfig : functionList) {

					String lastModifiedDate = functionConfig.getLastModified() ; 
					
					if ( true == CMDateUtils.isDateLaterthanWorkShopDate(lastModifiedDate)) {
						String name = functionConfig.getFunctionName();
						LOGGER.info("Deleting function with name : " +  name);
						
						DeleteFunctionRequest req = new DeleteFunctionRequest();
						req.setFunctionName(functionConfig.getFunctionName());
						
						lambdaClient.deleteFunction(req);
						
						LOGGER.info("Deleted function with name" + name );
					}
				}
			} // if region !=
			}
		} // for regions
	}


