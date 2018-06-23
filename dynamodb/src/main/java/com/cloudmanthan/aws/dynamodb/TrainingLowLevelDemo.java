package com.cloudmanthan.aws.dynamodb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
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

public class TrainingLowLevelDemo {

	static AmazonDynamoDB client;

	public static void main(String[] args) {

		// AmazonDynamoDB client =
		// AmazonDynamoDBClientBuilder.standard().build();
		initLocal();
		//createTable();
		//putItem();
		getItem();
		//updateItem();
		//getItem();
		/*
		 * HashMap<String, AttributeValue> key = new HashMap<String,
		 * AttributeValue>(); key.put("Artist", new
		 * AttributeValue().withS("No One You Know")); key.put("SongTitle", new
		 * AttributeValue().withS("Call Me Today"));
		 * 
		 * GetItemRequest request = new GetItemRequest() .withTableName("Music")
		 * .withKey(key);
		 * 
		 * try { GetItemResult result = client.getItem(request); if (result !=
		 * null){ if (result.getItem().isEmpty() == false) { AttributeValue year
		 * = result.getItem().get("Year");
		 * System.out.println("The song was released in " + year.getN()); } else
		 * { System.out.println("No matching song was found"); } } }catch
		 * (Exception e) { System.err.println("Unable to retrieve data: ");
		 * System.err.println(e.getMessage()); }
		 */
	}

	private static void putItem() {
		try {

			// Create an item to be added to table

			Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
			// specify composite primary key
			item.put("CustomerShortName", new AttributeValue("SEED"));
			item.put("ForClient", new AttributeValue("John Deere"));
			// add other attributes and values
			// TrainingPlatform - String type
			item.put("TrainingPlatform", new AttributeValue().withS("AWS"));
			// Multiple Topics can be covered
			List<String> topicList = new ArrayList<String>();
			topicList.add("Custom-Developer-Focus-AWS");
			item.put("TrainingTopics", new AttributeValue().withSS(topicList));
			// Multiple Locations may be required
			List<String> locationList = new ArrayList<String>();
			locationList.add("Pune");
			item.put("TrainingLocations", new AttributeValue().withSS(locationList));
			// Duration is a number
			item.put("TrainingDuration", new AttributeValue().withN("3"));
			// Won - Boolean
			item.put("Won", new AttributeValue().withBOOL(true));

			PutItemRequest putItemRequest = new PutItemRequest().withTableName("Training").withItem(item);
			PutItemResult putItemResult = client.putItem(putItemRequest);
			System.out.println("Result: " + putItemResult);

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
		HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
		key.put("CustomerShortName", new AttributeValue("SEED"));
		//key.put("ForClient", new AttributeValue("John Deere"));
		key.put("ForClient", new AttributeValue("Zensar"));

		GetItemRequest request = new GetItemRequest().withTableName("Training").withKey(key);

		try {
			GetItemResult result = client.getItem(request);
			if (result != null) {
				if (result.getItem().isEmpty() == false) {
					
					  AttributeValue trainingDuration =
					  result.getItem().get("TrainingDuration");
					  System.out.println("The training duration is " +
					  trainingDuration.getN());
					  
					  AttributeValue trainingPlatform =
					  result.getItem().get("TrainingPlatform");
					  System.out.println("The trainingPlatform  is " +
					  trainingPlatform.getS());
					  
					  AttributeValue topicList =
					  result.getItem().get("TrainingTopics");
					  System.out.println("The topicList  is " +
					  topicList.getSS().toString());
					 				 
					  AttributeValue trainingLocations =
					  result.getItem().get("TrainingLocations");
					  System.out.println("The training locations are " +
					  trainingLocations.getSS().toString());
					  
					  //BOOLEAN example 
					  AttributeValue won = result.getItem().get("Won"); System.out.println("Won " 	+  won.getBOOL()); 

					 
					AttributeValue trainingDeliveryMode = result.getItem().get("TrainingDeliveryMode");
					System.out.println("trainingDeliveryMode " + trainingDeliveryMode.getS());

				}
			} else {
				System.out.println("No matching record  was found");
			}
		} catch (Exception e) {
			System.err.println("Unable to retrieve data: ");
			System.err.println(e.getMessage());
		}

	}

	private static void updateItem() {
		try {

			// Create an item to be added to table

			Map<String, AttributeValue> itemKey = new HashMap<String, AttributeValue>();
			// specify composite primary key
			itemKey.put("CustomerShortName", new AttributeValue("SEED"));
			itemKey.put("ForClient", new AttributeValue("John Deere"));
			// add other attributes and values
						
			// Create a new attribute map which should be added 
								
			Map<String,AttributeValueUpdate> attributeUpdates = new HashMap<String, AttributeValueUpdate>();
			
			AttributeValue trainingDeliveryMode = new AttributeValue().withS("ClassRoom");
			
			AttributeValueUpdate updateAttribute = new AttributeValueUpdate().withAction(AttributeAction.PUT).withValue(trainingDeliveryMode);
  			
			attributeUpdates.put("TrainingDeliveryMode", updateAttribute);
			
			UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName("Training").withKey(itemKey).withAttributeUpdates(attributeUpdates);
			UpdateItemResult updateItemResult = client.updateItem(updateItemRequest);
			System.out.println("Result: " + updateItemResult);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
