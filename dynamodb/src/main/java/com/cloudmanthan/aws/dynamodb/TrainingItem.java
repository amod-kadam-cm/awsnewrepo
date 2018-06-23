package com.cloudmanthan.aws.dynamodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;

@DynamoDBTable(tableName="TrainingLSI")
public class TrainingItem {
	  
    
    @Override
	public String toString() {
		return "TrainingItem [customerShortName=" + customerShortName + ", forClient=" + forClient
				+ ", trainingPlatform=" + trainingPlatform + ", trainingTopics=" + trainingTopics
				+ ", trainingLocations=" + trainingLocations + ", trainingDuration=" + trainingDuration + ", won=" + won
				+ ", someProp=" + someProp + "]";
	}
	private String customerShortName;
    
    @DynamoDBHashKey(attributeName="CustomerShortName")
    public String getCustomerShortName() {
		return customerShortName;
	}
	public void setCustomerShortName(String customerShortName) {
		this.customerShortName = customerShortName;
	}
	
	@DynamoDBRangeKey(attributeName="ForClient")
	public String getForClient() {
		return forClient;
	}
	public void setForClient(String forClient) {
		this.forClient = forClient;
	}
	
	@DynamoDBAttribute(attributeName="TrainingPlatform")
	@DynamoDBIndexHashKey(globalSecondaryIndexName="trainingplatformgsiindex")
	public String getTrainingPlatform() {
		return trainingPlatform;
	}
	public void setTrainingPlatform(String trainingPlatform) {
		this.trainingPlatform = trainingPlatform;
	}
	@DynamoDBAttribute(attributeName="TrainingTopics")
	public Set<String> getTrainingTopics() {
		return trainingTopics;
	}
	public void setTrainingTopics(Set<String> trainingTopics) {
		this.trainingTopics = trainingTopics;
	}
	@DynamoDBAttribute(attributeName="TrainingLocations")
	public Set<String> getTrainingLocations() {
		return trainingLocations;
	}
	public void setTrainingLocations(Set<String> trainingLocations) {
		this.trainingLocations = trainingLocations;
	}
	@DynamoDBAttribute(attributeName="TrainingDuration")
	@DynamoDBIndexRangeKey(localSecondaryIndexName="trainingdurationlsi")
	public Integer getTrainingDuration() {
		return trainingDuration;
	}
	public void setTrainingDuration(Integer trainingDuration) {
		this.trainingDuration = trainingDuration;
	}
	@DynamoDBAttribute(attributeName="Won")
	public Boolean getWon() {
		return won;
	}
	public void setWon(Boolean won) {
		this.won = won;
	}
	private String forClient;
    private String trainingPlatform; //AWS
    private Set<String> trainingTopics; //Custom-Developer-Focus-AWS
    private Set<String> trainingLocations; //Pune
    private Integer trainingDuration; //3 
    private Boolean won ; // true
	private String someProp;
      
    
    @DynamoDBIgnore
    public String getSomeProp() { String someProp = null;
	return someProp;}
    public void setSomeProp(String someProp) {this.someProp = someProp;}
}