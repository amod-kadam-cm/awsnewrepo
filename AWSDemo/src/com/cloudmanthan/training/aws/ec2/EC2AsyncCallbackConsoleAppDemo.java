package com.cloudmanthan.training.aws.ec2;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.handlers.AsyncHandler;
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
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class EC2AsyncCallbackConsoleAppDemo {

	 public class AsyncEC2Handler implements AsyncHandler<RunInstancesRequest, RunInstancesResult>
	    {
	       
	        public void onError(Exception e) {
	            System.out.println(e.getMessage());
	            System.exit(1);
	        }

			@Override
			public void onSuccess(RunInstancesRequest request, RunInstancesResult result ) {
				
				
				Reservation reservation = result.getReservation();
				List<Instance> instanceList = reservation.getInstances();
				Iterator<Instance> instaceIterator = instanceList.iterator();

				while (instaceIterator.hasNext()) {

					Instance instance = instaceIterator.next();
					System.out.println("CALLBACK METHOD");
					System.out.println("Instance Id is " + instance.getInstanceId());
					
				}
				System.exit(0);
			}
	    }
	static AmazonEC2Async ec2Client;

	static void init() {

		/*InstanceProfileCredentialsProvider credentialsProvider = InstanceProfileCredentialsProvider.getInstance();
		ec2Client = AmazonEC2AsyncClientBuilder.standard().withRegion(Regions.US_EAST_2)
				.withCredentials(credentialsProvider).build();
				
				*/
		ec2Client = AmazonEC2AsyncClientBuilder.defaultClient();

	}

	public static void main(String[] args) {

		try {
			init();
			launchInstance();
				
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	private static void launchInstance() {
		String imageId = "ami-8c1be5f6";
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();

		runInstancesRequest
		.withImageId(imageId)
		.withInstanceType("t2.nano")
		.withMinCount(1)
		.withMaxCount(1)
		.withKeyName("cmudemy_us_east_1_kp")
		.withSubnetId("subnet-4740361c")
		.withSecurityGroupIds("sg-05fa0774");

			
		 ec2Client.runInstancesAsync(runInstancesRequest, new EC2AsyncCallbackConsoleAppDemo().new AsyncEC2Handler());
		}

}
