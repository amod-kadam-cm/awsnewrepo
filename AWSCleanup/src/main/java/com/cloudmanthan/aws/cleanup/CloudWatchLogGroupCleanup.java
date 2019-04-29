package com.cloudmanthan.aws.cleanup;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.DeleteLogGroupRequest;
import com.amazonaws.services.logs.model.DescribeLogGroupsResult;
import com.amazonaws.services.logs.model.LogGroup;

public class CloudWatchLogGroupCleanup extends ServiceCleanupBase {

	Logger LOGGER = Logger.getLogger(CloudWatchLogGroupCleanup.class.getName());

	public void startCleanup(Calendar startCal, Calendar endCal) {

		// super.startCleanup(startCal, endCal);

		Set<String> regionSet = new HashSet<String>();

		regionSet.add("us-gov-west-1");
		regionSet.add("cn-north-1");
		regionSet.add("cn-northwest-1");

		// iterate for all regions

		for (Regions region : Regions.values()) {
			int i = 0;
			String regionName = region.getName();

			if (regionSet.contains(regionName) == false) {

				AWSCredentialsProvider awsCreds = new ProfileCredentialsProvider(profile);

				AWSLogs logsClient = AWSLogsClientBuilder.standard().withCredentials(awsCreds).withRegion(region)
						.build();
			
				boolean hasNextToken = true;
				DescribeLogGroupsResult result = null;
				while (hasNextToken) {
					 result = logsClient.describeLogGroups();

					String nextToken = result.getNextToken();

					if (nextToken != null) {
						if (nextToken.isEmpty() == true) {
							hasNextToken = true;
						} else {
							hasNextToken = false;
						} 
					}else {
							hasNextToken = false ;
						}
					}

					List<LogGroup> list = result.getLogGroups();

					for (LogGroup logGroup : list) {
						LOGGER.log(Level.INFO, "Deleting Log group name from region - count " + regionName + ++i
								+ logGroup.getLogGroupName());
						DeleteLogGroupRequest arg0 = new DeleteLogGroupRequest()
								.withLogGroupName(logGroup.getLogGroupName());
						logsClient.deleteLogGroup(arg0);
						LOGGER.log(Level.INFO, "DELETED  Log group name from region " + regionName + " count " + ++i
								+ logGroup.getLogGroupName());
					} // for

				} // if regionSet
			}
		}
	} //


