package com.cloudmanthan.aws.workshop.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class WarmContainerTest implements RequestHandler<Object, String> {

	int i = 0;
    @Override
    public String handleRequest(Object input, Context context) {
        context.getLogger().log("Input: " + input);

        i++;
        context.getLogger().log("I got invoked : " + i);

        // TODO: implement your handler
        return "Hello from Lambda!";
    }

}
