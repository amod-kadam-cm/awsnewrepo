/**
 * null
 */
package com.cloudmanthan.aws.workshop.apigw.model.transform;

import java.math.*;

import javax.annotation.Generated;

import com.cloudmanthan.aws.workshop.apigw.model.*;
import com.amazonaws.transform.SimpleTypeJsonUnmarshallers.*;
import com.amazonaws.transform.*;

import com.fasterxml.jackson.core.JsonToken;
import static com.fasterxml.jackson.core.JsonToken.*;

/**
 * PostRegisterResult JSON Unmarshaller
 */
@Generated("com.amazonaws:aws-java-sdk-code-generator")
public class PostRegisterResultJsonUnmarshaller implements Unmarshaller<PostRegisterResult, JsonUnmarshallerContext> {

    public PostRegisterResult unmarshall(JsonUnmarshallerContext context) throws Exception {
        PostRegisterResult postRegisterResult = new PostRegisterResult();

        int originalDepth = context.getCurrentDepth();
        String currentParentElement = context.getCurrentParentElement();
        int targetDepth = originalDepth + 1;

        JsonToken token = context.getCurrentToken();
        if (token == null)
            token = context.nextToken();
        if (token == VALUE_NULL) {
            return postRegisterResult;
        }

        while (true) {
            if (token == null)
                break;

            postRegisterResult.setEmpty(EmptyJsonUnmarshaller.getInstance().unmarshall(context));
            token = context.nextToken();
        }

        return postRegisterResult;
    }

    private static PostRegisterResultJsonUnmarshaller instance;

    public static PostRegisterResultJsonUnmarshaller getInstance() {
        if (instance == null)
            instance = new PostRegisterResultJsonUnmarshaller();
        return instance;
    }
}
