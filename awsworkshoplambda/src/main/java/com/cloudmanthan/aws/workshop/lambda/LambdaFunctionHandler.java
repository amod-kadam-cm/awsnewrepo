package com.cloudmanthan.aws.workshop.lambda;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.cloudmanthan.aws.workshop.lambda.dao.RegisterDAO;
import com.cloudmanthan.aws.workshop.lambda.model.RegistrationInfo;

public class LambdaFunctionHandler implements RequestHandler<Object, String> {

	@Override
	public String handleRequest(Object input, Context context) {

		context.getLogger().log("Input: " + input);

		try {
			JSONParser jsonParser = new JSONParser();

			JSONObject obj;

			obj = (JSONObject) jsonParser.parse(input.toString());

			String firstname = (String) obj.get("firstname");
			String lastname = (String) obj.get("lastname");

			context.getLogger().log("Input: " + input + "firstname : " + firstname + "lastname: " + lastname);
			
			// now invoke out db code to insert record into the database
			
			RegisterDAO dao = new RegisterDAO();
			
			RegistrationInfo regInfo = new RegistrationInfo();
			
			regInfo.setFirstName(firstname);
			
			regInfo.setLastName(lastname);
	
			dao.register(regInfo);
			

		} catch (ParseException e) {

			e.printStackTrace();
			context.getLogger().log("exception: " + e);

		}

		// TODO: input values will be in JSON
		// Now parse the JSON to get the appropriate key
		/*
		 * { "firstname":"amod-lambda", "lastname" : "kadam-lambda" }
		 */
		//
		return "Hello from Lambda!";
	}

}
