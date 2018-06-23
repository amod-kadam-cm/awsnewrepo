package com.cloudmanthan.training.aws.lambda;
import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.AWSLambdaAsyncClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.regions.Regions;

import java.nio.ByteBuffer;
import java.util.concurrent.Future;

public class InvokeLambdaFunctionCallback
{
    public class AsyncLambdaHandler implements AsyncHandler<InvokeRequest, InvokeResult>
    {
        public void onSuccess(InvokeRequest req, InvokeResult res) {
            System.out.println("\nLambda function returned:");
            ByteBuffer response_payload = res.getPayload();
            System.out.println(new String(response_payload.array()));
            System.exit(0);
        }

        public void onError(Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args)
    {
    	InvokeLambdaFunctionCallback callbackInstance = new InvokeLambdaFunctionCallback();
        String function_name = "CustomFunction" ; //"HelloFunction";
        String function_input = "{\"who\":\"AWS SDK for Java\"}";

        //AWSLambdaAsync lambda = AWSLambdaAsyncClientBuilder.defaultClient();
        
        AWSLambdaAsync lambda = AWSLambdaAsyncClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
        
        InvokeRequest req = new InvokeRequest()
            .withFunctionName(function_name)
            .withPayload(ByteBuffer.wrap(function_input.getBytes()));
        //AsyncLambdaHandler asyncHandler =  new AsyncLambdaHandler();
        Future<InvokeResult> future_res = lambda.invokeAsync(req,  callbackInstance.new AsyncLambdaHandler() );

        System.out.print("Waiting for async callback");
        while (!future_res.isDone() && !future_res.isCancelled()) {
            // perform some other tasks...
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                System.err.println("Thread.sleep() was interrupted!");
                System.exit(0);
            }
            System.out.print(".");
        }
    }
}
