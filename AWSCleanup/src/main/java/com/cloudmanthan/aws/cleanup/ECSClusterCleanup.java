package com.cloudmanthan.aws.cleanup;

import java.util.HashSet;
import java.util.List;
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
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import com.amazonaws.services.ecs.model.Cluster;
import com.amazonaws.services.ecs.model.DeleteClusterRequest;
import com.amazonaws.services.ecs.model.DeleteServiceRequest;
import com.amazonaws.services.ecs.model.DeregisterTaskDefinitionRequest;
import com.amazonaws.services.ecs.model.DescribeClustersResult;
import com.amazonaws.services.ecs.model.ListClustersResult;
import com.amazonaws.services.ecs.model.ListServicesRequest;
import com.amazonaws.services.ecs.model.ListServicesResult;
import com.amazonaws.services.ecs.model.ListTaskDefinitionsResult;

public class ECSClusterCleanup {
	static Logger LOGGER = Logger.getLogger(ECSClusterCleanup.class.getName());

	public static void main(String[] args) {

		startCleanup();
		
		cleanupEC2();

	}

	private static void cleanupEC2() {
		
	}

	public static void startCleanup() {

		AmazonECS ecsClient = null;

		String profile = "amod_cmworkshop";
		AWSCredentialsProvider awsCreds = new ProfileCredentialsProvider(profile);
		Set<String> regionSet = new HashSet<String>();

		regionSet.add("us-gov-west-1");
		regionSet.add("cn-north-1");
		regionSet.add("cn-northwest-1");
		regionSet.add("ap-south-1");
		regionSet.add("ap-east-1");
		regionSet.add("us-gov-east-1");


		for (Regions region : Regions.values()) {

			LOGGER.info("Name of Region is " + region.getName());

			String regionName = region.getName() ;

			if (regionSet.contains(regionName) == false) {
					
				ecsClient = AmazonECSClientBuilder.standard().withCredentials(awsCreds).withRegion(region.getName()).build();

				DescribeClustersResult response = ecsClient.describeClusters();
				
				ListClustersResult listClusterResult =  ecsClient.listClusters();
				
				List<String> clusterARNS =listClusterResult.getClusterArns();
				LOGGER.info( "No. of ECS Clusters are  " + response.getClusters().size());

				for (String clusterARN :	clusterARNS ) {		
					
					//String alloc_id = cluster.getAllocationId();
					
					//LOGGER.info("DELETTING CLUSTER " + cluster.getClusterName() );
				
					//LOGGER.info("CLUSTER ARN IS " + clusterARN);
					
					// Get the ECS Service list
					
					ListServicesRequest arg0 = new ListServicesRequest()
							.withCluster(clusterARN);
					
					ListServicesResult result = ecsClient.listServices(arg0 );
					
					List<String> serviceList = result.getServiceArns();
					
					for (String service : serviceList) {
						DeleteServiceRequest arg1 = new DeleteServiceRequest()
								.withService(service)
								.withCluster(clusterARN)
								.withForce(true);
						ecsClient.deleteService(arg1 );
						
					}
					
					
					DeleteClusterRequest request = new DeleteClusterRequest()
							.withCluster(clusterARN);
					LOGGER.info("DELETING CLUSTER WITH ARN " + clusterARN);
					
					ecsClient.deleteCluster(request);
					LOGGER.info("DELETED CLUSTER WITH ARN " + clusterARN);
					
					

				} // for each address
				
				
				ListTaskDefinitionsResult taskDefnResult =  ecsClient.listTaskDefinitions();
				
				List<String> taskDefinitionList = taskDefnResult.getTaskDefinitionArns();
				
				for (String taskDefinition : taskDefinitionList ) {
					LOGGER.info("Task definition " + taskDefinition);
					
		
					
					DeregisterTaskDefinitionRequest request = new DeregisterTaskDefinitionRequest()
							.withTaskDefinition(taskDefinition);
					
					ecsClient.deregisterTaskDefinition(request);
				}
			} // if region !=
		} // for regions
	}

}
