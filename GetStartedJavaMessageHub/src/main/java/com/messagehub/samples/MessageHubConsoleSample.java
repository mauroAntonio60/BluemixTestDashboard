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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

//import org.apache.log4j.Level;
//import org.apache.log4j.//logger;

import com.messagehub.samples.bluemix.BluemixEnvironment;
import com.messagehub.samples.bluemix.MessageHubCredentials;
import com.messagehub.samples.rest.RESTAdmin;

import wasdev.sample.Visitor;

/**
 * Console-based sample interacting with Message Hub, authenticating with
 * SASL/PLAIN over an SSL connection.
 *
 * @author IBM
 */
@ApplicationPath("test")
@Path("/messagehub")
public class MessageHubConsoleSample extends Application {

	private static final String APP_NAME = "myapp.war";//"test-java-messagehub";
	private static final String DEFAULT_TOPIC_NAME = "test-java-messagehub-topic";
	private static final String ARG_CONSUMER = "-consumer";
	private static final String ARG_PRODUCER_ = "-producer";
	private static final String ARG_TOPIC = "-topic";

	String topicName = DEFAULT_TOPIC_NAME;
	String user;
	String password;
	String bootstrapServers = "kafka03-prod01.messagehub.services.us-south.bluemix.net:9093,kafka01-prod01.messagehub.services.us-south.bluemix.net:9093,kafka05-prod01.messagehub.services.us-south.bluemix.net:9093,kafka02-prod01.messagehub.services.us-south.bluemix.net:9093,kafka04-prod01.messagehub.services.us-south.bluemix.net:9093";// args[0];bootstrapServers
																																																																																					// =
																																																																																					// null;
	String adminRestURL = "https://kafka-admin-prod01.messagehub.services.us-south.bluemix.net:443";;
	String apiKey = "xMySYODBPUBsMoiw1EnvZPo7Qizt1w7lozxXzbASWJSoFgMW";

	public static String messageHubProduceMessage = "{}";
	public static String messageHubConsumeMessage = "{}";
	public static String responseCreateTopic = "{}";
	
	private static Thread consumerThread = null;
	private static ConsumerRunnable consumerRunnable = null;
	private static Thread producerThread = null;
	private static ProducerRunnable producerRunnable = null;
	private static String resourceDir;
	public static boolean isTestOver = false;
	
	public void setMessageHubProduceMessage(String msg){
		this.messageHubProduceMessage = msg;
	}

	private static void printUsage() {
		System.out.println("\n" + "Usage:\n" + "    java -jar build/libs/" + APP_NAME + ".jar \\\n"
				+ "              <kafka_brokers_sasl> <kafka_admin_url> <api_key> [" + ARG_CONSUMER + "] \\\n"
				+ "              [" + ARG_PRODUCER_ + "] [" + ARG_TOPIC + "]\n" + "Where:\n" + "    kafka_broker_sasl\n"
				+ "        Required. Comma separated list of broker endpoints to connect to, for\n"
				+ "        example \"host1:port1,host2:port2\".\n" + "    kafka_admin_url\n"
				+ "        Required. The URL of the Message Hub Kafka administration REST endpoint.\n" + "    api_key\n"
				+ "        Required. A Message Hub API key used to authenticate access to Kafka.\n" + "    "
				+ ARG_CONSUMER + "\n"
				+ "        Optional. Only consume message (do not produce messages to the topic).\n"
				+ "        If omitted this sample will both produce and consume messages.\n" + "    " + ARG_PRODUCER_
				+ "\n" + "        Optional. Only produce messages (do not consume messages from the\n"
				+ "        topic). If omitted this sample will both produce and consume messages.\n" + "    "
				+ ARG_TOPIC + "\n" + "        Optional. Specifies the Kafka topic name to use. If omitted the\n"
				+ "        default used is '" + DEFAULT_TOPIC_NAME + "'\n");
	}

	@POST
	@Path("/all")
	@Produces({ "application/json" })
	@Consumes("application/json")
	public String runTestPost(String string) {
		System.out.println("got post req + " + string);
		return runTest();
	}

	@GET
	@Path("/all")
	@Produces({ "application/json" })
	public String runTest() { // static void main(String args[]) {
		System.out.println("--------------------\nRunning in Message Hub.");

		try {
			final String userDir = System.getProperty("user.dir");
			final boolean isRunningInBluemix = BluemixEnvironment.isRunningInBluemix();
			final Properties clientProperties = new Properties();

			boolean runConsumer = true;
			boolean runProducer = true;

			// Check environment: Bluemix vs Local, to obtain configuration
			// parameters
			if (isRunningInBluemix) {

				//// logger.log(Level.INFO, "Running in Bluemix mode.");
				System.out.println("Running in Bluemix mode.");
				// ~/app/wlp/usr/servers/defaultServer/apps/myapp.war/WEB-INF/classes
				resourceDir = userDir + File.separator + "apps" + File.separator + APP_NAME + File.separator + "WEB-INF" + File.separator
						+ "classes";

				MessageHubCredentials credentials = BluemixEnvironment.getMessageHubCredentials();

				bootstrapServers = stringArrayToCSV(credentials.getKafkaBrokersSasl());
				adminRestURL = credentials.getKafkaRestUrl();
				apiKey = credentials.getApiKey();
				user = credentials.getUser();
				password = credentials.getPassword();

			} else {
				// If running locally
				System.out.println("Running in local mode.");
				resourceDir = userDir + File.separator + "apps" + File.separator + "MessageHubLibertyApp.war"
						+ File.separator + "WEB-INF" + File.separator + "classes";
				user = apiKey.substring(0, 16);
				password = apiKey.substring(16);

				
			}

			// inject bootstrapServers in configuration, for both consumer and
			// producer
			clientProperties.put("bootstrap.servers", bootstrapServers);

			System.out.println("Kafka Endpoints: " + bootstrapServers);
			System.out.println("Admin REST Endpoint: " + adminRestURL);
			
			responseCreateTopic = messageHubCreateTopic();

			// create the Kafka clients
			if (runConsumer) {
				System.out.println("starting consumer");
				Properties consumerProperties = getClientConfiguration(clientProperties, "consumer.properties", user,
						password);
				consumerRunnable = new ConsumerRunnable(consumerProperties, topicName);
				consumerThread = new Thread(consumerRunnable, "Consumer Thread");
				consumerThread.start();
			}

			if (runProducer) {
				System.out.println("starting producer");
				Properties producerProperties = getClientConfiguration(clientProperties, "producer.properties", user,
						password);
				producerRunnable = new ProducerRunnable(producerProperties, topicName);
				producerThread = new Thread(producerRunnable, "Producer Thread");
				producerThread.start();
			}

			System.out.println("MessageHubConsoleSample will run until interrupted.");
		} catch (Exception e) {
			System.out.println("Exception occurred, application will terminate" + e);
			System.exit(-1);
		}

		System.out.println("-------------------------------entering loop!");
		
		long startTime;
		long endTime;
		startTime = System.currentTimeMillis();
		endTime = startTime;
		while(((endTime-startTime)<5000)&&(!isTestOver)){
			endTime = System.currentTimeMillis();
		}
		isTestOver = false;
		System.out.println("-------------------------------Quitting");
		String response = "{service: 'message-hub', operations: [" + responseCreateTopic + ","
				+ messageHubProduceMessage + "," + messageHubConsumeMessage //+ "," + messageHubDeleteTopic() 
				+ "]"
				+ ", response_code: 200, desc:'operations implemented CrPCo/CrPCoD'}";
		shutdown();
		return response;
	}

	private String messageHubCreateTopic() {
		// TODO Auto-generated method stub
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		long startTime;
		long endTime;
		// Using Message Hub Admin REST API to create and list topics
		// If the topic already exists, creation will be a no-op
		System.out.println("Creating the topic " + topicName);
		String restResponse = "";
		// timed operation
		startTime = System.currentTimeMillis();
		try {

			// TODO: get the ObjectID
			// end of timed operation
			restResponse = RESTAdmin.createTopic(adminRestURL, apiKey, topicName);

		} catch (Exception e) {
			System.out.println("setting response code to 500; Error occurred accessing the Admin REST API " + e);
			responseCode = "500";
			restResponse = "Error occurred accessing the Admin REST API " + e;
			// The application will carry on regardless of Admin REST errors, as
			// the topic may already exist
		}
		endTime = System.currentTimeMillis();
		System.out.println("Admin REST response :" + restResponse);
		try {
			String topics = RESTAdmin.listTopics(adminRestURL, apiKey);
			System.out.println("Admin REST Listing Topics: " + topics);
		} catch (Exception e) {
			System.out.println("Error occurred accessing the Admin REST API " + e);
			// The application will carry on regardless of Admin REST errors, as
			// the topic may already exist
		}

		responseTime = (endTime - startTime);
		if (restResponse.length()==0) {
			restResponse = "no response - the topic has been already created";
		}
		responseDesc = "{'Admin REST response': '" + restResponse + "'}";
		String responseString = "{type: 'create topic', response_time: " + responseTime + ", response_code: " + responseCode
				+ ", desc: " + responseDesc + "}";
		System.out.println(responseString);
		return responseString;
	}

	/*
	 * convenience method for cleanup on shutdown
	 */
	private static void shutdown() {
		if (producerRunnable != null)
			producerRunnable.shutdown();
		if (consumerRunnable != null)
			consumerRunnable.shutdown();
		if (producerThread != null)
			producerThread.interrupt();
		if (consumerThread != null)
			consumerThread.interrupt();
	}

	/*
	 * Return a CSV-String from a String array
	 */
	private static String stringArrayToCSV(String[] sArray) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sArray.length; i++) {
			sb.append(sArray[i]);
			if (i < sArray.length - 1)
				sb.append(",");
		}
		return sb.toString();
	}

	/*
	 * Retrieve client configuration information, using a properties file, for
	 * connecting to Message Hub Kafka.
	 */
	static final Properties getClientConfiguration(Properties commonProps, String fileName, String user,
			String password) {
		Properties result = new Properties();
		InputStream propsStream;

		// test file system:
		//resourceDir = "...GetStartedJavaMessageHub/target/TestJavaMessageHub-1.0-SNAPSHOT/WEB-INF/classes";

		System.out.println("reading prop file :" + resourceDir + File.separator + fileName);
		try {
			propsStream = new FileInputStream(resourceDir + File.separator + fileName);
			result.load(propsStream);
			propsStream.close();
		} catch (IOException e) {
			System.out.println("Could not load properties from file");
			return result;
		}

		System.out.println("read prop file");

		result.putAll(commonProps);
		// Adding in credentials for MessageHub auth
		String saslJaasConfig = result.getProperty("sasl.jaas.config");
		saslJaasConfig = saslJaasConfig.replace("USERNAME", user).replace("PASSWORD", password);
		result.setProperty("sasl.jaas.config", saslJaasConfig);
		return result;
	}

}
