package com.cloudmanthan.aws.cloudfront;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.Security;
import java.text.ParseException;

import org.jets3t.service.CloudFrontService;
import org.jets3t.service.CloudFrontServiceException;
import org.jets3t.service.utils.ServiceUtils;

public class CloudFrontSignedURLGenerator {

	public static void main(String args[]) {

		generateSignedURL();

	}

	static void generateSignedURL() {
		
		// Signed URLs for a private distribution
		// Note that Java only supports SSL certificates in DER format,
		// so you will need to convert your PEM-formatted file to DER format.
		// To do this, you can use openssl:
		// openssl pkcs8 -topk8 -nocrypt -in origin.pem -inform PEM -out new.der
		// -outform DER
		// So the encoder works correctly, you should also add the bouncy castle jar
		// to your project and then add the provider.

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		String distributionDomain = "somevalue";
		String privateKeyFilePath = "/Users/amod/aws/keys/new.der";
		String s3ObjectKey = "private.html";
		String policyResourcePath = "http://" + distributionDomain + "/" + s3ObjectKey;

		// Convert your DER file into a byte array.

		FileInputStream is = null;
		try {
			is = new FileInputStream(privateKeyFilePath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		byte[] derPrivateKey = null;
		try {
			derPrivateKey = ServiceUtils.readInputStreamToBytes(is);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Generate a "canned" signed URL to allow access to a
		// specific distribution and object

		String keyPairId = "somevalue" ;//"APKAJYGKP7ITXBGCFKAQ";
		String signedUrlCanned = null;
		try {
			signedUrlCanned = CloudFrontService.signUrlCanned("http://" + distributionDomain + "/" + s3ObjectKey, // Resource
					keyPairId , // Certificate identifier,
								// an active trusted signer for the distribution
					derPrivateKey, // DER Private key data
					ServiceUtils.parseIso8601Date("2018-07-23T22:20:00.000Z") // DateLessThan
			);
		} catch (CloudFrontServiceException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(signedUrlCanned);

		// Build a policy document to define custom restrictions for a signed URL.

		String policy = null;
		try {
			policy = CloudFrontService.buildPolicyForSignedUrl(
					// Resource path (optional, can include '*' and '?' wildcards)
					policyResourcePath,
					// DateLessThan
					ServiceUtils.parseIso8601Date("2018-07-23T22:20:00.000Z"),
					
					// CIDR IP address restriction (optional, 0.0.0.0/0 means everyone)
					//"0.0.0.0/0"
					"115.97.41.137/32",
					// DateGreaterThan (optional)
					ServiceUtils.parseIso8601Date("2018-06-01T06:31:56.000Z"));
		} catch (CloudFrontServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Generate a signed URL using a custom policy document.

		String signedUrl = null;
		try {
			signedUrl = CloudFrontService.signUrl(
					// Resource URL or Path
					"http://" + distributionDomain + "/" + s3ObjectKey,
					// Certificate identifier, an active trusted signer for the distribution
					keyPairId,
					// DER Private key data
					derPrivateKey,
					// Access control policy
					policy);
		} catch (CloudFrontServiceException e) {
			e.printStackTrace();
		}
		System.out.println(signedUrl);
	}
}
