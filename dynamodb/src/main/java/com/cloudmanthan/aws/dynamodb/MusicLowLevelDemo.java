package com.cloudmanthan.aws.dynamodb;

import java.util.HashMap;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;

public class MusicLowLevelDemo {
	 public static void main(String[] args) {

	        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();

	        HashMap<String, AttributeValue> key = new HashMap<String, AttributeValue>();
	        key.put("Artist", new AttributeValue().withS("No One You Know"));
	        key.put("SongTitle", new AttributeValue().withS("Call Me Today"));

	        GetItemRequest request = new GetItemRequest()
	            .withTableName("Music")
	            .withKey(key);

	        try {
	            GetItemResult result = client.getItem(request);
	            if (result != null){
	            if (result.getItem().isEmpty() == false)
	             {
	                AttributeValue year = result.getItem().get("Year");
	                System.out.println("The song was released in " + year.getN());
	            } else {
	                System.out.println("No matching song was found");
	            }
	        }
	        }catch (Exception e) {
	            System.err.println("Unable to retrieve data: ");
	            System.err.println(e.getMessage());
	        }
	    }
}
