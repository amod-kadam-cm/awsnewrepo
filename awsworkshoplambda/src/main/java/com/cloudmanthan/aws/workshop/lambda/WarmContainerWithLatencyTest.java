package com.cloudmanthan.aws.workshop.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class WarmContainerWithLatencyTest implements RequestHandler<Object, String> {

	int i = 0;
    @Override
    public String handleRequest(Object input, Context context) {
    	
    	try {
    		// Sleep for 5 seconds
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	context.getLogger().log("Input: " + input);
        
       
        i++;
        context.getLogger().log("I got invoked : " + i);

        // TODO: implement your handler
        return "Hello from Lambda!";
    }

}
