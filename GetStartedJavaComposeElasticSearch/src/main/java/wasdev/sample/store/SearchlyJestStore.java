/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package wasdev.sample.store;

import java.io.IOException;
import java.util.Arrays;
import org.apache.log4j.BasicConfigurator;

import com.google.gson.JsonObject;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.cluster.Health;

public class SearchlyJestStore {

	private static JestClient client;
	
	public SearchlyJestStore() {

		if (client == null) {
			client = jestClient();
		}

	}

	public JestClient getClient() {
		return client;
	}

	/**
	 * https://help.compose.com/v2.0/docs/elasticsearch-connecting-to-elasticsearch
	 * 
	 * TODO: remove unnecessary create the key store: keytool -importcert
	 * -trustcacerts -file ./searchlycert.crt -keystore ./searchlyKeyStore
	 * -storepass aftereight
	 * 
	 * 
	 * The document is being stored at this location: locally: /your path to the
	 * target:
	 * GetStartedJavaMongoDb/target/TestJavaMongo-1.0-SNAPSHOT/WEB-INF/classes/mongoKeyStore
	 * on Bluemix:
	 * /home/vcap/app/wlp/usr/servers/defaultServer/apps/myapp.war/WEB-INF/classes/mongoKeyStore
	 * 
	 * 
	 */
	private static JestClient jestClient() {

		// TODO: upgrade the code to use the VCAP certificate for MongoDB
		// System.setProperty("javax.net.ssl.trustStore",
		// "/home/vcap/app/wlp/usr/servers/defaultServer/apps/myapp.war/WEB-INF/classes/mongoKeyStore");
		// uncomment this for local deployments:
		// System.setProperty("javax.net.ssl.trustStore",
		// "/Volumes/WD1TB/workspaceJee/GetStartedJavaMongoDb/target/TestJavaMongo-1.0-SNAPSHOT/WEB-INF/classes/mongoKeyStore");
		// System.setProperty("javax.net.ssl.trustStorePassword", "aftereight");
		// System.out.println("trustStore location: " +
		// System.getProperty("javax.net.ssl.trustStore"));
		// System.out.println("trustStorePassword: " +
		// System.getProperty("javax.net.ssl.trustStorePassword"));

		String url = "";
		// connectionUrl = "http://site:your-api-key@your- url.searchly.com";
		// //replace with the Connection URL

		if (System.getenv("VCAP_SERVICES") != null) {
			// When running in Bluemix, the VCAP_SERVICES env var will have the
			// credentials for all bound/connected services
			// Parse the VCAP JSON structure looking for searchly.
			JsonObject searchlyCredentials = VCAPHelper.getCloudCredentials("compose-for-elasticsearch");
			if (searchlyCredentials == null) {
				System.out.println("No searchlyCredentials service bound to this application");
				return null;
			}
			System.out.println(searchlyCredentials);
			url = searchlyCredentials.get("uri").getAsString();
			System.out.println("got searchlyCredentials credentials from VCAP: " + url);
		} else {
			System.out.println("Running locally. Looking for credentials in mongodb.properties");
			url = VCAPHelper.getLocalProperties("searchly.properties").getProperty("searchly_url");
			if (url == null || url.length() == 0) {
				System.out.println(
						"To use a searchly, set the ElasticSearch url in src/main/resources/searchly.properties");
				return null;
			}
		}

		try {

			// shows connection process
			BasicConfigurator.configure();
			System.out.println("Connecting to compose " + url);

			// start of Jest library methods
			JestClientFactory factory = new JestClientFactory();
			factory.setHttpClientConfig(new HttpClientConfig.Builder(Arrays.asList(
					// Compose connection strings
					// "https://username:password@portal113-2.latest-elasticsearch.compose-3.composedb.com:10113",
					// "https://username:password@portal164-1.latest-elasticsearch.compose-3.composedb.com:10113"
					url)).multiThreaded(true).build());
			// Construct a new Jest client according to configuration via
			// factory
			System.out.println("Connected to Searchly ");

			return factory.getObject();
		} catch (Exception e) {
			System.out.println("Unable to connect to compose");
			e.printStackTrace();
			return null;
		}
	}

}
