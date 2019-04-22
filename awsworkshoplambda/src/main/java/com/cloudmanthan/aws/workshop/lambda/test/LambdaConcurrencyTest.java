package com.cloudmanthan.aws.workshop.lambda.test;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvocationType;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

public class LambdaConcurrencyTest {

	public static void main(String[] args) {
		String functionName = "arn:aws:lambda:ap-south-1:123456789012:function:WarmContainerWithLatencyTest";

		AWSLambda client = AWSLambdaClientBuilder.standard().build();


		// Asynchronous Execution
		for (int i = 0; i < 15; i++) {
			InvokeRequest request = new InvokeRequest().withFunctionName(functionName)
					.withInvocationType(InvocationType.Event).withLogType("Tail")
					.withPayload("{\n" + "  \"key1\": \"value1\",\n" + "  \"key2\": \"value2\",\n"
							+ "  \"key3\": \"value3\"\n" + "}");

			InvokeResult response = client.invoke(request);
			int STATUS_CODE = response.getStatusCode();

			switch (STATUS_CODE) {
			case 200:
				System.out.println(
						"Successfully executed with Status Code " + STATUS_CODE + " Value of counter is :" + i);
				break;
			case 202:
				System.out.println(
						"Successfully executed with Status Code " + STATUS_CODE + " Value of counter is :" + i);
				break;

			default:
				System.out.println(
						"Problematic execution with Status Code " + STATUS_CODE + " Value of counter is :" + i);
				break;
			}

		} //
		

		// Synchronous Execution
		/*
		 * for (int i = 0; i < 10; i++) { InvokeRequest request = new
		 * InvokeRequest().withFunctionName(functionName)
		 * .withInvocationType(InvocationType.RequestResponse).withLogType("Tail")
		 * .withPayload("{\n" + "  \"key1\": \"value1\",\n" +
		 * "  \"key2\": \"value2\",\n" + "  \"key3\": \"value3\"\n" + "}"); InvokeResult
		 * response = client.invoke(request); if (200 == response.getStatusCode()) {
		 * System.out.println("Successfully executed " + i); } }
		 *///

	}

}
