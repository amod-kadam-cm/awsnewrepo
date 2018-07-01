package com.cloudmanthan.aws.AWSCleanup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Address;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DeleteKeyPairResult;
import com.amazonaws.services.ec2.model.DescribeAddressesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsRequest;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceExportDetails;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.ModifyInstanceAttributeRequest;
import com.amazonaws.services.ec2.model.ReleaseAddressRequest;
import com.amazonaws.services.ec2.model.ReleaseAddressResult;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.elasticache.AmazonElastiCache;
import com.amazonaws.services.elasticache.AmazonElastiCacheClient;
import com.amazonaws.services.elasticache.AmazonElastiCacheClientBuilder;
import com.amazonaws.services.elasticache.model.CacheCluster;
import com.amazonaws.services.elasticache.model.DeleteCacheClusterRequest;
import com.amazonaws.services.elasticache.model.DeleteReplicationGroupRequest;
import com.amazonaws.services.elasticache.model.DescribeCacheClustersResult;
import com.amazonaws.services.elasticache.model.InvalidCacheClusterStateException;
import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription;
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationRequest;
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationResult;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationsResult;

public class EC2Cleanup {
	static Logger LOGGER = Logger.getLogger(EC2Cleanup.class.getName());
	private static AmazonEC2 ec2Client;

	public static void main(String[] args) {

		startCleanup();

	}

	private static void startCleanup() {

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

			// if ( regionName.equals("us-gov-west-1") == false ||
			// regionName.equals("cn-north-1") == false ) {

			// if ( regionName.compareTo("us-gov-west-1") != 0 ) {
			if (regionSet.contains(regionName) == false) {
				;
				// String region = regions.g
				ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(awsCreds).withRegion(region.getName())
						.build();
				DescribeKeyPairsResult response = ec2Client.describeKeyPairs();

				for (KeyPairInfo key_pair : response.getKeyPairs()) {
					System.out.printf("Found key pair with name %s " + "and fingerprint %s", key_pair.getKeyName(),
							key_pair.getKeyFingerprint());

					String keypairName = key_pair.getKeyName();

					if ((keypairName.equalsIgnoreCase("SONALI-TEST-AP-MUM-KEY-PAIR2") == false
							|| keypairName.equalsIgnoreCase("CM-WORKSHOP-KP") == false)) {

						DeleteKeyPairResult result = ec2Client
								.deleteKeyPair(new DeleteKeyPairRequest(key_pair.getKeyName()));
					}
				}

				// now cleanup instances which are created on specific dates

				DescribeInstancesResult ec2InstancesResult = ec2Client.describeInstances();

				List<Reservation> reservations = ec2InstancesResult.getReservations();

				for (Reservation reservation : reservations) {

					List<Instance> instances = reservation.getInstances();

					for (Instance instance : instances) {

						Date launchDate = instance.getLaunchTime();

						String instanceId = instance.getInstanceId();
						Calendar workShopcal = Calendar.getInstance();
						// TODO : Parameterize it to accept the from date and todate
						workShopcal.set(2018, 5, 26);

						Calendar launchCal = Calendar.getInstance();

						launchCal.setTime(launchDate);

						LOGGER.info(
								"Instance with instance id " + instance.getInstanceId() + " launched on " + launchDate);

						if (launchCal.after(workShopcal)) {
							LOGGER.info("JD workshInstance with instance id " + instance.getInstanceId()
									+ " launched on " + launchDate);

							if (true == Arrays.asList("running", "pending", "stopping", "stopped")
									.contains(instance.getState().getName())) {

								ModifyInstanceAttributeRequest modRequest = new ModifyInstanceAttributeRequest();

								modRequest.setInstanceId(instanceId);
								modRequest.withDisableApiTermination(false);

								ec2Client.modifyInstanceAttribute(modRequest);

								TerminateInstancesRequest request = new TerminateInstancesRequest();

								List<String> instanceIds = new ArrayList<String>();

								instanceIds.add(instance.getInstanceId());

								request.setInstanceIds(instanceIds);

								ec2Client.terminateInstances(request);
							}

						}
					}

				}

			} // if region !=
		}
	} // for regions
}
