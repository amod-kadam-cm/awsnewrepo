package com.cloudmanthan.aws.awsworkshop.s3;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3EncryptionClient;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.EncryptedPutObjectRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.SSECustomerKey;

public class ServerSideEncryptionUsingSSE {
    private static AmazonS3 S3_CLIENT;
   
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        String clientRegion = "ap-south-1";
        String bucketName = "cmworkshop";
        String keyName = "ciphertext-c-sse-key";
        String uploadFileName = "/Users/amod/aws/s3/plaintext.txt";
        String targetKeyName = "rumiquote-sse";
        
      
        try {
            S3_CLIENT = AmazonS3ClientBuilder.standard()
                    .withCredentials(new ProfileCredentialsProvider())
                    .withRegion(clientRegion)
                    .build();

            // Upload an object.
            uploadObject(bucketName, keyName, new File(uploadFileName));
    
            // Download the object.
            downloadObject(bucketName, keyName);
    
            // Verify that the object is properly encrypted by attempting to retrieve it
         
            retrieveObjectMetadata(bucketName, keyName);
    
            // Copy the object into a new object that also uses SSE-C.
            copyObject(bucketName, keyName, targetKeyName);
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }

    private static void uploadObject(String bucketName, String keyName, File file) {
       
    	ObjectMetadata metadata = new ObjectMetadata();
        metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
		PutObjectRequest putRequest = new PutObjectRequest(bucketName, keyName, file).withMetadata(metadata);
        
        S3_CLIENT.putObject(putRequest);
        System.out.println("Object uploaded");
    }

    private static void downloadObject(String bucketName, String keyName) throws IOException {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, keyName);
        S3Object object = S3_CLIENT.getObject(getObjectRequest);

        System.out.println("Object content: ");
        displayTextInputStream(object.getObjectContent());
    }

    private static void retrieveObjectMetadata(String bucketName, String keyName) {
        GetObjectMetadataRequest getMetadataRequest = new GetObjectMetadataRequest(bucketName, keyName);
                                                             
        ObjectMetadata objectMetadata = S3_CLIENT.getObjectMetadata(getMetadataRequest);
        System.out.println("Metadata retrieved. Object size: " + objectMetadata.getContentLength());
    }
    
    private static void copyObject(String bucketName, String keyName, String targetKeyName)
            throws NoSuchAlgorithmException {
    

        CopyObjectRequest copyRequest = new CopyObjectRequest(bucketName, keyName, bucketName, targetKeyName);
        
        S3_CLIENT.copyObject(copyRequest);
        System.out.println("Object copied");
    }

    private static void displayTextInputStream(S3ObjectInputStream input) throws IOException {
        // Read one line at a time from the input stream and display each line.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println();
    }
}

