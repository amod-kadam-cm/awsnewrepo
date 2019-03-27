package com.cloudmanthan.aws.workshop.client;

import com.amazonaws.opensdk.SdkRequestConfig;
import com.amazonaws.opensdk.config.ConnectionConfiguration;
import com.amazonaws.opensdk.config.TimeoutConfiguration;
import com.cloudmanthan.aws.workshop.apigw.RegisterProxyAPI;
import com.cloudmanthan.aws.workshop.apigw.RegisterProxyAPIClientBuilder;
import com.cloudmanthan.aws.workshop.apigw.model.PostRegisterRequest;
import com.cloudmanthan.aws.workshop.apigw.model.PostRegisterResult;

public class App {

	public static void main(String[] args) {
		RegisterProxyAPIClientBuilder builder = RegisterProxyAPI.builder();

		RegisterProxyAPI client = RegisterProxyAPI.builder()
				.connectionConfiguration(
						new ConnectionConfiguration().maxConnections(100).connectionMaxIdleMillis(1000))
				.timeoutConfiguration(new TimeoutConfiguration().httpRequestTimeout(3000).totalExecutionTimeout(10000)
						.socketTimeout(2000))
				.build();
		
		
				// Configure PostRequest level settings
		PostRegisterResult result = client.postRegister(new PostRegisterRequest().sdkRequestConfig(SdkRequestConfig
				.builder().httpRequestTimeout(15000).totalExecutionTimeout(5000).customHeader("CustomHeaderName", "foo")
				.customQueryParam("firstname", "bar").customQueryParam("lastname", "body").
				build()));

		System.out.println(result.sdkResponseMetadata().requestId());
		System.out.println(result.sdkResponseMetadata().httpStatusCode());
		// Full access to all HTTP headers (including modeled ones)
		result.sdkResponseMetadata().header("Content-Length").ifPresent(System.out::println);

		client.shutdown();
		// Client is now unusable

	}

}
