package com.cloudmanthan.aws.AWSCleanup;

import java.util.List;
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
import com.amazonaws.services.elasticache.AmazonElastiCache;
import com.amazonaws.services.elasticache.AmazonElastiCacheClient;
import com.amazonaws.services.elasticache.AmazonElastiCacheClientBuilder;
import com.amazonaws.services.elasticache.model.CacheCluster;
import com.amazonaws.services.elasticache.model.DeleteCacheClusterRequest;
import com.amazonaws.services.elasticache.model.DeleteReplicationGroupRequest;
import com.amazonaws.services.elasticache.model.DescribeCacheClustersResult;
import com.amazonaws.services.elasticache.model.InvalidCacheClusterStateException;
import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription;
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationRequest;
import com.amazonaws.services.elasticbeanstalk.model.DeleteApplicationResult;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationsRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeApplicationsResult;

public class RedisCleanup {
	static Logger LOGGER = Logger.getLogger(RedisCleanup.class.getName());
	private static AmazonElastiCache cacheClient;

	public static void main(String[] args) {

		startCleanup();

	}


	private static void startCleanup() {

		String profile = "amod_cmworkshop";
		AWSCredentialsProvider awsCreds = new ProfileCredentialsProvider(profile);

		// iterate for all regions

		for (Regions region : Regions.values()) {

			LOGGER.info("Name of Region is " + region.getName());

			String regionName = region.getName();
			
			if ( regionName.equals("us-gov-west-1") == false || regionName.equals("cn-north-1") )  {
				               
				// String region = regions.g
				cacheClient = AmazonElastiCacheClientBuilder.standard().withCredentials(awsCreds)
						.withRegion(region.getName()).build();

				//DescribeApplicationsRequest request = new DescribeApplicationsRequest();
				DescribeCacheClustersResult response = cacheClient.describeCacheClusters();

				List<CacheCluster> cacheCluseters = response.getCacheClusters();

				for (CacheCluster cacheCluster : cacheCluseters) {

					DeleteCacheClusterRequest deleteRequest = new DeleteCacheClusterRequest(cacheCluster.getCacheClusterId());

					LOGGER.info("Deleting cluster " + cacheCluster.getCacheClusterId());
					try {
						
						String cacheStatus = cacheCluster.getCacheClusterStatus() ;

						cacheClient.deleteCacheCluster(deleteRequest);
					}catch (InvalidCacheClusterStateException ex) {
						//Cache cluster sivakarrirediscache-001 is serving as primary for replication group sivakarrirediscache
						// and cannot be deleted. To delete the entire replication group, use DeleteReplicationGroup. (
						
						String replicationGroupId = cacheCluster.getReplicationGroupId() ;
						DeleteReplicationGroupRequest request = new DeleteReplicationGroupRequest();
						request.setReplicationGroupId(replicationGroupId);
						cacheClient.deleteReplicationGroup(request);
						
						
						cacheClient.deleteCacheCluster(deleteRequest);
						
						
				}

			} // if region !=
			}
		} // for regions
	}
}


