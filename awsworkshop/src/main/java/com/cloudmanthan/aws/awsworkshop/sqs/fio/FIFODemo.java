package com.cloudmanthan.aws.awsworkshop.sqs.fio;

/*
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  https://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FIFODemo {

	static String myQueueUrl = "https://sqs.ap-south-1.amazonaws.com/123456789012/demo-cmworkshop.fifo";
	static AmazonSQS sqs = null;

	public static void main(String[] args) {
		/*
		 * Create a new instance of the builder with all defaults (credentials and
		 * region) set automatically. For more information, see Creating Service Clients
		 * in the AWS SDK for Java Developer Guide.
		 */

		intiSQSClient();

		SendMessages();

		// ReceiveMessages from Queue
		// ReceiveMessages(false);

		// ReceiveMessages from Queue with DELETE_MESSAGE_FLAG = true
		// ReceiveMessages(true);

		/* demo for group-id */
		//SendMessagewithMessageGroup();

	}

	private static void ReceiveMessages(boolean deleteFlag) {

		// Receive messages.
		System.out.println("Receiving messages from MyFifoQueue.fifo.\n");
		final ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl)
				.withMaxNumberOfMessages(10);

		// Uncomment the following to provide the ReceiveRequestDeduplicationId
		// receiveMessageRequest.setReceiveRequestAttemptId("1");
		final List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
		for (final Message message : messages) {
			System.out.println("Message");
			System.out.println("  MessageId:     " + message.getMessageId());
			System.out.println("  ReceiptHandle: " + message.getReceiptHandle());
			System.out.println("  MD5OfBody:     " + message.getMD5OfBody());
			System.out.println("  Body:          " + message.getBody());
			for (final Entry<String, String> entry : message.getAttributes().entrySet()) {
				System.out.println("Attribute");
				System.out.println("  Name:  " + entry.getKey());
				System.out.println("  Value: " + entry.getValue());
			}
		}
		if (true == deleteFlag) {
			// Delete the message.
			System.out.println("Deleting the message.\n");
			final String messageReceiptHandle = messages.get(0).getReceiptHandle();
			sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageReceiptHandle));
		}

	}

	private static void intiSQSClient() {

		sqs = AmazonSQSClientBuilder.defaultClient();
	}

	private static void SendMessages() {
		try {

			// Send a message.
			System.out.println("Sending a message to MyFifoQueue.fifo.\n");
			final SendMessageRequest sendMessageRequest = new SendMessageRequest(myQueueUrl,
					"This is my message text-duplicate-again-3");

			/*
			 * When you send messages to a FIFO queue, you must provide a non-empty
			 * MessageGroupId.
			 */

			sendMessageRequest.setMessageGroupId("message-group-id-1");

			// Uncomment the following to provide the MessageDeduplicationId
			sendMessageRequest.setMessageDeduplicationId("1");
			final SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);
			final String sequenceNumber = sendMessageResult.getSequenceNumber();
			final String messageId = sendMessageResult.getMessageId();

			System.out.println(
					"SendMessage succeed with messageId " + messageId + ", sequence number " + sequenceNumber + "\n");

			System.out.println();

		} catch (final AmazonServiceException ase) {
			System.out.println(
					"Caught an AmazonServiceException, which means " + "your request made it to Amazon SQS, but was "
							+ "rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (final AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means "
					+ "the client encountered a serious internal problem while "
					+ "trying to communicate with Amazon SQS, such as not " + "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}

	}

	private static void SendMessagewithMessageGroup() {
		try {

			/*
			 * When you send messages to a FIFO queue, you must provide a non-empty
			 * MessageGroupId.
			 */
			final SendMessageRequest sendMessageRequest = new SendMessageRequest().withQueueUrl(myQueueUrl);

			String messageBody = "Message ";
			String messageGroup = null;

			for (int i = 0; i < 22; i++) {
				messageGroup = "";
				messageBody = "Message ";
				if (i % 2 == 0) {
					messageGroup = "even-message-group";

				} else {
					messageGroup = "odd-message-group";

				}
				
				

				sendMessageRequest.setMessageGroupId(messageGroup);
				messageBody = messageBody + i + " " + messageGroup;

				sendMessageRequest.setMessageBody(messageBody);

				// Send a message.
				System.out.println("Sending a message to MyFifoQueue.fifo.\n");

				// Uncomment the following to provide the MessageDeduplicationId
				String msgDedupId = "dedup_id" + i;
				sendMessageRequest.setMessageDeduplicationId(msgDedupId);
				final SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);
				final String sequenceNumber = sendMessageResult.getSequenceNumber();
				final String messageId = sendMessageResult.getMessageId();

				System.out.println("SendMessage succeed with messageId " + messageId + ", sequence number "
						+ sequenceNumber + "\n");

				System.out.println();
			}

		} catch (final AmazonServiceException ase) {
			System.out.println(
					"Caught an AmazonServiceException, which means " + "your request made it to Amazon SQS, but was "
							+ "rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (final AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means "
					+ "the client encountered a serious internal problem while "
					+ "trying to communicate with Amazon SQS, such as not " + "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}

	}
}