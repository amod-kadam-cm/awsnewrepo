package com.cloudmanthan.aws.s3.s3;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Encryption;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.CryptoConfiguration;
import com.amazonaws.services.s3.model.CryptoMode;
import com.amazonaws.services.s3.model.KMSEncryptionMaterialsProvider;

public class S3ClientKMSEncryptionDemo {
	
	public static String BUCKET_NAME = "amod.kadam";
	
	// It is recommended to read it from configuration file or memory
	public static String ENCRYPTED_KEY = "kmsencryptedfile";

	public static void main(String[] args) {
		
		AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder
		        .standard()
		        .withRegion(Regions.US_EAST_1)
		        .withCryptoConfiguration(new CryptoConfiguration(CryptoMode.EncryptionOnly))
		        // Can either be Key ID or alias (prefixed with 'alias/')
		        .withEncryptionMaterials(new KMSEncryptionMaterialsProvider("alias/amodcmk"))
		        .build();

	
		
		// upload the object using encryption client
		s3Encryption.putObject(BUCKET_NAME, ENCRYPTED_KEY, "lionel_messi");
		// get object as a string
		System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, ENCRYPTED_KEY));




	}

}
