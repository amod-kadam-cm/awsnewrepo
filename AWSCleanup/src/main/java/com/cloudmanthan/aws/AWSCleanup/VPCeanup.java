package com.cloudmanthan.aws.AWSCleanup;

import java.util.HashSet;
import java.util.Set;
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

public class VPCeanup {
	static Logger LOGGER = Logger.getLogger(VPCeanup.class.getName());

	public static void main(String[] args) {

		startCleanup();
		
		cleanupEC2();

	}

	private static void cleanupEC2() {
		
	}

	private static void startCleanup() {

		AmazonEC2 ec2 = null;

		String profile = "amod_cmworkshop";
		AWSCredentialsProvider awsCreds = new ProfileCredentialsProvider(profile);
		Set<String> regionSet = new HashSet<String>();

		regionSet.add("us-gov-west-1");
		regionSet.add("cn-north-1");
		regionSet.add("cn-northwest-1");


		// iterate for all regions

		for (Regions region : Regions.values()) {

			LOGGER.info("Name of Region is " + region.getName());

			String regionName = region.getName() ;
			// Don't do any operations on gov_cloud
			if (regionSet.contains(regionName) == false) {
					
				// String region = regions.g
				ec2 = AmazonEC2ClientBuilder.standard().withCredentials(awsCreds).withRegion(region.getName()).build();

				DescribeAddressesResult response = ec2.describeAddresses();

				LOGGER.info(response.getAddresses().size() + " Elastic IPs found in " + region.getName());

				for (Address address : response.getAddresses()) {
					System.out.printf(
							"Found address with public IP %s, " + "domain %s, " + "allocation id %s " + "and NIC id %s",
							address.getPublicIp(), address.getDomain(), address.getAllocationId(),
							address.getNetworkInterfaceId());

					// now release EIP addresses
					// TBD - in case there is an exception of inuse those needs to be
					// disassociated

					String alloc_id = address.getAllocationId();
					ReleaseAddressRequest request = new ReleaseAddressRequest().withAllocationId(alloc_id);

					ReleaseAddressResult eipReleaseresponse = ec2.releaseAddress(request);

					System.out.printf("Successfully released elastic IP address %s from region %s ", alloc_id,
							region.getName() + "\n");

				} // for each address
				
				
			} // if region !=
		} // for regions
	}

}
