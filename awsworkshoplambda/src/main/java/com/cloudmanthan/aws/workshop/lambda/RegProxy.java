package com.cloudmanthan.aws.workshop.lambda;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cloudmanthan.aws.workshop.lambda.dao.RegisterDAO;
import com.cloudmanthan.aws.workshop.lambda.model.RegistrationInfo;

public class RegProxy implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

        LambdaLogger logger = context.getLogger();
        logger.log("Loading Java Lambda handler of Proxy");

        JSONParser parser = new JSONParser();
    
        String name = "you";
        String city = "World";
        String firstname = "default";
        String lastname = "default";
        String day = null;

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            Map<String, String> qps = event.getQueryStringParameters();
            if (qps != null) {
                if (qps.get("firstname") != null) {
                	firstname = qps.get("firstname");
                }
                if (qps.get("lastname") != null) {
                	lastname = qps.get("lastname");
                }
            }

            Map<String, String> hps = event.getHeaders();
            if (hps != null) {
                day = hps.get("Day");
            }

            String bodyStr = event.getBody();
            if (bodyStr != null) {
                JSONObject body;

                body = (JSONObject) parser.parse(bodyStr);

                if (body.get("firstname") != null) {
                	firstname = (String) body.get("firstname");
                }
                
                if (body.get("lastname") != null) {
                	lastname = (String) body.get("lastname");
                }
            }

            String greeting = "Good " + firstname + ", " + name + " of " + city + ".";
            if (day != null && day != "")
                greeting += " Happy " + day + "!";

            // now insert data into DB
            RegisterDAO regDAO = new RegisterDAO();
            RegistrationInfo regInfo = new RegistrationInfo();
            regInfo.setFirstName(firstname);
            regInfo.setLastName(lastname);
			regDAO.register(regInfo );
            
            response.setHeaders(Collections.singletonMap("x-custom-header", "my custom header value"));
            response.setStatusCode(200);

            Map<String, String> responseBody = new HashMap<String, String>();
            responseBody.put("input", event.toString());
            responseBody.put("message", greeting);
            String responseBodyString = new JSONObject(responseBody).toJSONString();

            response.setBody(responseBodyString);

        } catch (ParseException pex) {
            response.setStatusCode(400);

            Map<String, String> responseBody = Collections.singletonMap("message", pex.toString());
            String responseBodyString = new JSONObject(responseBody).toJSONString();
            response.setBody(responseBodyString);
        }

        logger.log(response.toString());
        return response;
    }
}