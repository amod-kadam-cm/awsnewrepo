package com.cloudmanthan.aws.cleanup;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.route53.AmazonRoute53;
import com.amazonaws.services.route53.AmazonRoute53ClientBuilder;
import com.amazonaws.services.route53.model.DeleteHealthCheckRequest;
import com.amazonaws.services.route53.model.GetHealthCheckCountResult;
import com.amazonaws.services.route53.model.HealthCheck;
import com.amazonaws.services.route53.model.HealthCheckConfig;
import com.amazonaws.services.route53.model.ListHealthChecksResult;

public class Route53Cleanup extends ServiceCleanupBase {

	static Logger LOGGER = Logger.getLogger(Route53Cleanup.class.getName());

	private static AmazonRoute53 route53Client;

	public static void main(String[] args) {

	}

	public void startCleanup(Calendar startCal, Calendar endCal) {

		super.startCleanup(startCal, endCal);

		Set<String> regionSet = new HashSet<String>();

		regionSet.add("us-gov-west-1");
		regionSet.add("cn-north-1");
		regionSet.add("cn-northwest-1");

		// iterate for all regions

		for (Regions region : Regions.values()) {

			LOGGER.info("Name of Region is " + region.getName());

			String regionName = region.getName();

			if (regionSet.contains(regionName) == false) {

				route53Client = AmazonRoute53ClientBuilder.standard().withCredentials(awsCredentialsProvider)
						.build();

				try {
					cleanupHealthChecks(regionName);
				} catch (Exception ex) {

					ex.printStackTrace();
				}
			}
		} //
	}

	private void cleanupHealthChecks(String currentRegion) {

		GetHealthCheckCountResult healthCheckCountResult  = route53Client.getHealthCheckCount();
		
		Long noHealthChecks = healthCheckCountResult.getHealthCheckCount();
		
		ListHealthChecksResult result = route53Client.listHealthChecks();
		
		List<HealthCheck> healthChecks = result.getHealthChecks();
		
		for (HealthCheck healthCheck: healthChecks ) {
			
			String healthCheckId = healthCheck.getId();
			
			LOGGER.log(Level.INFO, "Health Check id is " + healthCheckId);
			
			
			DeleteHealthCheckRequest req = new DeleteHealthCheckRequest().withHealthCheckId(healthCheckId) ;
			route53Client.deleteHealthCheck(req);
		
		
	}

}
}
