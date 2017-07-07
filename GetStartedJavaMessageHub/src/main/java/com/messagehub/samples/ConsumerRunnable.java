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

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.errors.WakeupException;
//import org.apache.log4j.//logger;

public class ConsumerRunnable implements Runnable {

    private final KafkaConsumer<String, String> kafkaConsumer;
    public static volatile boolean closing = false;

    public ConsumerRunnable(Properties consumerProperties, String topic) {
        // Create a Kafka consumer with the provided client configuration
        kafkaConsumer = new KafkaConsumer<String, String>(consumerProperties);

        // Checking for topic existence before subscribing
        List<PartitionInfo> partitions = kafkaConsumer.partitionsFor(topic);
        if (partitions == null || partitions.isEmpty()) {
        	System.out.println("Topic '" + topic + "' does not exists - application will terminate");//logger.log(Level.ERROR, "Topic '" + topic + "' does not exists - application will terminate");
            kafkaConsumer.close();
            throw new IllegalStateException("Topic '" + topic + "' does not exists - application will terminate");
        } else {
        	System.out.println(partitions.toString());//logger.log(Level.INFO, partitions.toString());
        }
        
        kafkaConsumer.subscribe(Arrays.asList(topic));
    }

    @Override
    public void run() {
        System.out.println(ConsumerRunnable.class.toString() + " is starting.");
        long responseTime = 0;
		String responseCode = "404";
		String responseDesc = "''";
		String restResponse = "";
		String responseString = "";
		long startTime = 0;
		long endTime = 0;
		closing=false;
		
        try {
            while (!closing) {
                try {
                	// timed operation
					startTime = System.currentTimeMillis();
					System.out.println(startTime);
					
                    // Poll on the Kafka consumer, waiting up to 3 secs if there's nothing to consume.
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(3000);
                    
                    endTime = System.currentTimeMillis();
					System.out.println(endTime);
					responseTime = (endTime - startTime);
					System.out.println(responseTime);
                    if (records.isEmpty()) {
                    	responseCode = "500";
                    	System.out.println("No messages consumed");
                    	restResponse = "No messages consumed";
                    	
                    } else {
                        // Iterate through all the messages received and print their content
                        for (ConsumerRecord<String, String> record : records) {
                        	responseCode = "200";
                        	System.out.println("Message consumed: " + record.toString());
                        	restResponse = "Message consumed: " + record.toString();
                        }
                    }
                    responseDesc = "{'Message consumed': '" + restResponse + "'}";
                    responseString = "{type: 'consume message', response_time: " + responseTime + ", response_code: "
							+ responseCode + ", desc: " + responseDesc + "}";
					System.out.println(responseString);
					MessageHubConsoleSample.messageHubConsumeMessage = responseString;
                } catch (final WakeupException e) {
                	System.out.println("Consumer closing - caught e: " + e);
                	if (!"200".contains(responseCode)) {
						responseCode = "500";
						restResponse = "Consumer has caught : " + e;
                	}
                } catch (final KafkaException e) {
                	System.out.println("Sleeping for 5s - Consumer has caught: " + e);
                	if (!"200".contains(responseCode)) {
						responseCode = "500";
						restResponse = "Consumer has caught : " + e;
                	}
                	
                    try {
                        Thread.sleep(5000); // Longer sleep before retrying
                    } catch (InterruptedException e1) {
                    	System.out.println("Consumer closing - caught exception: " + e);
                    	if (!"200".contains(responseCode)) {
    						responseCode = "500";
    						restResponse = "Consumer has caught : " + e;
                    	}
                    }
                }
            }
        } finally {
            kafkaConsumer.close();
            System.out.println(ConsumerRunnable.class.toString() + " has shut down.");
        }
        if (!"200".contains(responseCode) ) {

			responseTime = (endTime - startTime);
			responseDesc = "{'Message consumer': '" + restResponse + "'}";
			responseString = "{type: 'consume message', response_time: " + responseTime + ", response_code: "
					+ responseCode + ", desc: " + responseDesc + "}";
			MessageHubConsoleSample.messageHubConsumeMessage = responseString;
		}
        System.out.println("final ---------- :" + responseString);
    }

    public void shutdown() {
        closing = true;
        kafkaConsumer.wakeup();
        System.out.println(ConsumerRunnable.class.toString() + " is shutting down.");//logger.log(Level.INFO, ConsumerRunnable.class.toString() + " is shutting down.");
    }
}
