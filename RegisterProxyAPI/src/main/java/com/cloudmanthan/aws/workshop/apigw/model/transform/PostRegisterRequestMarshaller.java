/**
 * null
 */
package com.cloudmanthan.aws.workshop.apigw.model.transform;

import javax.annotation.Generated;

import com.amazonaws.SdkClientException;
import com.cloudmanthan.aws.workshop.apigw.model.*;

import com.amazonaws.protocol.*;
import com.amazonaws.annotation.SdkInternalApi;

/**
 * PostRegisterRequestMarshaller
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
@SdkInternalApi
public class PostRegisterRequestMarshaller {

    private static final PostRegisterRequestMarshaller instance = new PostRegisterRequestMarshaller();

    public static PostRegisterRequestMarshaller getInstance() {
        return instance;
    }

    /**
     * Marshall the given parameter object.
     */
    public void marshall(PostRegisterRequest postRegisterRequest, ProtocolMarshaller protocolMarshaller) {

        if (postRegisterRequest == null) {
            throw new SdkClientException("Invalid argument passed to marshall(...)");
        }

        try {
        } catch (Exception e) {
            throw new SdkClientException("Unable to marshall request to JSON: " + e.getMessage(), e);
        }
    }

}
