package com.cloudmanthan.aws.cleanup;

import java.util.Calendar;

public class MasterCleanup {

	public static void main(String[] args) {
		
		//Starts cleaning up all the resources one by one
	
		Calendar startCal = Calendar.getInstance();
		startCal.clear();
		// this is the start date of workshop
		startCal.set(2018, Calendar.JULY, 27);
		
		Calendar endCal = Calendar.getInstance();
		endCal.clear();
		// this is the start date of workshop
		endCal.set(2018, Calendar.OCTOBER ,26);
	
		// set the start date and end date of workshop
		
		WorkshopCalendar workShopCalendar = new WorkshopCalendar();
	
		workShopCalendar.setWorkshopStartCalendar(startCal);
		
		workShopCalendar.setWorkshopEndDate(endCal);
			
		EC2Cleanup ec2Cleanup = new EC2Cleanup(startCal,endCal);
		
		
		//ec2Cleanup.startCleanup();
		// RDS cleanup
		RDSCleanup rdsCleanup = new RDSCleanup();
		//rdsCleanup.startCleanup(startCal,endCal);
		
		// S3 Cleanup
		S3Cleanup s3Cleanup = new S3Cleanup();
		//s3Cleanup.startCleanup(startCal,endCal);
		
		// Route53 Cleanup
		Route53Cleanup r53Cleanup = new Route53Cleanup();
		
		//r53Cleanup.startCleanup(startCal, endCal);
		
		// API Gateway Cleanup
		APIGatewayCleanup apiGgateWayCleanup = new APIGatewayCleanup();
		
		apiGgateWayCleanup.startCleanup(startCal, endCal);
		
		
		
	}

}
