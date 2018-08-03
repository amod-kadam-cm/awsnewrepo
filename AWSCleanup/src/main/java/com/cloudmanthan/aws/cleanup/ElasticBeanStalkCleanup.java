package com.cloudmanthan.aws.AWSCleanup;

import java.util.List;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.DescribeAddressesResult;
import com.amazonaws.services.ec2.model.ReleaseAddressRequest;
import com.amazonaws.services.ec2.model.ReleaseAddressResult;

import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClientBuilder;
import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription;
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationRequest;
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationResult;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationsResult;

public class ElasticBeanStalkCleanup {
	static Logger LOGGER = Logger.getLogger(ElasticBeanStalkCleanup.class.getName());
	private static AWSElasticBeanstalk ebClient;

	public static void main(String[] args) {

		startCleanup();

	}


	private static void startCleanup() {

		String profile = "amod_cmworkshop";
		AWSCredentialsProvider awsCreds = new ProfileCredentialsProvider(profile);

		// iterate for all regions

		for (Regions region : Regions.values()) {

			LOGGER.info("Name of Region is " + region.getName());

			String regionName = region.getName();
			
			if ( regionName.equals("us-gov-west-1") == false || regionName.equals("cn-north-1") )  {
			// Don't do any operations on gov_cloud
			if (regionName != "us-gov-west-1" || regionName != "cn-north-1") {
				               
				// String region = regions.g
				ebClient = AWSElasticBeanstalkClientBuilder.standard().withCredentials(awsCreds)
						.withRegion(region.getName()).build();

				DescribeApplicationsRequest request = new DescribeApplicationsRequest();
				DescribeApplicationsResult response = ebClient.describeApplications(request);

				List<ApplicationDescription> appDescriptions = response.getApplications();

				for (ApplicationDescription appDescription : appDescriptions) {

					String appName = appDescription.getApplicationName();

					LOGGER.info("Deleting app " + appName);

					DeleteApplicationRequest delebrequest = new DeleteApplicationRequest()
							.withTerminateEnvByForce(true)
							.withApplicationName(appName);
					DeleteApplicationResult delebresponse = ebClient.deleteApplication(delebrequest);
						
				}

			} // if region !=
			}
		} // for regions
	}

}
