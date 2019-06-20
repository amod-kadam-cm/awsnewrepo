package com.cloudmanthan.aws.cleanup;

import java.util.Calendar;

public class MasterCleanup {

	private static final boolean AUTOSCALING_CLEANUP = false;
	private static final boolean ECSCLUSTER_CLEANUP = false;
	private static final boolean EC2_CLEANUP = true;
	private static final boolean RDS_CLEANUP = false;
	private static final boolean ROUTE_53_CLEANUP = false;
	private static final boolean APIGATEWAY_CLEANUP = false;
	private static final boolean EB_CLEANUP = true;
	private static final boolean S3_CLEANUP = true ;
	private static final boolean CLOUDWATCH_LOGROUP_CLEANUP = true;
	private static final boolean ELB_CLEANUP = true;

	public static void main(String[] args) {

		// set the start date and end date of workshop

		Calendar startCal = Calendar.getInstance();
		startCal.clear();
		// this is the start date of workshop
		startCal.set(2016, Calendar.SEPTEMBER, 10);

		Calendar endCal = Calendar.getInstance();
		endCal.clear();
		// this is the start date of workshop
		endCal.set(2019, Calendar.JUNE, 14);

		WorkshopCalendar workShopCalendar = new WorkshopCalendar();

		workShopCalendar.setWorkshopStartCalendar(startCal);

		workShopCalendar.setWorkshopEndDate(endCal);

		// Starts cleaning up all the resources one by one

		// Cleanup ECS
		if (true == ECSCLUSTER_CLEANUP) {

			ECSClusterCleanup ecsCleanup = new ECSClusterCleanup();
			ecsCleanup.startCleanup();
		}

		// Cleanup AutoScaling
		if (true == AUTOSCALING_CLEANUP) {

			AutoScalingCleanup autoScalingCleanup = new AutoScalingCleanup();
			autoScalingCleanup.startCleanup(startCal, endCal);
		}

		// Cleanup AutoScaling
		if (true == EC2_CLEANUP) {

			EC2Cleanup ec2Cleanup = new EC2Cleanup(startCal, endCal);

			ec2Cleanup.startCleanup();
		}

		// ELB Cleanup
		if (true == ELB_CLEANUP) {
			ELBCleanup elbCleanup = new ELBCleanup();
			elbCleanup.startCleanup(startCal, endCal);
		}
		// RDS cleanup
		if (true == RDS_CLEANUP) {
			RDSCleanup rdsCleanup = new RDSCleanup();
			rdsCleanup.startCleanup(startCal, endCal);
		}

		if (true == ROUTE_53_CLEANUP) {
			// Route53 Cleanup
			Route53Cleanup r53Cleanup = new Route53Cleanup();

			r53Cleanup.startCleanup(startCal, endCal);
		}

		if (true == APIGATEWAY_CLEANUP) {
			// API Gateway Cleanup
			APIGatewayCleanup apiGgateWayCleanup = new APIGatewayCleanup();

			apiGgateWayCleanup.startCleanup(startCal, endCal);
		}
		if (true == EB_CLEANUP) {
			// Cleanup ElasticBeanstalk
			EBCleanup ebCleanup = new EBCleanup();
			ebCleanup.startCleanup(startCal, endCal);
		}
		if (true == S3_CLEANUP) {

			// S3 Cleanup
			S3Cleanup s3Cleanup = new S3Cleanup();
			s3Cleanup.startCleanup(startCal, endCal);
		}

		if (true == CLOUDWATCH_LOGROUP_CLEANUP) {

			// Cleanup CloudWatchGroup
			CloudWatchLogGroupCleanup logGroupCleanup = new CloudWatchLogGroupCleanup();
			logGroupCleanup.startCleanup(startCal, endCal);
		}
	}

}
