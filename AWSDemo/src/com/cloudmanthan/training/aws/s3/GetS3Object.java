package com.cloudmanthan.training.aws.s3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.costandusagereport.model.AWSRegion;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;

public class GetS3Object {
  private static String bucketName = /*"com.cloudmanthan.softwares"; */ "amod.kadam.lambda";
  private static String key = "custom_software.txt";

  public static void main(String[] args) throws IOException
  {
   //sAmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
   /*AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
            .withCredentials(new InstanceProfileCredentialsProvider())
            .build(); */
   
   AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
           .withRegion(Regions.US_EAST_2)
           .build();
   InstanceProfileCredentialsProvider  instanceProfile = InstanceProfileCredentialsProvider.getInstance();
   
   AWSCredentials awsCred = instanceProfile.getCredentials();
   
   System.out.println("AccessKeyId " +awsCred.getAWSAccessKeyId());
   
   System.out.println("SecretAccessKey : " + awsCred.getAWSSecretKey());
   
   System.out.println(instanceProfile.getCredentials().toString());
   
    
    try {
      System.out.println("Downloading an object");
      com.amazonaws.services.s3.model.S3Object s3object = s3Client.getObject(
          new GetObjectRequest(bucketName, key));
      displayTextInputStream(s3object.getObjectContent());
      
      s3Client.createBucket("amod.kadam.udemy.test");
    }
    catch(AmazonServiceException ase) {
      System.err.println("Exception was thrown by the service");
      ase.printStackTrace();
    }
    catch(AmazonClientException ace) {
      System.err.println("Exception was thrown by the client");
      ace.printStackTrace();
    }
  }

  private static void displayTextInputStream(InputStream input) throws IOException
  {
    // Read one text line at a time and display.
    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    while(true)
    {
      String line = reader.readLine();
      if(line == null) break;
      System.out.println( "    " + line );
    }
    System.out.println();
  }
}