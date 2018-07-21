package com.amazonaws.lambda.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class DynamoDBHandler implements RequestStreamHandler {
	JSONParser parser = new JSONParser();
	AmazonDynamoDB client;
	DynamoDBMapper mapper;
	TrainingItemDAO trainingItemDAO = new TrainingItemDAO();

	public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {

		LambdaLogger logger = context.getLogger();
		logger.log("Loading Java Lambda handler of ProxyWithStream");

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		JSONObject responseJson = new JSONObject();
		String name = "you";
		String customerShortName = null;
		String city = "World";
		String time = "day";
		String day = null;
		String responseCode = "200";

		String platform;
		String topic;
		String forclient = null;
		Integer duration;
		Boolean won;
		String location;
		String message = null;

		String httpMethod = null;
		String output = null;
		try {
			JSONObject event = (JSONObject) parser.parse(reader);

			JSONObject responseBody = new JSONObject();
			responseBody.put("input", event.toJSONString());

			if (event.get("httpMethod") != null) {

				httpMethod = (String) event.get("httpMethod");
				logger.log("httpMethod is " + httpMethod);
			}
			
			if (event.get("queryStringParameters") != null) {
				JSONObject qps = (JSONObject) event.get("queryStringParameters");
				if (qps.get("CustomerShortName") != null) {
					customerShortName = (String) qps.get("CustomerShortName");
				}
				
				if (qps.get("forClient") != null) {
					forclient = (String) qps.get("forClient");
				}
				// if it is GET 
				if (httpMethod.equalsIgnoreCase("get") == true) {

					List<TrainingItem> trainingItemList = trainingItemDAO.query(logger, customerShortName, forclient);
					for (TrainingItem item : trainingItemList) {
						message = message + item.toString();
					}
					responseBody.put("message", message);
				}

			} else {
				if (httpMethod.equalsIgnoreCase("get") == true) {

					List<TrainingItem> trainingItemList = trainingItemDAO.scan(logger);
					for (TrainingItem item : trainingItemList) {
						message = message + item.toString();
					}
					responseBody.put("message", message);
								}
			}

			if (event.get("pathParameters") != null) {
				JSONObject pps = (JSONObject) event.get("pathParameters");
				if (pps.get("proxy") != null) {
					city = (String) pps.get("proxy");
				}
			}

			if (event.get("headers") != null) {
				JSONObject hps = (JSONObject) event.get("headers");
				if (hps.get("day") != null) {
					day = (String) hps.get("day");
				}
			}

			if (event.get("body") != null) {
				TrainingItem trainingItem = new TrainingItem();
				JSONObject body = (JSONObject) parser.parse((String) event.get("body"));

				if (body.get("CustomerShortName") != null) {
					customerShortName = (String) body.get("CustomerShortName");
					trainingItem.setCustomerShortName(customerShortName);
				}

				if (body.get("forClient") != null) {
					forclient = (String) body.get("forClient");
					trainingItem.setForClient(forclient);
				}

				if (body.get("time") != null) {
					time = (String) body.get("time");
				}
				if (body.get("platform") != null) {
					platform = (String) body.get("platform");
					trainingItem.setTrainingPlatform(platform);
				}
				if (body.get("topic") != null) {
					topic = (String) body.get("topic");

					Set<String> topics = new HashSet<String>(Arrays.asList(topic));
					trainingItem.setTrainingTopics(topics);

				}
				if (body.get("location") != null) {
					location = (String) body.get("location");

					Set<String> locations = new HashSet<String>(Arrays.asList(location));
					trainingItem.setTrainingLocations(locations);

				}

				if (body.get("duration") != null) {
					String durationString = (String) body.get("duration");
					trainingItem.setTrainingDuration(new Integer(durationString));
				}
				if (body.get("won") != null) {
					won = (boolean) body.get("won");
					trainingItem.setWon(won);
				}

				initMapper();

				if (httpMethod.equalsIgnoreCase("post") == true) {
					mapper.save(trainingItem);
				} else {
				
						if (httpMethod.equalsIgnoreCase("put") == true) {
							mapper.save(trainingItem);
							logger.log("UPDATED DATA " + trainingItem);
						} else {
							if (httpMethod.equalsIgnoreCase("delete") == true) {
								mapper.delete(trainingItem);
								logger.log("Deleted  DATA " + trainingItem);
							}
						}
					}
				}
			

			JSONObject headerJson = new JSONObject();
			headerJson.put("x-custom-header", "my custom header value");
			responseJson.put("isBase64Encoded", false);
			responseJson.put("statusCode", responseCode);
			responseJson.put("headers", headerJson);
			responseJson.put("body", responseBody.toString());

		} catch (ParseException pex) {
			responseJson.put("statusCode", "400");
			responseJson.put("exception", pex);
		}

		logger.log(responseJson.toJSONString());
		OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
		writer.write(responseJson.toJSONString());
		writer.close();
	}

	private void initMapper() {

		client = AmazonDynamoDBClientBuilder.defaultClient();
		mapper = new DynamoDBMapper(client);

	}

}