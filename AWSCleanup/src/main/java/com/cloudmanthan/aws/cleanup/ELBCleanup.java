package com.cloudmanthan.aws.cleanup;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder;
import com.amazonaws.services.elasticloadbalancingv2.model.DeleteLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancingv2.model.LoadBalancer;
import com.cloudmanthan.aws.utils.CMDateUtils;

public class ELBCleanup {
	static Logger LOGGER = Logger.getLogger(ELBCleanup.class.getName());
	static AmazonElasticLoadBalancing elbClient;
	static AWSCredentialsProvider awsCreds;
	static String profile = "amod_cmworkshop";
	private static DescribeLoadBalancersRequest arg;

	private static void initialze() {
		awsCreds = new ProfileCredentialsProvider(profile);
	}

	static void startCleanup(Calendar startCal, Calendar endCal) {

		initialze();

		Set<String> regionSet = new HashSet<String>();
		regionSet.add("us-gov-west-1");
		regionSet.add("us-gov-east-1");
		regionSet.add("cn-north-1");
		regionSet.add("cn-northwest-1");
		regionSet.add("ap-south-1");
		regionSet.add("ap-east-1");
		regionSet.add("ap-east-1");

		// iterate over all regions

		for (Regions region : Regions.values()) {

			LOGGER.info("Name of Region is " + region.getName());

			String regionName = region.getName();

			if (regionSet.contains(regionName) == false) {

				elbClient = AmazonElasticLoadBalancingClientBuilder.standard().withCredentials(awsCreds)
						.withRegion(region.getName()).build();
				
				DescribeLoadBalancersRequest request = new DescribeLoadBalancersRequest();
			
				DescribeLoadBalancersResult result = elbClient.describeLoadBalancers(request);

				List<LoadBalancer> elbList = result.getLoadBalancers();
			
				for  (LoadBalancer lb : elbList) {
					String lbARN = lb.getLoadBalancerArn();

					LOGGER.info("  LB Found " + lbARN);

					Date lbCreatedDate = lb.getCreatedTime();

					if (true == CMDateUtils.isDateWithinWorkShopDate(startCal, endCal, lbCreatedDate)) {

						LOGGER.info("DELETING  ELB " + lbARN);

						DeleteLoadBalancerRequest arg0 = new DeleteLoadBalancerRequest().withLoadBalancerArn(lbARN);
						elbClient.deleteLoadBalancer(arg0);
						LOGGER.info("DELETED ELB " + lbARN);

					}
				}

			} // if region !=
		}
	} // for regions

}
