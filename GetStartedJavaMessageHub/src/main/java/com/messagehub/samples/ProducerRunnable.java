/**
 * Copyright 2015-2016 IBM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corp. 2015-2016
 */
package com.messagehub.samples;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.errors.TimeoutException;
//import org.apache.log4j.Level;
//import org.apache.log4j.//logger;

import com.messagehub.samples.rest.RESTAdmin;

public class ProducerRunnable implements Runnable {
	// private static final //logger //logger =
	// //logger.get//logger(ProducerRunnable.class);

	private final KafkaProducer<String, String> kafkaProducer;
	private final String topic;
	private volatile boolean closing = false;

	public ProducerRunnable(Properties producerProperties, String topic) {
		this.topic = topic;

		// Create a Kafka producer with the provided client configuration
		kafkaProducer = new KafkaProducer<String, String>(producerProperties);

		try {
			// Checking for topic existence.
			// If the topic does not exist, the kafkaProducer will retry for
			// about 60 secs
			// before throwing a TimeoutException
			// see configuration parameter 'metadata.fetch.timeout.ms'
			List<PartitionInfo> partitions = kafkaProducer.partitionsFor(topic);
			System.out.println(partitions.toString());// logger.log(Level.INFO,
														// partitions.toString());
		} catch (TimeoutException kte) {
			System.out.println("Topic '" + topic + "' may not exist - application will terminate");// logger.log(Level.ERROR,
																									// "Topic
																									// '"
																									// +
																									// topic
																									// +
																									// "'
																									// may
																									// not
																									// exist
																									// -
																									// application
																									// will
																									// terminate");
			kafkaProducer.close();
			throw new IllegalStateException("Topic '" + topic + "' may not exist - application will terminate", kte);
		}
	}

	@Override
	public void run() {
		// Simple counter for messages sent
		int producedMessages = 0;
		System.out.println(ProducerRunnable.class.toString() + " is starting.");// logger.log(Level.INFO,
																				// ProducerRunnable.class.toString()
																				// +
																				// "
																				// is
																				// starting.");

		long responseTime = 0;
		String responseCode = "404";
		String responseDesc = "''";
		String restResponse = "";
		String responseString = "";
		long startTime = 0;
		long endTime = 0;

		try {
			while (!closing) {
				String key = "key";
				String message = "This is a test message #" + producedMessages;

				try {
					// If a partition is not specified, the client will use the
					// default partitioner to choose one.
					ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, key, message);

					// timed operation
					startTime = System.currentTimeMillis();
					System.out.println(startTime);
					// Send record asynchronously
					Future<RecordMetadata> future = kafkaProducer.send(record);
					endTime = System.currentTimeMillis();
					System.out.println(endTime);
					responseTime = (endTime - startTime);
					System.out.println(responseTime);
					responseDesc = "{'Message produced': '" + restResponse + "'}";
					responseString = "{type: 'produce message', response_time: " + responseTime + ", response_code: "
							+ responseCode + ", desc: " + responseDesc + "}";
					System.out.println(responseString);

					// Synchronously wait for a response from Message Hub /
					// Kafka on every message produced.
					// For high throughput the future should be handled
					// asynchronously.
					RecordMetadata recordMetadata = future.get(5000, TimeUnit.MILLISECONDS);
					producedMessages++;

					System.out.println("MessageHubConsoleSample.isTestOver is: " + MessageHubConsoleSample.isTestOver
							+ ", \nMessage produced, offset: " + recordMetadata.offset());
					restResponse = "Message produced, offset: " + recordMetadata.offset();
					responseCode = "200";
					responseDesc = "{'Message created': '" + restResponse + "'}";
					responseString = "{type: 'produce message', response_time: " + responseTime + ", response_code: "
							+ responseCode + ", desc: " + responseDesc + "}";
					System.out.println(responseString);
					MessageHubConsoleSample.messageHubProduceMessage = responseString;
					// Short sleep for flow control in this sample app
					// to make the output easily understandable
					Thread.sleep(2000);

				} catch (final InterruptedException e) {
					if (!"200".contains(responseCode)) {
						responseCode = "500";
						restResponse = "Producer has caught : " + e;
					}
					System.out.println("Producer closing - interrupted exception: " + e);
				} catch (final Exception e) {
					if (!"200".contains(responseCode)) {
						responseCode = "500";
						restResponse = "Producer has caught : " + e;
					}
					System.out.println("Sleeping for 5s - Producer has caught : " + e);
					try {
						Thread.sleep(5000); // Longer sleep before retrying
					} catch (InterruptedException e1) {
						System.out.println("Producer closing - caught exception: " + e);
						if (!"200".contains(responseCode)) {
							responseCode = "500";
							restResponse = "Producer has caught : " + e1;
						}

					}
				}
				// end process
				if (producedMessages > 2) {
					System.out.println("Messages produced: " + producedMessages);
					// TODO: send shutdown to consumer and producer					
					closing = true;
					ConsumerRunnable.closing = true;
				}
			}
		} finally {
			kafkaProducer.close(5000, TimeUnit.MILLISECONDS);
			System.out.println(ProducerRunnable.class.toString() + " has shut down.");
			closing = false;
			ConsumerRunnable.closing = false;
		}
		if (!"200".contains(responseCode) ) {

			responseTime = (endTime - startTime);
			responseDesc = "{'Message created': '" + restResponse + "'}";
			responseString = "{type: 'produce message', response_time: " + responseTime + ", response_code: "
					+ responseCode + ", desc: " + responseDesc + "}";
			MessageHubConsoleSample.messageHubProduceMessage = responseString;
		}
		System.out.println("final ---------- :" + responseString);
		
		//force shutting down threads
		MessageHubConsoleSample.isTestOver = true;
	}

	public void shutdown() {
		closing = true;
		System.out.println(ProducerRunnable.class.toString() + " is shutting down.");// logger.log(Level.INFO,
																						// ProducerRunnable.class.toString()
																						// +
																						// "
																						// is
																						// shutting
																						// down.");
	}
}
