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

import wasdev.sample.Visitor;

public class MongoDbVisitorStore implements VisitorStore{
	
	private MongoDatabase db = null;
	private static final String databaseName = "test-java-mongodb";
	private MongoCollection<Document> collection; //"java-test"
	
	public MongoDbVisitorStore(){
		MongoClient client = createClient(); 
		if(client!=null){
			db = client.getDatabase(databaseName);
			if (db==null) {
				System.out.println("database null - creating " + databaseName);
				collection = db.getCollection("java-test");
				//db = client.add db
			}
		}
	}
	
	public MongoDatabase getDB(){
		return db;
	}

	/**
	 * create the mongoDBKey store: 
	 * keytool -importcert -trustcacerts -file ./mongodbcert.crt -keystore ./mongoKeyStore -storepass aftereight
	 * 
	 * The document is being stored at this location: 
	 * locally: /your path to the target: GetStartedJavaMongoDb/target/TestJavaMongo-1.0-SNAPSHOT/WEB-INF/classes/mongoKeyStore
	 * on Bluemix: /home/vcap/app/wlp/usr/servers/defaultServer/apps/myapp.war/WEB-INF/classes/mongoKeyStore
	 * 
	 * 
	 */
	private static MongoClient createClient() {
		
		// TODO: upgrade the code to use the VCAP certificate for MongoDB
		System.setProperty("javax.net.ssl.trustStore", "/home/vcap/app/wlp/usr/servers/defaultServer/apps/myapp.war/WEB-INF/classes/mongoKeyStore");
		//uncomment this for local deployments: 
		//System.setProperty("javax.net.ssl.trustStore", "/Volumes/WD1TB/workspaceJee/GetStartedJavaMongoDb/target/TestJavaMongo-1.0-SNAPSHOT/WEB-INF/classes/mongoKeyStore");
		System.setProperty("javax.net.ssl.trustStorePassword", "aftereight");
		//System.out.println("trustStore location: " + System.getProperty("javax.net.ssl.trustStore"));
		//System.out.println("trustStorePassword: " + System.getProperty("javax.net.ssl.trustStorePassword"));
		String url = "";
		//"mongodb://admin:OYQQXPRULWQEOLPL@bluemix-sandbox-dal-9-portal.8.dblayer.com:28247,bluemix-sandbox-dal-9-portal.6.dblayer.com:28247/admin?ssl=true";
			
		if (System.getenv("VCAP_SERVICES") != null) {
			// When running in Bluemix, the VCAP_SERVICES env var will have the credentials for all bound/connected services
			// Parse the VCAP JSON structure looking for mongodb.
			JsonObject mongoCredentials = VCAPHelper.getCloudCredentials("mongodb");
			if(mongoCredentials == null){
				System.out.println("No MongoDB database service bound to this application");
				return null;
			}
			System.out.println(mongoCredentials);
			url = mongoCredentials.get("uri").getAsString();
			System.out.println("got mongodb credentials from VCAP: " + url);
		} else {
			System.out.println("Running locally. Looking for credentials in mongodb.properties");
			url = VCAPHelper.getLocalProperties("mongo.properties").getProperty("mongo_url");
			if(url == null || url.length()==0){
				System.out.println("To use a database, set the Mongo url in src/main/resources/mongo.properties");
				return null;
			}
		}

		try {
			//System.out.println("Connecting to MongoDb " + url);
			MongoClient client = new MongoClient(new MongoClientURI(url));
			System.out.println("Connected to MongoDb ");
			return client;
		} catch (Exception e) {
			System.out.println("Unable to connect to database");
			//e.printStackTrace();
			return null;
		}
	}
	
	@Override
	/*
	 * docs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(Visitor.class);
	 * @see wasdev.sample.store.VisitorStore#getAll()
	 */
	public Collection<Visitor> getAll(){
        List<Visitor> docs = new ArrayList<Visitor>();
		try {
			MongoIterable<String> listDatabaseNames = createClient().listDatabaseNames();
			System.out.println("listed databeses " + listDatabaseNames);
			Document myDoc;
			Visitor newVisitor = new Visitor();
			MongoCursor<Document> cursor = collection.find().iterator();
			try {
			    while (cursor.hasNext()) {
			    	myDoc = cursor.next();
					System.out.println("READ: current id: " + myDoc.getObjectId("_id").toString());
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
	 * 	//String id = db.save(td).getId();
		//return db.find(Visitor.class, id);

	 * @see wasdev.sample.store.VisitorStore#get(java.lang.String)
	 */
	//TODO: change to find by ObjectId
	public Visitor get(String name) {
		
		collection = db.getCollection("java-test");
		//System.out.println("number of documents in the collection : " + collection.count());
		//System.out.println("READ: previous name: " + name);
		//TODO: find by _id
		Document myDoc = collection.find(com.mongodb.client.model.Filters.eq("name", name)).first();
		//TODO: find faster method to getting object id of an inserted object 
		//System.out.println(myDoc.toJson());
		Visitor newVisitor = new Visitor();
		//System.out.println("READ: current id: " + myDoc.getObjectId("_id").toString());
		newVisitor.set_id(myDoc.getObjectId("_id").toString());
		newVisitor.setName(myDoc.getString("name"));
		//System.out.println("number of documents in the collection : " + collection.count());
		return newVisitor;
	}

	/**
	 * code doing the same for Cloudant
	 * String id = db.save(td).getId();
	 *	return db.find(Visitor.class, id);
	 */
	@Override
	public Visitor persist(Visitor td) {
		collection = db.getCollection("java-test");
		//System.out.println("number of documents in the collection : " + collection.count());
		Document doc = new Document("name", td.getName())
                .append("count", 1);
		collection.insertOne(doc);
		//TODO: find faster method to getting object id of an inserted object 
		Document myDoc = collection.find(com.mongodb.client.model.Filters.eq("name", td.getName())).first();
		//System.out.println(myDoc.toJson());
		//System.out.println(myDoc.getObjectId("_id").toString());
		td.set_id(myDoc.getObjectId("_id").toString());
		//System.out.println("number of documents in the collection : " + collection.count());
		return td;
	}

	/**
	 * cloudant update:
	 * String id = db.save(td).getId();
	 * return db.find(Visitor.class, id);
	 */
	@Override
	public Visitor update(String id, Visitor newVisitor) {
		
		//Visitor visitor = db.find(Visitor.class, id);
		//visitor.setName(newVisitor.getName());
		//db.update(visitor);
		//return db.find(Visitor.class, id);
		
		collection = db.getCollection("java-test");
		//System.out.println("number of documents in the collection : " + collection.count());
		//System.out.println("UPDATE previous _id: " + newVisitor.get_id());
		//TODO: find by _id
		collection.updateOne(com.mongodb.client.model.Filters.eq("name", newVisitor.getName()), new Document("$set", new Document("name", newVisitor.getName()).append("count", 2)));
		
		//TODO: find faster method to getting object id of an inserted object 
		Document myDoc = collection.find(com.mongodb.client.model.Filters.eq("name", newVisitor.getName())).first();
		//System.out.println(myDoc.toJson());
		//System.out.println("UPDATE: current id: " + myDoc.getObjectId("_id").toString());
		newVisitor.set_id(myDoc.getObjectId("_id").toString());
		//System.out.println("number of documents in the collection : " + collection.count());
		return newVisitor;
		
	}

	@Override
	//TODO: use ObjectId
	/**
	 * 	Visitor visitor = db.find(Visitor.class, id);
	 * 	db.remove(id, visitor.get_rev());
	 * @see wasdev.sample.store.VisitorStore#delete(java.lang.String)
	 */
	public void delete(String name) {
		
		collection = db.getCollection("java-test");
		//System.out.println("number of documents in the collection : " + collection.count());
		//System.out.println("DELETE previous _id: " + name);
		//TODO: find by _id
		DeleteResult deleteResult = collection.deleteOne(com.mongodb.client.model.Filters.eq("name", name));
		//System.out.println("number of documents deleted :" + deleteResult.getDeletedCount()); 
		//System.out.println("number of documents in the collection : " + collection.count());
		
	}

	@Override
	/*
	 * //return getAll().size();
	 * @see wasdev.sample.store.VisitorStore#count()
	 */
	public int count() throws Exception {
		collection = db.getCollection("java-test");
		System.out.println("number of documents in the collection : " + collection.count());
		int counter = ((Long) collection.count()).intValue();
		return counter;
	}

}
