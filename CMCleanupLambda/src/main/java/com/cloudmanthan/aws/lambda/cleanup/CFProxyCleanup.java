package com.cloudmanthan.aws.lambda.cleanup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.cloudmanthan.aws.cleanup.CloudFormationCleanupService;

public class CFProxyCleanup implements RequestStreamHandler {

	JSONParser parser = new JSONParser();

	LambdaLogger logger ;

	private String httpMethod;


	@Override
	public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
		

		logger = context.getLogger();

		logger.log("Loading Java Lambda handler of ProxyWithStream");

		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		JSONObject responseJson = new JSONObject();
		String name = "you";
		String city = "World";
		String time = "day";
		String day = null;
		String responseCode = "200";

		try {
			JSONObject event = (JSONObject) parser.parse(reader);
			
			if (event.get("httpMethod") != null) {

				httpMethod = (String) event.get("httpMethod");
				logger.log("httpMethod is " + httpMethod);
			}
			

			if (event.get("queryStringParameters") != null) {
				JSONObject qps = (JSONObject) event.get("queryStringParameters");
				if (qps.get("name") != null) {
					name = (String) qps.get("name");
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
				JSONObject body = (JSONObject) parser.parse((String) event.get("body"));
				if (body.get("time") != null) {
					time = (String) body.get("time");
				}
				exctractBodyVariables(body);
			}

			String greeting = "Good " + time + ", " + name + " of " + city + ". ";
			if (day != null && day != "")
				greeting += "Happy " + day + "!";

			JSONObject responseBody = new JSONObject();
			responseBody.put("input", event.toJSONString());
			responseBody.put("message", greeting);

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
		OutputStreamWriter writer = new OutputStreamWriter(output, "UTF-8");
		writer.write(responseJson.toJSONString());
		writer.close();

	}

	private void exctractBodyVariables(JSONObject body) {
		
		String WORKSHOP_START_DATE = null ;
		String WORKSHOP_END_DATE = null ;
		
		if (body.get("WORKSHOP_START_DATE") != null) {
			 WORKSHOP_START_DATE = (String) body.get("WORKSHOP_START_DATE");
		}
		
		// WORKSHOP_END_DATE
		if (body.get("WORKSHOP_END_DATE") != null) {
			 WORKSHOP_END_DATE = (String) body.get("WORKSHOP_END_DATE");
		}
		
		logger.log("WORKSHOP_START_DATE " + WORKSHOP_START_DATE );
		
		logger.log("WORKSHOP_END_DATE " + WORKSHOP_END_DATE );
		
		CloudFormationCleanupService.startCleanup(logger, WORKSHOP_START_DATE, WORKSHOP_END_DATE);
		
		
	}

}
