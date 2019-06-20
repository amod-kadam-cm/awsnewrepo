package com.cloudmanthan.aws.cleanup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DeleteKeyPairRequest;
import com.amazonaws.services.ec2.model.DeleteKeyPairResult;
import com.amazonaws.services.ec2.model.DeleteSnapshotRequest;
import com.amazonaws.services.ec2.model.DeleteVolumeRequest;
import com.amazonaws.services.ec2.model.DeleteVpcRequest;
import com.amazonaws.services.ec2.model.DeregisterImageRequest;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSnapshotsRequest;
import com.amazonaws.services.ec2.model.DescribeVolumesRequest;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.ModifyInstanceAttributeRequest;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Snapshot;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.Volume;
import com.amazonaws.services.ec2.model.Vpc;
import com.cloudmanthan.aws.utils.CMDateUtils;

public class EC2Cleanup extends ServiceCleanupBase {

	static Logger LOGGER = Logger.getLogger(EC2Cleanup.class.getName());
	private static AmazonEC2 ec2Client;

	private static CharSequence clientInitials = "ge";

	static WorkshopCalendar workshopCalendar;

	static Calendar workShopCal = null;

	public EC2Cleanup(Calendar startCal, Calendar endCal) {
		super.startCleanup(startCal, endCal);

		// workShopCal = Calendar.getInstance();
		// workShopCal.clear();
		// workShopCal.set(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONTH),
		// startCal.get(Calendar.DAY_OF_MONTH));
	}

	private static void cleanupAMIS() {

		DescribeImagesRequest request = new DescribeImagesRequest();

		Filter accountIdFilter = new Filter();

		accountIdFilter.setName("owner-id");

		ArrayList<String> listValues = new ArrayList<String>();

		listValues.add("297106433303");

		accountIdFilter.setValues(listValues);

		Collection<Filter> filters = new ArrayList<Filter>();
		filters.add(accountIdFilter);

		request.setFilters(filters);

		DescribeImagesResult imagesResult = ec2Client.describeImages(request);

		List<Image> images = imagesResult.getImages();

		for (Image image : images) {

			String imageCreationDate = image.getCreationDate();

			String imageId = image.getImageId();

			LOGGER.info("IMAGE CREATED DATE" + imageCreationDate);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

			Date date;
			try {
				date = sdf.parse(imageCreationDate);
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				if (cal.after(workShopCal) == true) {

					LOGGER.info("Image Created after workshop " + image.getName());
					LOGGER.info("Image Created On  " + image.getCreationDate());

					LOGGER.info("Deregistering image " + image.toString());
					DeregisterImageRequest request1 = new DeregisterImageRequest();
					request1.setImageId(imageId);

					ec2Client.deregisterImage(request1);

					LOGGER.info("Deregistered image " + image.toString());

				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	private void cleanupSnapshots() {

		DescribeSnapshotsRequest request = new DescribeSnapshotsRequest();

		Filter accountIdFilter = new Filter();

		accountIdFilter.setName("owner-id");

		ArrayList<String> listValues = new ArrayList<String>();

		listValues.add("297106433303");

		accountIdFilter.setValues(listValues);

		Collection<Filter> filters = new ArrayList<Filter>();
		filters.add(accountIdFilter);

		request.setFilters(filters);

		com.amazonaws.services.ec2.model.DescribeSnapshotsResult result = ec2Client.describeSnapshots(request);

		// display only private

		// start-time
		LOGGER.info("GETTING SNAPSHOTS  ");

		List<Snapshot> snapList = result.getSnapshots();

		for (Snapshot snapshot : snapList) {

			Date snapShotCreationDate = snapshot.getStartTime();

			//Calendar workShopcal = Calendar.getInstance();

			//Calendar snapShotCreationCal = Calendar.getInstance();

			//snapShotCreationCal.setTime(snapShotCreationDate);

			if (true == CMDateUtils.isDateWithinWorkShopDate(workShopStartCal, workShopendCal,
			snapShotCreationDate)) {
			//if (true == snapShotCreationCal.(workShopcal)) {

				LOGGER.info("SNAPSHOT Information " + snapshot.toString());

				DeleteSnapshotRequest delRequest = new DeleteSnapshotRequest();

				delRequest.withSnapshotId(snapshot.getSnapshotId());
				LOGGER.info("Deleting SNAPSHOT  " + snapshot.toString());

				ec2Client.deleteSnapshot(delRequest);
				LOGGER.info("Deleted SNAPSHOT  " + snapshot.toString());

			}
		}
	}

	public void startCleanup() {

		String profile = "amod_cmworkshop";
		AWSCredentialsProvider awsCreds = new ProfileCredentialsProvider(profile);
		Set<String> regionSet = new HashSet<String>();

		regionSet.add("us-gov-west-1");
		regionSet.add("us-gov-east-1");
		regionSet.add("cn-north-1");
		regionSet.add("cn-northwest-1");
		regionSet.add("ap-south-1");
		regionSet.add("ap-east-1");

		// iterate for all regions

		for (Regions region : Regions.values()) {

			LOGGER.info("Name of Region is " + region.getName());

			String regionName = region.getName();

			if (regionSet.contains(regionName) == false) {

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

						Calendar launchCal = Calendar.getInstance();

						launchCal.setTime(launchDate);

						LOGGER.info(
								"Instance with instance id " + instance.getInstanceId() + " launched on " + launchDate);

						if (launchCal.after(workShopCal)) {
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

								LOGGER.info("TERMINATING  instance id " + instance.getInstanceId() + " launched on "
										+ launchDate);
								ec2Client.terminateInstances(request);
							}

						}
					}

				}

				// Cleanup AMIs
				cleanupAMIS();
				// cleanup snapshots
				cleanupSnapshots();

				// Cleanup Volumes
				cleanupVolumes();
				// cleanup security groups
				cleanupSGS();

				// cleanupVPCs();

			} // if region !=
		}
	}

	private static void cleanupVolumes() {

		DescribeVolumesRequest req = new DescribeVolumesRequest();

		Collection<Filter> filters = new HashSet<Filter>();

		Filter filter = new Filter();
		filter.setName("status");

		ArrayList<String> listValues = new ArrayList<String>();
		listValues.add("available");

		filter.setValues(listValues);

		filters.add(filter);

		req.setFilters(filters);

		DescribeVolumesResult volResult = ec2Client.describeVolumes(req);

		List<Volume> listVolumes = volResult.getVolumes();

		for (Volume vol : listVolumes) {
			LOGGER.info("Volume id is  " + vol.getVolumeId());

			LOGGER.info("Volume is " + vol.getSize());

			DeleteVolumeRequest delreq = new DeleteVolumeRequest();

			String volumeId = vol.getVolumeId();
			delreq.setVolumeId(volumeId);
			//
			ec2Client.deleteVolume(delreq);

		}
	}

	private static void cleanupVPCs() {

		DescribeVpcsRequest req = new DescribeVpcsRequest();

		Collection<Filter> filters = new HashSet<Filter>();

		Filter filter = new Filter();
		filter.setName("tag-key");

		ArrayList<String> listValues = new ArrayList<String>();

		listValues.add("del");
		filter.setValues(listValues);

		req.setFilters(filters);

		DescribeVpcsResult result = ec2Client.describeVpcs(req);

		List<Vpc> vpcs = result.getVpcs();
		for (Vpc vpc : vpcs) {

			String vpcId = vpc.getVpcId();

			LOGGER.info("VPC ID : " + vpcId);

			List<Tag> tagList = vpc.getTags();
			for (Tag vpcTag : tagList) {
				String tagValue = vpcTag.getValue();
				LOGGER.info("Tag Value is " + tagValue);

				String lowerTagValue = tagValue.toLowerCase();

				if (true == tagValue.contains(clientInitials.toString().toLowerCase())) {

					LOGGER.info("Deleting VPC with Name : " + tagValue);

					DeleteVpcRequest delVPCReq = new DeleteVpcRequest().withVpcId(vpcId);
					ec2Client.deleteVpc(delVPCReq);

				}
			}
		}

	}

	private static void cleanupSGS() {

		DescribeSecurityGroupsRequest req = new DescribeSecurityGroupsRequest();

		Filter accountIdFilter = new Filter();

		accountIdFilter.setName("owner-id");

		ArrayList<String> listValues = new ArrayList<String>();

		listValues.add("297106433303");

		accountIdFilter.setValues(listValues);

		Collection<Filter> filters = new ArrayList<Filter>();
		filters.add(accountIdFilter);

		// request.setFilters(filters);

		ec2Client.describeSecurityGroups(req);

	}

}
