package com.ibm.dashboard.store;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;

public class CloudantUrlStatusPersistedStore implements UrlStatusPersistedStore {
	private Database db = null;
	private static final String databaseName = "test-dashboard";
	
	public CloudantUrlStatusPersistedStore() {
		CloudantClient cloudant = createClient();
		if(cloudant!=null){
		 db = cloudant.database(databaseName, true);
		}
	}

	public Database getDB() {
		return db;
	}

	public void setDB(Database db) {
		this.db = db;
	}

	public static String getDatabasename() {
		return databaseName;
	}


	private CloudantClient createClient() {
		String url;

		if (System.getenv("VCAP_SERVICES") != null) {
			// When running in Bluemix, the VCAP_SERVICES env var will have the credentials for all bound/connected services
			// Parse the VCAP JSON structure looking for cloudant.
			JsonObject cloudantCredentials = VCAPHelper.getCloudCredentials("cloudant");
			if(cloudantCredentials == null){
				System.out.println("CloudantUrlStatusPersistedStore: No cloudant database service bound to this application");
				return null;
			}
			url = cloudantCredentials.get("url").getAsString();
		} else {
			System.out.println("CloudantUrlStatusPersistedStore: Running locally. Looking for credentials in cloudant.properties");
			url = VCAPHelper.getLocalProperties("cloudant.properties").getProperty("cloudant_url");
			if(url == null || url.length()==0){
				System.out.println("To use a database, set the Cloudant url in src/main/resources/cloudant.properties");
				return null;
			}
		}

		try {
			System.out.println("CloudantUrlStatusPersistedStore: Connecting to Cloudant");
			CloudantClient client = ClientBuilder.url(new URL(url)).build();
			return client;
		} catch (Exception e) {
			System.out.println("Unable to connect to database");
			//e.printStackTrace();
			return null;
		}
	}

	@Override
	public Collection<UrlStatusPersisted> getAll() {
		List<UrlStatusPersisted> docs;
		try {
			docs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(UrlStatusPersisted.class);
		} catch (IOException e) {
			return null;
		}
        return docs;
	}

	@Override
	public UrlStatusPersisted get(String id) {
		return db.find(UrlStatusPersisted.class, id);
	}

	@Override
	public UrlStatusPersisted persist(UrlStatusPersisted urlStatusPersisted) {
		String id = db.save(urlStatusPersisted).getId();
		return db.find(UrlStatusPersisted.class, id);
	}

	@Override
	public UrlStatusPersisted update(String id, UrlStatusPersisted urlStatusPersisted) {
		UrlStatusPersisted oldUrlStatusPersisted = db.find(UrlStatusPersisted.class, id);
		oldUrlStatusPersisted.sslOn = urlStatusPersisted.sslOn;
		oldUrlStatusPersisted.isGet = urlStatusPersisted.isGet;
		oldUrlStatusPersisted.urlLogTails = urlStatusPersisted.urlLogTails;
		oldUrlStatusPersisted.urlResponses = urlStatusPersisted.urlResponses;
		oldUrlStatusPersisted.urlTimes = urlStatusPersisted.urlTimes;
		oldUrlStatusPersisted.url = urlStatusPersisted.url;
		oldUrlStatusPersisted.name = urlStatusPersisted.name;
		db.update(oldUrlStatusPersisted);
		return db.find(UrlStatusPersisted.class, id);
	}

	@Override
	public void delete(String id) {
		UrlStatusPersisted urlStatusPersisted = db.find(UrlStatusPersisted.class, id);
		db.remove(id, urlStatusPersisted.get_rev());		
	}

	@Override
	public int count() throws Exception {
		return getAll().size();
	}

}
