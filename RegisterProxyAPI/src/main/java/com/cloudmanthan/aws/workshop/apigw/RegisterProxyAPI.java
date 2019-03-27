/**
 * null
 */
package com.cloudmanthan.aws.workshop.apigw;

import javax.annotation.Generated;

import com.amazonaws.*;
import com.amazonaws.opensdk.*;
import com.amazonaws.opensdk.model.*;
import com.amazonaws.regions.*;

import com.cloudmanthan.aws.workshop.apigw.model.*;

/**
 * Interface for accessing RegisterProxyAPI.
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public interface RegisterProxyAPI {

    /**
     * @param postRegisterRequest
     * @return Result of the PostRegister operation returned by the service.
     * @sample RegisterProxyAPI.PostRegister
     * @see <a href="http://docs.aws.amazon.com/goto/WebAPI/mzwkea2cmc-2019-03-27T13:23:39Z/PostRegister"
     *      target="_top">AWS API Documentation</a>
     */
    PostRegisterResult postRegister(PostRegisterRequest postRegisterRequest);

    /**
     * @return Create new instance of builder with all defaults set.
     */
    public static RegisterProxyAPIClientBuilder builder() {
        return new RegisterProxyAPIClientBuilder();
    }

    /**
     * Shuts down this client object, releasing any resources that might be held open. This is an optional method, and
     * callers are not expected to call it, but can if they want to explicitly release any open resources. Once a client
     * has been shutdown, it should not be used to make any more requests.
     */
    void shutdown();

}
