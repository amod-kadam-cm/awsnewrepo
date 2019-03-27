/**
 * null
 */
package com.cloudmanthan.aws.workshop.apigw;

import com.amazonaws.annotation.NotThreadSafe;
import com.amazonaws.client.AwsSyncClientParams;
import com.amazonaws.opensdk.protect.client.SdkSyncClientBuilder;
import com.amazonaws.opensdk.internal.config.ApiGatewayClientConfigurationFactory;
import com.amazonaws.util.RuntimeHttpUtils;
import com.amazonaws.Protocol;

import java.net.URI;
import javax.annotation.Generated;

/**
 * Fluent builder for {@link com.cloudmanthan.aws.workshop.apigw.RegisterProxyAPI}.
 * 
 * @see com.cloudmanthan.aws.workshop.apigw.RegisterProxyAPI#builder
 **/
@NotThreadSafe
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public final class RegisterProxyAPIClientBuilder extends SdkSyncClientBuilder<RegisterProxyAPIClientBuilder, RegisterProxyAPI> {

    private static final URI DEFAULT_ENDPOINT = RuntimeHttpUtils.toUri("mzwkea2cmc.execute-api.ap-south-1.amazonaws.com", Protocol.HTTPS);
    private static final String DEFAULT_REGION = "ap-south-1";

    /**
     * Package private constructor - builder should be created via {@link RegisterProxyAPI#builder()}
     */
    RegisterProxyAPIClientBuilder() {
        super(new ApiGatewayClientConfigurationFactory());
    }

    /**
     * Construct a synchronous implementation of RegisterProxyAPI using the current builder configuration.
     *
     * @param params
     *        Current builder configuration represented as a parameter object.
     * @return Fully configured implementation of RegisterProxyAPI.
     */
    @Override
    protected RegisterProxyAPI build(AwsSyncClientParams params) {
        return new RegisterProxyAPIClient(params);
    }

    @Override
    protected URI defaultEndpoint() {
        return DEFAULT_ENDPOINT;
    }

    @Override
    protected String defaultRegion() {
        return DEFAULT_REGION;
    }

}
