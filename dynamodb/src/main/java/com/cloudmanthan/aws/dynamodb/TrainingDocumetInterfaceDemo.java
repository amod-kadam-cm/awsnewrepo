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
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.GetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateGlobalSecondaryIndexAction;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;

public class TrainingDocumetInterfaceDemo {

	static AmazonDynamoDB client;
	static DynamoDB docClient;

	public static void main(String[] args) {

		initLocal();

		// CreateTable
		// createTable();
		// create global secondary index
		// createGSI();
		createLSI();
	}

	private static void createLSI() {
		docClient = new DynamoDB(client);
		try {
			// LSI can be created only during table creation
			List<KeySchemaElement> keySchemaList = new ArrayList<KeySchemaElement>();
			// Create Partition Key
			KeySchemaElement partitionKeyElement = new KeySchemaElement();
			partitionKeyElement.withKeyType(KeyType.HASH).withAttributeName("CustomerShortName");

			keySchemaList.add(partitionKeyElement);

			// Create Sort Key
			KeySchemaElement sortKeyElement = new KeySchemaElement();
			sortKeyElement.withKeyType(KeyType.RANGE).withAttributeName("ForClient");

			keySchemaList.add(sortKeyElement);

			// Setup AttributeDefinition
			List<AttributeDefinition> attributeDefintions = new ArrayList<AttributeDefinition>();

			AttributeDefinition customerShortName = new AttributeDefinition().withAttributeName("CustomerShortName")
					.withAttributeType(ScalarAttributeType.S);

			attributeDefintions.add(customerShortName);

			AttributeDefinition forClient = new AttributeDefinition().withAttributeName("ForClient")
					.withAttributeType(ScalarAttributeType.S);

			attributeDefintions.add(forClient);
			
			AttributeDefinition trainingDuration = new AttributeDefinition().withAttributeName("TrainingDuration")
					.withAttributeType(ScalarAttributeType.N);

			attributeDefintions.add(trainingDuration);

			ProvisionedThroughput pvThroughPut = new ProvisionedThroughput().withReadCapacityUnits(10L)
					.withWriteCapacityUnits(10L);

			
			CreateTableRequest req = new CreateTableRequest();
			

			// Create LSI

			// first create key schema - set Partition Key

			KeySchemaElement indexPartitionKey = new KeySchemaElement().withKeyType(KeyType.HASH)
					.withAttributeName("CustomerShortName");

			// now set the sort key for index
			KeySchemaElement indexSortKey = new KeySchemaElement().withKeyType(KeyType.RANGE)
					.withAttributeName("TrainingDuration");

			List<KeySchemaElement> indexkeySchemaList = new ArrayList<KeySchemaElement>();
			indexkeySchemaList.add(indexPartitionKey);
			indexkeySchemaList.add(indexSortKey);

			// set the projection attributes
			Projection indexProject = new Projection().withProjectionType(ProjectionType.KEYS_ONLY);

			// Create Local Secondary Index
			LocalSecondaryIndex lsi = new LocalSecondaryIndex().withIndexName("trainingdurationlsi")
					.withKeySchema(indexkeySchemaList).withProjection(indexProject);

			req.withTableName("TrainingLSI")
			.withProvisionedThroughput(pvThroughPut)
			.withKeySchema(keySchemaList)
			.withAttributeDefinitions(attributeDefintions)
					.withLocalSecondaryIndexes(lsi);

			Table trainintLsiTable = docClient.createTable(req);

			
			 Item item = new Item().withString("CustomerShortName", "SEED")
					 .withString("ForClient","Zensar")			 		
					 .withString("TrainingPlatform", "AWS")
			 .withStringSet("TrainingTopics", "Developer")
			 .withStringSet("TrainingLocations", "Pune")
			 .withNumber("TrainingDuration", 3).withBoolean("Won",true);
			 
			 trainintLsiTable.putItem(item);

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
			docClient = new DynamoDB(client);

			Table table = docClient.getTable("Music");
			GetItemOutcome outcome = table.getItemOutcome("Artist", "No One You Know", "SongTitle", "Call Me Today");

			int year = outcome.getItem().getInt("Year");
			System.out.println("The song was released in " + year);

			// Setup KeySchema

			List<KeySchemaElement> keySchemaList = new ArrayList<KeySchemaElement>();

			KeySchemaElement keyElement = new KeySchemaElement();

			keyElement.withKeyType(KeyType.HASH).withAttributeName("FirstName");

			keySchemaList.add(keyElement);

			// Setup AttributeDefinition
			List<AttributeDefinition> attributeDefintions = new ArrayList<AttributeDefinition>();

			AttributeDefinition firstName = new AttributeDefinition().withAttributeName("FirstName")
					.withAttributeType(ScalarAttributeType.S);

			attributeDefintions.add(firstName);

			ProvisionedThroughput pvThroughPut = new ProvisionedThroughput().withReadCapacityUnits(10L)
					.withWriteCapacityUnits(10L);

			Table empTable = docClient.createTable("Employee2", keySchemaList, attributeDefintions, pvThroughPut);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private static void createGSI() {
		docClient = new DynamoDB(client);
		Table trainingTable = docClient.getTable("Training");
		// now create GSI on trainingPlatform

		KeySchemaElement keySchema = new KeySchemaElement().withAttributeName("TrainingPlatform")
				.withKeyType(KeyType.HASH);

		Projection projection = new Projection();
		projection.setProjectionType(ProjectionType.KEYS_ONLY);
		ProvisionedThroughput pvThroughPut = new ProvisionedThroughput().withReadCapacityUnits(10L)
				.withWriteCapacityUnits(10L);

		CreateGlobalSecondaryIndexAction trainingPlatformGSIAction = new CreateGlobalSecondaryIndexAction()
				.withIndexName("trainingplatformgsiindex").withKeySchema(keySchema).withProjection(projection)
				.withProvisionedThroughput(pvThroughPut);

		// add attribute defintions
		// Setup AttributeDefinition

		AttributeDefinition hashKeyDefinition = new AttributeDefinition().withAttributeName("TrainingPlatform")
				.withAttributeType(ScalarAttributeType.S);

		// hashKeyDefinition.add(firstName);

		trainingTable.createGSI(trainingPlatformGSIAction, hashKeyDefinition);

	}

}
