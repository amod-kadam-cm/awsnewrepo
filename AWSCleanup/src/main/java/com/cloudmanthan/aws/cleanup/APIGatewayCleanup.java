package com.cloudmanthan.aws.cleanup;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.DeleteRestApiRequest;
import com.amazonaws.services.apigateway.model.GetRestApisRequest;
import com.amazonaws.services.apigateway.model.GetRestApisResult;
import com.amazonaws.services.apigateway.model.RestApi;
import com.cloudmanthan.aws.utils.CMDateUtils;

public class APIGatewayCleanup extends ServiceCleanupBase implements ICleanup {

	static Logger LOGGER = Logger.getLogger(APIGatewayCleanup.class.getName());

	public void startCleanup(Calendar startCal, Calendar endCal) {
		// initialize base class variables
		super.startCleanup(startCal, endCal);

		startCleanup();
	}

	public void startCleanup() {

		String profile = "amod_cmworkshop";
		AWSCredentialsProvider awsCreds = new ProfileCredentialsProvider(profile);
		Set<String> regionSet = new HashSet<String>();

		regionSet.add("us-gov-west-1");
		regionSet.add("cn-north-1");
		regionSet.add("cn-northwest-1");

		// iterate for all regions

		for (Regions region : Regions.values()) {

			LOGGER.info("Name of Region is " + region.getName());

			String regionName = region.getName();

			if (regionSet.contains(regionName) == false) {

				AmazonApiGateway client = AmazonApiGatewayClientBuilder.standard().withCredentials(awsCreds)
						.withRegion(region.getName()).build();

				GetRestApisRequest arg0 = new GetRestApisRequest();
				GetRestApisResult result = client.getRestApis(arg0);

				List<RestApi> apiList = result.getItems();

				for (RestApi restAPI : apiList) {
					LOGGER.log(Level.INFO, "Got the rest api" + restAPI.getId());

					String restApiId = restAPI.getId();
					try {
						Thread.sleep(30000);

						Date createdDate = restAPI.getCreatedDate();

						if (CMDateUtils.isDateWithinWorkShopDate(super.workShopCal, super.workShopendCal,
								createdDate) == true) {
							
							LOGGER.log(Level.INFO, "Deleting  rest api" + restAPI.getId());

							DeleteRestApiRequest delReq = new DeleteRestApiRequest().withRestApiId(restApiId);
							client.deleteRestApi(delReq);
							LOGGER.log(Level.INFO, "DELETED  rest api" + restAPI.getId());
						}

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			} // if region !=
		}
	}

}
