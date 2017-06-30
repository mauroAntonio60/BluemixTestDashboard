/**
 * 
 */
package com.ibm.dashboard.store;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;

/**
 * @author mareksadowski
 *
 */
public class CloudantSchedulerSettingsStore implements SchedulerSettingsStore {
	
	private Database db = null;
	private static final String databaseName = "test-dashboard-settings";


	/**
	 * 
	 */
	public CloudantSchedulerSettingsStore() {
		CloudantClient cloudant = createClient();
		if(cloudant!=null){
		 db = cloudant.database(databaseName, true);
		}
	}
	
	public void setDB(Database db) {
		this.db = db;
	}

	public static String getDatabasename() {
		return databaseName;
	}

/**
 * Redundant method with CloudnatUrlStatusPersistedStore - 
 * TODO: create new class outside
 * @return
 */
	private CloudantClient createClient() {
		String url;

		if (System.getenv("VCAP_SERVICES") != null) {
			// When running in Bluemix, the VCAP_SERVICES env var will have the credentials for all bound/connected services
			// Parse the VCAP JSON structure looking for cloudant.
			JsonObject cloudantCredentials = VCAPHelper.getCloudCredentials("cloudant");
			if(cloudantCredentials == null){
				System.out.println("CloudantSchedulerSettingsStore: No cloudant database service bound to this application");
				return null;
			}
			url = cloudantCredentials.get("url").getAsString();
		} else {
			System.out.println("CloudantSchedulerSettingsStore: Running locally. Looking for credentials in cloudant.properties");
			url = VCAPHelper.getLocalProperties("cloudant.properties").getProperty("cloudant_url");
			if(url == null || url.length()==0){
				System.out.println("To use a database, set the Cloudant url in src/main/resources/cloudant.properties");
				return null;
			}
		}

		try {
			System.out.println("Connecting to Cloudant");
			CloudantClient client = ClientBuilder.url(new URL(url)).build();
			return client;
		} catch (Exception e) {
			System.out.println("Unable to connect to database");
			//e.printStackTrace();
			return null;
		}
		
	}

	/* (non-Javadoc)
	 * @see com.ibm.dashboard.store.SchedulerSettingsStore#getDB()
	 */
	@Override
	public Database getDB() {
		// TODO Auto-generated method stub
		return db;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dashboard.store.SchedulerSettingsStore#getAll()
	 */
	@Override
	public Collection<SchedulerSettings> getAll() {
		List<SchedulerSettings> docs;
		try {
			docs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(SchedulerSettings.class);
		} catch (IOException e) {
			return null;
		}
        return docs;
	}

	/* (non-Javadoc)
	 * @see com.ibm.dashboard.store.SchedulerSettingsStore#get(java.lang.String)
	 */
	@Override
	public SchedulerSettings get(String id) {
		return db.find(SchedulerSettings.class, id);
	}

	/* (non-Javadoc)
	 * @see com.ibm.dashboard.store.SchedulerSettingsStore#persist(com.ibm.dashboard.store.SchedulerSettings)
	 */
	@Override
	public SchedulerSettings persist(SchedulerSettings schedulerSettings) {
		String id = db.save(schedulerSettings).getId();
		return db.find(SchedulerSettings.class, id);
	}

	/* (non-Javadoc)
	 * @see com.ibm.dashboard.store.SchedulerSettingsStore#update(java.lang.String, com.ibm.dashboard.store.SchedulerSettings)
	 */
	@Override
	public SchedulerSettings update(String id, SchedulerSettings schedulerSettings) {
		SchedulerSettings newSchedulerSettings = db.find(SchedulerSettings.class, id);
		
		newSchedulerSettings.refreshTime = schedulerSettings.refreshTime;
		newSchedulerSettings.tresholdToYellow = schedulerSettings.tresholdToYellow;
		newSchedulerSettings.pruneAfter = schedulerSettings.pruneAfter;
		newSchedulerSettings.urlAddress = schedulerSettings.urlAddress;
		newSchedulerSettings.urlName = schedulerSettings.urlName;
		newSchedulerSettings.urlFirstId = schedulerSettings.urlFirstId;
		newSchedulerSettings.isGet = schedulerSettings.isGet;
		newSchedulerSettings.postJson = schedulerSettings.postJson;
		
		db.update(newSchedulerSettings);
		return db.find(SchedulerSettings.class, id);
	}

	/* (non-Javadoc)
	 * @see com.ibm.dashboard.store.SchedulerSettingsStore#delete(java.lang.String)
	 */
	@Override
	public void delete(String id) {
		SchedulerSettings schedulerSettings = db.find(SchedulerSettings.class, id);
		db.remove(id, schedulerSettings.get_rev());

	}

	/* (non-Javadoc)
	 * @see com.ibm.dashboard.store.SchedulerSettingsStore#count()
	 */
	@Override
	public int count() throws Exception {
		return getAll().size();
	}

}
