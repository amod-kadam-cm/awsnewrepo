package com.cloudmanthan.training.aws.ec2;


import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Async;
import com.amazonaws.services.ec2.AmazonEC2AsyncClient;
import com.amazonaws.services.ec2.AmazonEC2AsyncClientBuilder;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.cloudmanthan.training.aws.s3.S3Demo;

public class EC2AsyncConsoleAppDemo {

	static AmazonEC2Async ec2Client;
	static String imageId = "ami-8c1be5f6";
	static final Logger logger = Logger.getLogger(EC2AsyncConsoleAppDemo.class);

	static void init() {

		/*
		 * InstanceProfileCredentialsProvider credentialsProvider =
		 * InstanceProfileCredentialsProvider.getInstance(); ec2Client =
		 * AmazonEC2AsyncClientBuilder.standard().withRegion(Regions.US_EAST_2)
		 * .withCredentials(credentialsProvider).build();
		 */
		ec2Client = AmazonEC2AsyncClientBuilder.defaultClient();

	}

	public static void main(String[] args) {

		try {
			init();
			launchInstance();
			System.exit(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void launchInstance() {

		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		
		runInstancesRequest
		.withImageId(imageId)
		.withInstanceType("t2.nano")
		.withMinCount(1)
		.withMaxCount(1)
		.withKeyName("cmudemy_us_east_1_kp")
		.withSubnetId("subnet-4740361c")
		.withSecurityGroupIds("sg-05fa0774");

		Future<RunInstancesResult> futureResult = ec2Client.runInstancesAsync(runInstancesRequest);
		int i = 0;
		while ((futureResult.isDone() == false) && (futureResult.isCancelled() == false)) {
			System.out.println(i);
			logger.info("Waiting for laucninstance to be successful");
			i++;
		}

		try {
			RunInstancesResult runResult = futureResult.get();
			Reservation reservation = runResult.getReservation();
			List<Instance> instanceList = reservation.getInstances();
			Iterator<Instance> instaceIterator = instanceList.iterator();

			while (instaceIterator.hasNext()) {

				Instance instance = instaceIterator.next();

				System.out.println("Instance Id is " + instance.getInstanceId());

			}

		} catch (InterruptedException e) {
			
			e.printStackTrace();
		} catch (ExecutionException e) {
			
			e.printStackTrace();
		}
	}

}
