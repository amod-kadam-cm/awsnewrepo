package com.cloudmanthan.aws.cleanup;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClientBuilder;
import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription;
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationRequest;
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationVersionRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationsResult;
import com.amazonaws.services.elasticbeanstalk.model.DescribeEnvironmentsResult;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription;

public class EBCleanup extends ServiceCleanupBase implements ICleanup {
	
	static Logger LOGGER = Logger.getLogger(EBCleanup.class.getName());

	public void startCleanup(Calendar startCal, Calendar endCal) {
		super.startCleanup(startCal, endCal);

		Set<String> regionSet = new HashSet<String>();

		regionSet.add("us-gov-west-1");
		regionSet.add("us-gov-east-1");
		regionSet.add("cn-north-1");
		regionSet.add("cn-northwest-1");
		regionSet.add("ap-south-1");
		regionSet.add("ap-east-1");

		for (Regions region : Regions.values()) {

			LOGGER.info("Name of Region is " + region.getName());

			String regionName = region.getName();

			if (regionSet.contains(regionName) == false) {

				// initialize the client
				AWSElasticBeanstalk client =

						AWSElasticBeanstalkClientBuilder.standard().withCredentials(awsCredentialsProvider)
								.withRegion(region.getName()).build();
				
				// process environments
				DescribeEnvironmentsResult environmentsResult  = client.describeEnvironments();
				
				List<EnvironmentDescription> envDescription = environmentsResult.getEnvironments();
				
			
				

				DescribeApplicationsResult groupResult = client.describeApplications();
				
				List<ApplicationDescription> applicationDescriptionList  = groupResult.getApplications();
				
				
				
				Iterator<ApplicationDescription> appIterator = applicationDescriptionList.iterator();
				
				while (appIterator.hasNext()) {
					
				
					ApplicationDescription appDescription  = appIterator.next();
					String applicationName  = appDescription.getApplicationName();
					DeleteApplicationVersionRequest appVersion = new DeleteApplicationVersionRequest().withApplicationName(applicationName) ;
					client.deleteApplicationVersion(appVersion );
				
					
					LOGGER.info("DELETING Application with name : " + applicationName );
					DeleteApplicationRequest arg0 = new DeleteApplicationRequest().withApplicationName(applicationName);
					client.deleteApplication(arg0);
					LOGGER.info("DELETED  Application with name : " + applicationName );

				}
			}
		} // for region

	}



}
