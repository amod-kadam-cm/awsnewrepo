package com.cloudmanthan.aws.awsworkshop.dynamodb;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Page;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableCollection;
import com.amazonaws.services.dynamodbv2.document.internal.PageIterable;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;

public class DynamoDBMapperDemo {

	static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
			.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
			.build();
	static DynamoDBMapper mapper = new DynamoDBMapper(client);

	public static void main(String[] args) {

		String tableName = "ProductCatalog";

		if (false == doesTableExists(tableName)) {
			createTable();

		}
		putItem();
		readItem();
	}

	private static boolean doesTableExists(String ptableName) {

		boolean isTablePresent = false;
		DynamoDB docClient = new DynamoDB(client);

		System.out.println("Insided doesTableExists");

		TableCollection<ListTablesResult> tableCollection = docClient.listTables();

		Page<Table, ListTablesResult> firstPage = tableCollection.firstPage();

		ListTablesResult lowLevelResult = firstPage.getLowLevelResult();

		List<String> tableNames = lowLevelResult.getTableNames();

		Iterator<String> tableName = tableNames.iterator();

		while (tableName.hasNext()) {

			String currentTableName = tableName.next();
			if (true == currentTableName.equalsIgnoreCase(ptableName)) {
				System.out.print("match found");
				isTablePresent = true;
			}

		}

		return isTablePresent;
	}

	private static void readItem() {
		Integer hashKey = 102;
		CatalogItem obj = mapper.load(CatalogItem.class, hashKey);
		System.out.println("ISBN" + obj.getISBN());
		System.out.println("Value is" + obj);
	}

	private static void putItem() {
		CatalogItem item = new CatalogItem();
		item.setId(102);
		item.setTitle("Book 102 Title");
		item.setISBN("222-2222222222");
		item.setBookAuthors(new HashSet<String>(Arrays.asList("Author 1", "Author 2")));
		item.setSomeProp("Test");
		item.setVersion(1L);

		mapper.save(item);

	}

	public static void createTable() {

		CreateTableRequest req = mapper.generateCreateTableRequest(CatalogItem.class);
		// Table provision throughput is still required since it cannot be specified in
		// your POJO
		req.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L));
		// Fire off the CreateTableRequest using the low-level client
		client.createTable(req);

	}

}
