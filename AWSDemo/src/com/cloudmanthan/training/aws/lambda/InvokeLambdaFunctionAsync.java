package com.cloudmanthan.training.aws.lambda;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaAsync;
import com.amazonaws.services.lambda.AWSLambdaAsyncClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

public class InvokeLambdaFunctionAsync {
	public static void main(String[] args) {
		String function_name = "CustomFunction" ;//"HelloFunction";
		String function_input = "{\"who\":\"AWS SDK for Java\"}";
		//String function_input = "{\amod\}";

		//AWSLambdaAsync lambda = AWSLambdaAsyncClientBuilder.defaultClient();

		AWSLambdaAsync lambda = AWSLambdaAsyncClientBuilder.standard().withRegion(Regions.US_EAST_2).build();

		InvokeRequest req = new InvokeRequest().withFunctionName(function_name)
				.withPayload(ByteBuffer.wrap(function_input.getBytes()));

		Future<InvokeResult> future_res = lambda.invokeAsync(req);

		System.out.print("Waiting for future");
		while (future_res.isDone() == false) {
			System.out.print(".");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.err.println("\nThread.sleep() was interrupted!");
				System.exit(1);
			}
		}

		try {
			InvokeResult res = future_res.get();
			if (res.getStatusCode() == 200) {
				System.out.println("\nLambda function returned:");
				ByteBuffer response_payload = res.getPayload();
				System.out.println(new String(response_payload.array()));
			} else {
				System.out.format("Received a non-OK response from AWS: %d\n", res.getStatusCode());
			}
		} catch (InterruptedException | ExecutionException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}

		System.exit(0);
	}
}