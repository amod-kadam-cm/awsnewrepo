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

public class AutoScalingCleanup extends ServiceCleanupBase implements ICleanup {

	static Logger LOGGER = Logger.getLogger(AutoScalingCleanup.class.getName());

	public void startCleanup(Calendar startCal, Calendar endCal) {
		super.startCleanup(startCal, endCal);

		Set<String> regionSet = new HashSet<String>();

		regionSet.add("us-gov-west-1");
		regionSet.add("cn-north-1");
		regionSet.add("cn-northwest-1");
		regionSet.add("ap-south-1");

		for (Regions region : Regions.values()) {

			LOGGER.info("Name of Region is " + region.getName());

			String regionName = region.getName();

			if (regionSet.contains(regionName) == false) {

				// initialize the client
				AmazonAutoScaling client =

						AmazonAutoScalingClientBuilder.standard().withCredentials(awsCredentialsProvider)
								.withRegion(region.getName()).build();

				DescribeAutoScalingGroupsResult groupResult = client.describeAutoScalingGroups();
				List<AutoScalingGroup> autoScalingGroups = groupResult.getAutoScalingGroups();

				Iterator<AutoScalingGroup> asgIterator = autoScalingGroups.iterator();
				
				while (asgIterator.hasNext()) {
					AutoScalingGroup asg = asgIterator.next();
					String asgName = asg.getAutoScalingGroupName();
					LOGGER.info("DELETING AutoScalingGroup with name : " + asgName );

					DeleteAutoScalingGroupRequest arg0 = new DeleteAutoScalingGroupRequest()
							.withAutoScalingGroupName(asgName).withForceDelete(true);
					client.deleteAutoScalingGroup(arg0);
					LOGGER.info("DELETED  AutoScalingGroup with name : " + asgName );

				}
			}
		} // for region

	}

}


