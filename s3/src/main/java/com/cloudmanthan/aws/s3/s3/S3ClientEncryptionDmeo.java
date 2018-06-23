package com.cloudmanthan.aws.s3.s3;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3Encryption;
import com.amazonaws.services.s3.AmazonS3EncryptionClientBuilder;
import com.amazonaws.services.s3.model.CryptoConfiguration;
import com.amazonaws.services.s3.model.CryptoMode;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.StaticEncryptionMaterialsProvider;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class S3ClientEncryptionDmeo {
	
	public static String BUCKET_NAME = "amod.kadam";
	
	// It is recommended to read it from configuration file or memory
	public static String ENCRYPTED_KEY = "encryptedfile";

	public static void main(String[] args) {
		
		SecretKey secretKey = null;
		try {
			secretKey = KeyGenerator.getInstance("AES").generateKey();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Get the S3 client for encryption
		AmazonS3Encryption s3Encryption = AmazonS3EncryptionClientBuilder
		        .standard()
		        .withRegion(Regions.US_EAST_1)
		        .withCryptoConfiguration(new CryptoConfiguration(CryptoMode.EncryptionOnly))
		        .withEncryptionMaterials(new StaticEncryptionMaterialsProvider(new EncryptionMaterials(secretKey)))
		        .build();
		
		// upload the object using encryption client
		s3Encryption.putObject(BUCKET_NAME, ENCRYPTED_KEY, "christiano_ronaldo");
		// get object as a string
		System.out.println(s3Encryption.getObjectAsString(BUCKET_NAME, ENCRYPTED_KEY));




	}

}
