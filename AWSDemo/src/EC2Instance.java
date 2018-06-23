import java.awt.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Iterable;
import java.util.*;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
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
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.RunInstancesResult;

public class EC2Instance {

	
	static final String accessKey = "acceesky";
	static final String secretKey = "secretkey";
	static AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
	static AmazonEC2 ec2 = new AmazonEC2Client(credentials);
	static String ImageID,InstanceType,SecurityGp,KeyName,StartInstanceID,StopInstanceID,TerInstanceID;
	
	
	 public static void RegionN() {
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
	        System.out.println("14\t South America (S�o Paulo)");
	        System.out.println("Please enter your Region choice:");
	        
	        //Get user's choice
	        int choice=in.nextInt();
	        //Display the title of the chosen module
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
	            default: System.out.println("Invalid choice");
	        }//end of switch
		     }

	 public void Input()
		{
			
			ImageID = "ami-38cd975d";
			InstanceType = "t2.nano";
			SecurityGp = "sg-2361b74a";
			KeyName = "SONALI-NEW-INSTANCE-3";
			
			StartInstanceID = "i-0f9ff8e55c43f73f5";
			
			StopInstanceID = "i-07afcaa3e61a276df";
			
			TerInstanceID="i-07afcaa3e61a276df";
		}
	 
	public void CreateInstance() throws IOException
	{ 
    //Get user's choice
		//ec2.setRegion(Region.getRegion(Regions.US_EAST_2));
		RegionN();
		Input();
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
		.withImageId(ImageID)
	    .withInstanceType(InstanceType)
	    .withMinCount(1)
	    .withMaxCount(1)
	    .withSecurityGroupIds(SecurityGp)
	    .withKeyName(KeyName);
		RunInstancesResult runInstances = ec2.runInstances(runInstancesRequest);
		// TAG EC2 INSTANCES
				String Id = runInstances.getReservation().getInstances().get(0).getInstanceId();
				  CreateTagsRequest createTagsRequest = new CreateTagsRequest();
				  createTagsRequest.withResources(Id) 
				      .withTags(new Tag("Name", "Test-Instance"));
				  ec2.createTags(createTagsRequest);

		
	}

	public void Start() throws IOException
	{	
		RegionN();
		Input();
		StartInstancesRequest startRequest = new StartInstancesRequest().withInstanceIds(StartInstanceID);
         ec2.startInstances(startRequest);
         System.out.println("Instance is started:- \t");
         System.out.print(StartInstanceID);
	}
	
	public void Stop() throws IOException
	{   
		RegionN();
		Input();
		StopInstancesRequest stopRequest = new StopInstancesRequest().withInstanceIds(StopInstanceID);
        ec2.stopInstances(stopRequest);
        System.out.println("Instance is stoped:- \t");
        System.out.print(StopInstanceID);
	}
	
	public void Terminate() throws IOException
	{  
		RegionN();
		Input();
		TerminateInstancesRequest req = new TerminateInstancesRequest().withInstanceIds(TerInstanceID);
    	 ec2.terminateInstances(req);
    	 System.out.println("Instance is terminated:- \t");
         System.out.print(TerInstanceID);
	}
	
	public static void main(String args[])throws Exception
	{
		EC2Instance x=new EC2Instance();
		
		 Scanner in = new Scanner(System.in);
	        // Display the menu
	        System.out.println("1\t Launch New Instance");
	        System.out.println("2\t Start Instance");
	        System.out.println("3\t Stop Instance");
	        System.out.println("4\t Terminate Instance");
	        System.out.println("Select your choice :");
	        int ch=in.nextInt();
	        //Display the title of the chosen module
	        switch (ch) {
	            case 1: 
	            	x.CreateInstance();
	            		break;
	            case 2:
	            	x.Start();
	                    break;
	            case 3:
	            	x.Stop();            	
	                    break;
	            case 4:
	            	x.Terminate();	            	
	                    break;
	            default: System.out.println("Invalid choice");
	        }//end of switch	
	}
	
}

