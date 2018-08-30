package com.cloudmanthan.aws.cleanup;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;

public class DynamoDBCleanup {
	static Logger LOGGER = Logger.getLogger(DynamoDBCleanup.class.getName());
	private static AmazonDynamoDB dynamoDBClient;

	public static void main(String[] args) {

		startCleanup();

	}

	private static void cleanupTables() {
		ListTablesResult result = dynamoDBClient.listTables();

		List<String> tableNames = result.getTableNames();
		LOGGER.info("No. Of tables are " + tableNames.size());

		for (String tableName : tableNames) {

			LOGGER.info("TABLE NAME IS" + tableName);

			DescribeTableRequest request = new DescribeTableRequest();
			request.withTableName(tableName);

			DescribeTableResult result1 = dynamoDBClient.describeTable(request);

			TableDescription tableDesc = result1.getTable();

			Date creationDate = tableDesc.getCreationDateTime();

			/*Calendar workShopcal = Calendar.getInstance();
			// TODO : Parameterize it to accept the from date and to date
			workShopcal.clear();
			workShopcal.set(2018, Calendar.JULY, 25); */
			
			Calendar workShopcal = 
			WorkschopCalendar.getWorksHopCalendar();

			Calendar launchCal = Calendar.getInstance();

			launchCal.setTime(creationDate);

			LOGGER.info("Table  with tableName " + tableName + " created  on " + creationDate);

			if (launchCal.after(workShopcal)) {
				LOGGER.info("DELETING table " + tableDesc.toString());
				dynamoDBClient.deleteTable(tableName);
				LOGGER.info("DELETED " + tableDesc.toString());

			}

		}

	}

	private static void startCleanup() {

		String profile = "amod_cmworkshop";
		AWSCredentialsProvider awsCreds = new ProfileCredentialsProvider(profile);
		Set<String> regionSet = new HashSet<String>();

		regionSet.add("us-gov-west-1");
		regionSet.add("cn-north-1");
		regionSet.add("cn-northwest-1");

		// iterate for all regions

		for (Regions region : Regions.values()) {

			LOGGER.info("Name of Region is " + region.getName());

			String regionName = region.getName();

			if (regionSet.contains(regionName) == false) {

				dynamoDBClient = AmazonDynamoDBClientBuilder.standard().withCredentials(awsCreds)
						.withRegion(region.getName()).build();

				cleanupTables();

			}
		} // for regions
	}
}
