package com.cloudmanthan.aws.awsworkshop.sqs.fio;

import java.util.List;
import java.util.Map.Entry;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

public class ReceiveConsumer1 {

	static String myQueueUrl = "https://sqs.ap-south-1.amazonaws.com/12345678/demo-cmworkshop.fifo";
	static AmazonSQS sqs = null;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		intiSQSClient();
		ReceiveMessages(false);

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

}
