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
 /* mongodb VCAP credentials management patched by mauro antonio giacomello 2019-07-17 */
 *******************************************************************************/
package wasdev.sample.store;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

import wasdev.sample.Visitor;

public class MongoDbVisitorStore implements VisitorStore {

	private MongoDatabase db = null;
	private static final String databaseName = "test-java-mongodb";
	private MongoCollection<Document> collection; // "java-test"
		
	public MongoDbVisitorStore() {
		System.out.println("xyz token 02");
		MongoClient client = createClient();
		if (client != null) {
			System.out.println("xyz token 03");
			db = client.getDatabase(databaseName);
			if (db == null) {
				System.out.println("xyz database null - creating " + databaseName);
				collection = db.getCollection("java-test");
				// db = client.add db
			}
		}
		System.out.println("xyz token 04");
	}

	public MongoDatabase getDB() {
		System.out.println("xyz token 05");
		return db;
	}

	/**
	 * create the mongoDBKey store: keytool -importcert -trustcacerts -file
	 * ./mongodbcert.crt -keystore ./mongoKeyStore -storepass aftereight
	 * 
	 * The document is being stored at this location: locally: /your path to the
	 * target:
	 * GetStartedJavaMongoDb/target/TestJavaMongo-1.0-SNAPSHOT/WEB-INF/classes/mongoKeyStore
	 * on Bluemix:
	 * /home/vcap/app/wlp/usr/servers/defaultServer/apps/myapp.war/WEB-INF/classes/mongoKeyStore
	 * 
	 * 
	 */
	private static MongoClient createClient() {

		// TODO: upgrade the code to use the VCAP certificate for MongoDB
		System.setProperty("javax.net.ssl.trustStore",
				"/home/vcap/app/wlp/usr/servers/defaultServer/apps/myapp.war/WEB-INF/classes/mongoKeyStore");
		// TODO: uncomment the next line for local deployments:
		// System.setProperty("javax.net.ssl.trustStore",
		// "/Users/mareksadowski/Documents/2018-ibm/201805-blogging/BluemixTestDashboard/GetStartedJavaMongoDb/target/TestJavaMongo-1.0-SNAPSHOT/WEB-INF/classes/mongoKeyStore");
		
		System.setProperty("javax.net.ssl.trustStorePassword", "tokenant");
		/**/ System.out.println("xyz trustStore location: " +
		/**/ System.getProperty("javax.net.ssl.trustStore"));
		/**/ System.out.println("xyz trustStorePassword: " +
		/**/ System.getProperty("javax.net.ssl.trustStorePassword"));
		String url = "";

		if (System.getenv("VCAP_SERVICES") != null) {
			
			System.out.println("xyz token 06 VCAP_SERVICES != null");
			
			// When running in Bluemix, the VCAP_SERVICES env var will have the credentials
			// for all bound/connected services
			// Parse the VCAP JSON structure looking for mongodb.
			JsonObject mongoCredentials = VCAPHelper.getCloudCredentials("mongodb");
			if (mongoCredentials == null) {
				System.out.println("xyz No MongoDB database service bound to this application");
				return null;
			}
			System.out.println("xyz token 07 mongo credentials");
			System.out.println(mongoCredentials);
			
			//url = mongoCredentials.get("uri").getAsString();
			
			JsonElement mongoconn = mongoCredentials.get("connection");
			System.out.println("xyz mongoconn: " + mongoconn);
			
			JsonObject  generatedObj1 = mongoconn.getAsJsonObject();
			System.out.println("xyz generatedObj1: " + generatedObj1);
			
			JsonElement mongomongodb = generatedObj1.get("mongodb");
			System.out.println("xyz mongomongodb: " + mongomongodb);
			
			JsonObject  generatedObj2 = mongomongodb.getAsJsonObject();						
			System.out.println("xyz generatedObj2: " + generatedObj2);
			
			url = generatedObj2.get("composed").getAsString();
			System.out.println("xyz url: " + url);
			
			System.out.println("xyz got mongodb credentials from VCAP: " + url);

			// url manipulation - getting a User, the password, a link
			String url1 = url.substring(10);//removing "mongodb://"
			
			// search for : and @ to obtain user and password
			int indexEndUser = url1.indexOf(":");
			int indexEndPassword = url1.indexOf("@");
			int indexEndAddress = url1.indexOf("?");
			// user password url
			String user = url1.substring(0, indexEndUser);
			String password = url1.substring(indexEndUser + 1, indexEndPassword);
			String urlHost = url1.substring(indexEndPassword + 1, indexEndAddress);

			/**/System.out.println(user);
			/**/System.out.println(password);
			System.out.println(urlHost);
			
			System.out.println("xyz token 01");

			// TODO: Add SSL
			/**
			 * TODO: add trust store manipulation + SSL
			 * System.setProperty("javax.net.ssl.trustStore","/home/project/truststore");
			 * System.setProperty("javax.net.ssl.trustStorePassword","somepass"); String
			 * user = "admin"; String password = "mypass"; String URL =
			 * "?verifyServerCertificate=true"+ "&useSSL=true" + "&requireSSL=true";
			 */
			url = "mongodb://" 
					//admin:OYQQXPRULWQEOLPL@
					+ user + ":" + password + "@"
					+ urlHost + "?authSource=admin&ssl=true";

			System.out.println("xyz Connecting database...");
			
		} else {
			System.out.println("xyz Running locally. Looking for credentials in mongodb.properties");
			url = VCAPHelper.getLocalProperties("mongo.properties").getProperty("mongo_url");
			if (url == null || url.length() == 0) {
				System.out.println("xyz To use a database, set the Mongo url in src/main/resources/mongo.properties");
				return null;
			}
		}

		try {
			System.out.println("xyz Connecting to MongoDb - url: ... " + url);
			MongoClient client = new MongoClient(new MongoClientURI(url));
			System.out.println("xyz Connected to MongoDb ");
			return client;
		} catch (Exception e) {
			System.out.println("xyz Unable to connect to database");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	/*
	 * docs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().
	 * getDocsAs(Visitor.class);
	 * 
	 * @see wasdev.sample.store.VisitorStore#getAll()
	 */
	public Collection<Visitor> getAll() {
		List<Visitor> docs = new ArrayList<Visitor>();
		try {
			MongoIterable<String> listDatabaseNames = createClient().listDatabaseNames();
			System.out.println("xyz listed databeses " + listDatabaseNames);
			Document myDoc;
			Visitor newVisitor = new Visitor();
			MongoCursor<Document> cursor = collection.find().iterator();
			try {
				while (cursor.hasNext()) {
					myDoc = cursor.next();
					System.out.println("xyz READ: current id: " + myDoc.getObjectId("_id").toString());
					newVisitor.set_id(myDoc.getObjectId("_id").toString());
					newVisitor.setName(myDoc.getString("name"));
					docs.add(newVisitor);
				}
			} finally {
				cursor.close();
			}
		} catch (Exception e) {
			return null;
		}
		return docs;
	}

	@Override
	/*
	 * //String id = db.save(td).getId(); //return db.find(Visitor.class, id);
	 * 
	 * @see wasdev.sample.store.VisitorStore#get(java.lang.String)
	 */
	// TODO: change to find by ObjectId
	public Visitor get(String name) {

		collection = db.getCollection("java-test");
		/**/ System.out.println("xyz number of documents in the collection : " +
		/**/ collection.count());
		/**/ System.out.println("xyz READ: previous name: " + name);
		// TODO: find by _id
		Document myDoc = collection.find(com.mongodb.client.model.Filters.eq("name", name)).first();
		// TODO: find faster method to getting object id of an inserted object
		/**/ System.out.println(myDoc.toJson());
		Visitor newVisitor = new Visitor();
		/**/ System.out.println("xyz READ: current id: " +
		/**/ myDoc.getObjectId("_id").toString());
		newVisitor.set_id(myDoc.getObjectId("_id").toString());
		newVisitor.setName(myDoc.getString("name"));
		/**/ System.out.println("xyz number of documents in the collection : " +
		/**/ collection.count());
		return newVisitor;
	}

	/**
	 * code doing the same for Cloudant String id = db.save(td).getId(); return
	 * db.find(Visitor.class, id);
	 */
	@Override
	public Visitor persist(Visitor td) {
		collection = db.getCollection("java-test");
		/**/ System.out.println("xyz number of documents in the collection : " +
		/**/ collection.count());
		Document doc = new Document("name", td.getName()).append("count", 1);
		collection.insertOne(doc);
		// TODO: find faster method to getting object id of an inserted object
		Document myDoc = collection.find(com.mongodb.client.model.Filters.eq("name", td.getName())).first();
		/**/ System.out.println(myDoc.toJson());
		/**/ System.out.println(myDoc.getObjectId("_id").toString());
		td.set_id(myDoc.getObjectId("_id").toString());
		/**/ System.out.println("xyz number of documents in the collection : " +
		/**/ collection.count());
		return td;
	}

	/**
	 * cloudant update: String id = db.save(td).getId(); return
	 * db.find(Visitor.class, id);
	 */
	@Override
	public Visitor update(String id, Visitor newVisitor) {

		// Visitor visitor = db.find(Visitor.class, id);
		// visitor.setName(newVisitor.getName());
		// db.update(visitor);
		// return db.find(Visitor.class, id);

		collection = db.getCollection("java-test");
		/**/ System.out.println("xyz number of documents in the collection : " +
		/**/ collection.count());
		/**/ System.out.println("xyz UPDATE previous _id: " + newVisitor.get_id());
		// TODO: find by _id
		collection.updateOne(com.mongodb.client.model.Filters.eq("name", newVisitor.getName()),
				new Document("$set", new Document("name", newVisitor.getName()).append("count", 2)));

		// TODO: find faster method to getting object id of an inserted object
		Document myDoc = collection.find(com.mongodb.client.model.Filters.eq("name", newVisitor.getName())).first();
		// System.out.println(myDoc.toJson());
		/**/ System.out.println("xyz UPDATE: current id: " +
		/**/ myDoc.getObjectId("_id").toString());
		newVisitor.set_id(myDoc.getObjectId("_id").toString());
		/**/ System.out.println("xyz number of documents in the collection : " +
		/**/ collection.count());
		return newVisitor;

	}

	@Override
	// TODO: use ObjectId
	/**
	 * Visitor visitor = db.find(Visitor.class, id); db.remove(id,
	 * visitor.get_rev());
	 * 
	 * @see wasdev.sample.store.VisitorStore#delete(java.lang.String)
	 */
	public void delete(String name) {

		collection = db.getCollection("java-test");
		/**/ System.out.println("xyz number of documents in the collection : " +
		/**/ collection.count());
		/**/ System.out.println("xyz DELETE previous _id: " + name);
		// TODO: find by _id
		DeleteResult deleteResult = collection.deleteOne(com.mongodb.client.model.Filters.eq("name", name));
		/**/ System.out.println("xyz number of documents deleted :" +
		/**/ deleteResult.getDeletedCount());
		/**/ System.out.println("xyz number of documents in the collection : " +
		/**/ collection.count());

	}

	@Override
	/*
	 * //return getAll().size();
	 * 
	 * @see wasdev.sample.store.VisitorStore#count()
	 */
	public int count() throws Exception {
		collection = db.getCollection("java-test");
		System.out.println("xyz number of documents in the collection : " + collection.count());
		int counter = ((Long) collection.count()).intValue();
		return counter;
	}

}
