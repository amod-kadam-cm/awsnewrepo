package com.cloudmanthan.training.aws.ec2;

import java.awt.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Iterable;
import java.util.*;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.autoscaling.model.Instance;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.simpledb.AmazonSimpleDBClient;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.RunInstancesResult;

public class EC2ConsoleApp {

	static final String accessKey = "AKIAICW46AB5ALZP6WJQ";
	static final String secretKey = "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";
	static AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
	static AmazonEC2 ec2;
	static String imageID, instanceType, securityGroup, keyPairName, startInstanceId, stopInstanceID,
			terminateInstanceId;

	private static void init() throws Exception {

		/*
		 * The ProfileCredentialsProvider will return your [udemy_amod]
		 * credential profile by reading from the credentials file located at
		 * (C:\\Users\\Administrator\\.aws\\credentials).
		 */
		AWSCredentials credentials = null;
		try {
			credentials = new ProfileCredentialsProvider("udemy_amod").getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (C:\\Users\\Administrator\\.aws\\credentials), and is in valid format.", e);
		}
		ec2 = new AmazonEC2Client(credentials);

	}

	public static void selectRegion() {
		Scanner in = new Scanner(System.in);
		// Display the menu
		System.out.println("1\t US East (N. Virginia)");
		System.out.println("2\t US East (Ohio)");
		System.out.println("3\t US West (N. California)");
		System.out.println("4\t US West (Oregon)");
		System.out.println("5\t Canada (Central)");
		System.out.println("6\t Asia Pacific (Mumbai)");
		System.out.println("7\t Asia Pacific (Seoul)	");
		System.out.println("8\t Asia Pacific (Singapore)");
		System.out.println("9\t Asia Pacific (Sydney)");
		System.out.println("10\t Asia Pacific (Tokyo)");
		System.out.println("11\t EU (Frankfurt)");
		System.out.println("12\t EU (Ireland)");
		System.out.println("13\t EU (London)");
		System.out.println("14\t South America (São Paulo)");
		System.out.println("Please enter your Region choice:");

		// Get user's choice
		int choice = in.nextInt();
		// Display the title of the chosen module
		switch (choice) {
		case 1:
			ec2.setRegion(Region.getRegion(Regions.US_EAST_1));
			System.out.println("You have selected US_EAST_1 Region");
			break;
		case 2:
			ec2.setRegion(Region.getRegion(Regions.US_EAST_2));
			System.out.println("You have selected US_EAST_2 Region");
			break;
		case 3:
			ec2.setRegion(Region.getRegion(Regions.US_WEST_1));
			System.out.println("You have selected US_WEST_1 Region");
			break;
		case 4:
			ec2.setRegion(Region.getRegion(Regions.US_WEST_2));
			System.out.println("You have selected US_WEST_2 Region");
			break;
		case 5:
			ec2.setRegion(Region.getRegion(Regions.CA_CENTRAL_1));
			System.out.println("You have selected CA_CENTRAL_1 Region");
			break;
		case 6:
			ec2.setRegion(Region.getRegion(Regions.AP_SOUTH_1));
			System.out.println("You have selected AP_SOUTH_1 Region");
			break;
		case 7:
			ec2.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));
			System.out.println("You have selected AP_NORTHEAST_2 Region");
			break;
		case 8:
			ec2.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_1));
			System.out.println("You have selected AP_SOUTHEAST_1 Region");
			break;
		case 9:
			ec2.setRegion(Region.getRegion(Regions.AP_SOUTHEAST_2));
			System.out.println("You have selected AP_SOUTHEAST_2 Region");
			break;
		case 10:
			ec2.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
			System.out.println("You have selected AP_NORTHEAST_1 Region");
			break;
		case 11:
			ec2.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
			System.out.println("You have selected EU_CENTRAL_1 Region");
			break;
		case 12:
			ec2.setRegion(Region.getRegion(Regions.EU_WEST_1));
			System.out.println("You have selected EU_WEST_1 Region");
			break;
		case 13:
			ec2.setRegion(Region.getRegion(Regions.EU_WEST_2));
			System.out.println("You have selected EU_WEST_2 Region");
			break;
		case 14:
			ec2.setRegion(Region.getRegion(Regions.SA_EAST_1));
			System.out.println("You have selected SA_EAST_1 Region");
			break;
		default:
			System.out.println("Invalid choice");
		}// end of switch
	}

	public void setRequestValues() {
		// set appropriate values
		imageID = "ami-38cd975d";
		instanceType = "t2.nano";
		// linux_dev_sg : sg-39228850
		securityGroup = "sg-39228850";
		keyPairName = "udmey_amod_ohio";

		startInstanceId = "i-0f9ff8e55c43f73f5";

		stopInstanceID = "i-0d566ca49a5240801";

		terminateInstanceId = "i-0d566ca49a5240801";
	}

	public void launchInstance() throws IOException {

		try {
			// Get user's choice
			this.selectRegion();

			// ec2.setRegion(Region.getRegion(Regions.US_EAST_2));
			this.setRequestValues();
			System.out.println("Launching an EC2 instance");

			RunInstancesRequest runInstancesRequest = new RunInstancesRequest().withImageId(imageID)
					.withInstanceType(instanceType).withMinCount(1).withMaxCount(1).withSecurityGroupIds(securityGroup)
					.withKeyName(keyPairName);
			RunInstancesResult runInstances = ec2.runInstances(runInstancesRequest);
			// TAG EC2 INSTANCES
			String id = runInstances.getReservation().getInstances().get(0).getInstanceId();
			CreateTagsRequest createTagsRequest = new CreateTagsRequest();
			createTagsRequest.withResources(id).withTags(new Tag("Name", "Test-Instance"));
			ec2.createTags(createTagsRequest);

			// Add Code to describe the launched instance
			runInstances.toString();

			System.out.println("Successfully launcheed instance with id  " + id);

		} catch (AmazonServiceException ase) {
			this.printStackTrace(ase);
		}

	}

	public void startInstance() throws IOException {
		try {

			StartInstancesRequest startRequest = new StartInstancesRequest().withInstanceIds(startInstanceId);
			ec2.startInstances(startRequest);
			System.out.println("Instance is started:- \t");
			System.out.print(startInstanceId);
		} catch (AmazonServiceException ase) {
			printStackTrace(ase);
		}
	}

	private void printStackTrace(AmazonServiceException ase) {
		System.out.println("Caught Exception: " + ase.getMessage());
		System.out.println("Reponse Status Code: " + ase.getStatusCode());
		System.out.println("Error Code: " + ase.getErrorCode());
		System.out.println("Request ID: " + ase.getRequestId());

	}

	public void stopInstance() throws IOException {
		selectRegion();
		setRequestValues();

		StopInstancesRequest stopRequest = new StopInstancesRequest().withInstanceIds(stopInstanceID);
		ec2.stopInstances(stopRequest);
		System.out.println("Instance is stoped:- \t");
		System.out.print(stopInstanceID);
	}

	public void terminateInstance() throws IOException {
		try {
			selectRegion();
			setRequestValues();
			TerminateInstancesRequest req = new TerminateInstancesRequest().withInstanceIds(terminateInstanceId);
			ec2.terminateInstances(req);
			System.out.println("Instance is terminated:- \t");
			System.out.print(terminateInstanceId);
		} catch (AmazonServiceException ase) {
			this.printStackTrace(ase);
		}
	}

	public static void main(String args[]) throws Exception {

		EC2ConsoleApp ec2App = new EC2ConsoleApp();

		init();

		Scanner in = new Scanner(System.in);
		// Display the menu
		System.out.println("1\t Launch New Instance");
		System.out.println("2\t Start Instance");
		System.out.println("3\t Stop Instance");
		System.out.println("4\t Terminate Instance");
		System.out.println("Select your choice :");
		int ch = in.nextInt();
		// Display the title of the chosen module
		switch (ch) {
		case 1:
			ec2App.launchInstance();
			break;
		case 2:
			ec2App.startInstance();
			break;
		case 3:
			ec2App.stopInstance();
			break;
		case 4:
			ec2App.terminateInstance();
			break;
		default:
			System.out.println("Invalid choice");
		}// end of switch
	}

}
