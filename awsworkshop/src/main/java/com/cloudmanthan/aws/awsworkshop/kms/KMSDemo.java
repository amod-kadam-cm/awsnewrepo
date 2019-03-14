package com.cloudmanthan.aws.awsworkshop.kms;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyRequest;
import com.amazonaws.services.kms.model.GenerateDataKeyResult;

public class KMSDemo {

	static Logger KMSLogger = Logger.getLogger("KMSdemo");

	static AWSKMS kmsClient = AWSKMSClientBuilder.defaultClient();

	public static void main(String[] args) {

		UsingCMKDemo();

		UsingDataKeyDemo();

	}

	private static void UsingDataKeyDemo() {
		String keyId = "arn:aws:kms:ap-south-1:123456789012:key/8208d5b8-4000-45cc-8587-a64d640be2f3";

		GenerateDataKeyRequest dataKeyRequest = new GenerateDataKeyRequest();
		dataKeyRequest.setKeyId(keyId);
		dataKeyRequest.setKeySpec("AES_256");

		GenerateDataKeyResult dataKeyResult = kmsClient.generateDataKey(dataKeyRequest);
		ByteBuffer plaintextKey = dataKeyResult.getPlaintext();

		ByteBuffer encryptedKey = dataKeyResult.getCiphertextBlob();
		KMSLogger.info(" Using DataKey - plaintext key  " + plaintextKey.array().toString());

		KMSLogger.info(" using Data Key  -  Encrypted Key " + encryptedKey.array().toString());

		String dataEncryptionKeyId = new String(encryptedKey.array());

		String plainText = "amod.kadam";
		byte[] plainTextArray = plainText.getBytes();

		ByteBuffer plainTextBuffer = ByteBuffer.wrap(plainTextArray);

		KMSLogger.warning("KeyId used during DataKey is " + dataKeyResult.getKeyId());

		String plaintextKeyString = plaintextKey.array().toString();
		EncryptRequest encryptReq = new EncryptRequest().withKeyId(dataKeyResult.getKeyId())
				.withPlaintext(plainTextBuffer);

		ByteBuffer ciphertextBlob = kmsClient.encrypt(encryptReq).getCiphertextBlob();

		KMSLogger.info("Data Key : ciphertextBlob : " + ciphertextBlob.array().toString());

		// Decryption logic

		DecryptRequest decreq = new DecryptRequest().withCiphertextBlob(ciphertextBlob);

		String keyId2 = kmsClient.decrypt(decreq).getKeyId();

		ByteBuffer decryptResultBuffer = kmsClient.decrypt(decreq).getPlaintext();

		byte[] decryptedByteArray = decryptResultBuffer.array();

		KMSLogger.info("Finally Managed to decrypt it " + new String(decryptedByteArray));

	}

	private static void UsingCMKDemo() {
		String plainText = "amod.kadam";

		byte[] plainTextArray = plainText.getBytes();

		ByteBuffer plainTextBuffer = ByteBuffer.wrap(plainTextArray);

		// Point to actual keyId from your account
		String keyId = "arn:aws:kms:ap-south-1:123456789012:key/8208d5b8-4000-45cc-8587-a64d640be2f3";

		// Encryption
		EncryptRequest req2 = new EncryptRequest().withKeyId(keyId).withPlaintext(plainTextBuffer);

		ByteBuffer ciphertextBlob = kmsClient.encrypt(req2).getCiphertextBlob();

		KMSLogger.info("CMK : ciphertextBlob : " + ciphertextBlob.array().toString());

		// Decryption
		DecryptRequest decreq = new DecryptRequest().withCiphertextBlob(ciphertextBlob);

		String keyId2 = kmsClient.decrypt(decreq).getKeyId();

		ByteBuffer decryptResultBuffer = kmsClient.decrypt(decreq).getPlaintext();

		byte[] decryptedByteArray = decryptResultBuffer.array();

		KMSLogger.info("Finally Managed to decrypt it " + new String(decryptedByteArray));

	}

}
