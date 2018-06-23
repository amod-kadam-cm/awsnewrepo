package com.cloudmanthan.aws.dynamodb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;

public class TrainingHighLevelDemo {

	static AmazonDynamoDB client;
	static DynamoDBMapper mapper;

	public static void main(String[] args) {

		// AmazonDynamoDB client =
		// AmazonDynamoDBClientBuilder.standard().build();
		initLocal();

		// putItem();
		//getItem();
		// updateItem();
		getItem();
		//scanTable();
		//queryTable();
		// query using GSI
		//queryUsingIndex();
		
		queryUsingLSI();
	}

	private static void queryUsingLSI() {

		try {
			DynamoDBMapper mapper = new DynamoDBMapper(client);

			DynamoDBQueryExpression<TrainingItem> queryExpression = new DynamoDBQueryExpression<TrainingItem>();

			TrainingItem trainingItem = new TrainingItem();
			trainingItem.setCustomerShortName("SEED");
			//trainingItem.setForClient("Zensar");
			//trainingItem.setTrainingDuration(new Integer(30));
			
			queryExpression.withHashKeyValues(trainingItem);
			Condition rangeKeyCondition = new Condition();
			rangeKeyCondition.setComparisonOperator("EQ");
			rangeKeyCondition.withAttributeValueList(new AttributeValue().withN("4"));

			
			queryExpression.withIndexName("trainingdurationlsi").withRangeKeyCondition("TrainingDuration", rangeKeyCondition);
		
			PaginatedQueryList<TrainingItem> list = mapper.query(TrainingItem.class, queryExpression);

			// get the count of items which matched the query
			int count = mapper.count(TrainingItem.class, queryExpression);

			System.out.println("Count of items " + count);

			if (list != null) {
				Iterator<TrainingItem> itemIterator = list.iterator();

				while (true == itemIterator.hasNext()) {

					System.out.println("Index Output " + itemIterator.next());
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static void putItem() {

		try {
			DynamoDBMapper mapper = new DynamoDBMapper(client);

			TrainingItem item = new TrainingItem();

			item.setCustomerShortName("SEED");
			item.setForClient("Zensar");
			item.setTrainingDuration(2);
			item.setTrainingPlatform("AWS");

			Set<String> locations = new HashSet<String>(Arrays.asList("Pune"));
			item.setTrainingLocations(locations);
			Set<String> topics = new HashSet<String>(Arrays.asList("Developer Focus"));
			item.setTrainingTopics(topics);

			mapper.save(item);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static void initLocal() {

		client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
				new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-east-1")).build();

	}

	private static void createTable() {
		try {
			CreateTableRequest request = new CreateTableRequest()
					.withAttributeDefinitions(new AttributeDefinition("CustomerShortName", ScalarAttributeType.S),
							new AttributeDefinition("ForClient", ScalarAttributeType.S))
					.withKeySchema(new KeySchemaElement("CustomerShortName", KeyType.HASH),
							new KeySchemaElement("ForClient", KeyType.RANGE))
					.withProvisionedThroughput(new ProvisionedThroughput(new Long(10), new Long(10)));

			request.setTableName("Training");

			client.createTable(request);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	static void getItem() {

		try {

			DynamoDBMapper mapper = new DynamoDBMapper(client);

			String hashKey = "SEED";
			String rangeKey = "Zensar";
			TrainingItem item = mapper.load(TrainingItem.class, hashKey, rangeKey);

			
		} catch (Exception e) {
			System.err.println("Unable to retrieve data: ");
			System.err.println(e.getMessage());
		}

	}

	private static void updateItem() {
		try {
			DynamoDBMapper mapper = new DynamoDBMapper(client);
			TrainingItem item = new TrainingItem();

			item.setCustomerShortName("SEED");
			item.setForClient("Zensar");
			item.setTrainingDuration(20);
			item.setTrainingPlatform("AWS and Azure");

			Set<String> locations = new HashSet<String>(Arrays.asList("Pune"));
			item.setTrainingLocations(locations);
			Set<String> topics = new HashSet<String>(Arrays.asList("Developer Focus"));
			item.setTrainingTopics(topics);

			mapper.save(item);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static void scanTable() {
		try {
			DynamoDBMapper mapper = new DynamoDBMapper(client);

			DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();

			PaginatedScanList<TrainingItem> list = mapper.scan(TrainingItem.class, scanExpression);

			if (list != null) {
				Iterator<TrainingItem> itemIterator = list.iterator();

				while (true == itemIterator.hasNext()) {

					System.out.println("Scan Output" + itemIterator.next());
				}

			}

			Condition condition = new Condition();

			AttributeValue forClientAttributeValue = new AttributeValue();
			forClientAttributeValue.withS("Zensar");
			condition.withAttributeValueList(forClientAttributeValue);
			condition.setComparisonOperator("EQ");
			// Using ScanExpression
			// Should this be attributeName of Class or actual attributename
			// from
			// dynamodb
			// This must be actual TABLE NAME - what a sorry state of affairs
			// scanExpression.addFilterCondition("ForClient", condition);
			// add one more condition
			AttributeValue trainingPlatformAttributeValue = new AttributeValue();
			trainingPlatformAttributeValue.withS("AWS and Azure");
			Condition condition2 = new Condition();
			condition2.withAttributeValueList(trainingPlatformAttributeValue);
			condition2.setComparisonOperator("EQ");

			scanExpression.addFilterCondition("TrainingPlatform", condition2);

			list = mapper.scan(TrainingItem.class, scanExpression);

			if (list != null) {
				Iterator<TrainingItem> itemIterator = list.iterator();

				while (true == itemIterator.hasNext()) {

					System.out.println("Filtered Output" + itemIterator.next());
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static void queryTable() {
		try {
			DynamoDBMapper mapper = new DynamoDBMapper(client);

			DynamoDBQueryExpression<TrainingItem> queryExpression = new DynamoDBQueryExpression<TrainingItem>();

			TrainingItem hashKeyValues = new TrainingItem();
			hashKeyValues.setCustomerShortName("SEED");

			// queryExpression.withConditionalOperator("EQ");
			queryExpression.withHashKeyValues(hashKeyValues);

			Condition rangeKeyCondition = new Condition();
			rangeKeyCondition.setComparisonOperator("EQ");
			rangeKeyCondition.withAttributeValueList(new AttributeValue().withS("Zensar"));

			// queryExpression.withRangeKeyCondition("ForClient",
			// rangeKeyCondition);

			// queryExpression.addExpressionAttributeNamesEntry("CustomerShortName","SEED");

			// queryExpression.withFilterExpression(filterExpression)

			PaginatedQueryList<TrainingItem> list = mapper.query(TrainingItem.class, queryExpression);

			// get the count of items which matched the query
			int count = mapper.count(TrainingItem.class, queryExpression);

			System.out.println("Count of items " + count);

			if (list != null) {
				Iterator<TrainingItem> itemIterator = list.iterator();

				while (true == itemIterator.hasNext()) {

					System.out.println("Query Output " + itemIterator.next());
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static void queryUsingIndex() {
	
		try {
			DynamoDBMapper mapper = new DynamoDBMapper(client);
	
			DynamoDBQueryExpression<TrainingItem> queryExpression = new DynamoDBQueryExpression<TrainingItem>();
	
			TrainingItem hashKeyValues = new TrainingItem();
			hashKeyValues.setTrainingPlatform("AWS");
			//hashKeyValues.setTrainingPlatform("AWS and Azure");
	
			queryExpression.withHashKeyValues(hashKeyValues);
			Condition rangeKeyCondition = new Condition();
			rangeKeyCondition.setComparisonOperator("EQ");
			rangeKeyCondition.withAttributeValueList(new AttributeValue().withS("AWS"));
	
			// GSI is always eventual consistent read  			
			queryExpression.withConsistentRead(false);
			queryExpression.withIndexName("trainingplatformgsiindex");
		
			PaginatedQueryList<TrainingItem> list = mapper.query(TrainingItem.class, queryExpression);
	
			// get the count of items which matched the query
			int count = mapper.count(TrainingItem.class, queryExpression);
	
			System.out.println("Count of items " + count);
	
			if (list != null) {
				Iterator<TrainingItem> itemIterator = list.iterator();
	
				while (true == itemIterator.hasNext()) {
	
					System.out.println("Index Output " + itemIterator.next());
				}
			}
	
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
