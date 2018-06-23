package com.cloudmanthan.training.aws.s3;

import java.io.File;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

public class UploadObjectMultipartUploadUsingHighLevelAPI {


	    public static void main(String[] args) throws Exception {
	        String existingBucketName = "amod.kadam";
	        String keyName            = "s3/IntelliJIDE-Installable-new";
	        // file size is approximately 365 MB
	        String filePath           = "D:\\Softwares\\ideaIC-2017.2.5.exe";  
	        
	        TransferManager tm = new TransferManager(new ProfileCredentialsProvider());        
	        System.out.println("Hello");
	        // TransferManager processes all transfers asynchronously, 
	        // so this call will return immediately.
	        Upload upload = tm.upload(
	        		existingBucketName, keyName, new File(filePath));
	        System.out.println("Hello2");

	        try {
	        	// Or you can block and wait for the upload to finish
	        	upload.waitForCompletion();
	        	System.out.println("Upload complete.");
	        } catch (AmazonClientException amazonClientException) {
	        	System.out.println("Unable to upload file, upload was aborted.");
	        	amazonClientException.printStackTrace();
	        }
	    }
	}
	

