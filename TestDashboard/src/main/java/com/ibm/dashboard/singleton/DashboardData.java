/**
 * 
 */
package com.ibm.dashboard.singleton;

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ibm.dashboard.UrlStatus;
import com.ibm.dashboard.store.SchedulerSettings;
import com.ibm.dashboard.store.SchedulerSettingsStore;
import com.ibm.dashboard.store.SchedulerSettingsStoreFactory;
import com.ibm.dashboard.store.UrlStatusPersisted;
import com.ibm.dashboard.store.UrlStatusPersistedStore;
import com.ibm.dashboard.store.UrlStatusPersistedStoreFactory;

/**
 * thread safe singleton
 * 
 * @author mareksadowski
 *
 */
public final class DashboardData {

	// creating the instance at the initialization - by default it is thread
	// safe!
	private static final DashboardData instance = new DashboardData();

	private int refreshRate = 90; // refreshing every 90 sec
	private Collection<UrlStatusPersisted> urlsPersisted;
	public int noThreads = 10; //rev1.0 Scheduler should use thread pools to run the urls.  Otherwise we might run into thread issues if we try to run all the URLs at once (especially as the URLs grow in number)
	
	public String nextRun = "";
	
	protected DashboardData() {

		// TODO CR 0.4 get refresh schedule from DB
		SchedulerSettingsStore schedulerStore = SchedulerSettingsStoreFactory.getInstance();

		if (schedulerStore == null) {
			System.out.println("no store defined for scheduler!");
		} else {
			SchedulerSettings schedulerSettings;
			Collection<SchedulerSettings> col = schedulerStore.getAll();
			if (col != null && col.size() > 0) {
				System.out.println("Found the scheduler in the db!");
				// get the first one
				schedulerSettings = (SchedulerSettings) (col.toArray())[0];
				if (schedulerSettings != null && schedulerSettings.getRefreshTime() > 0) {
					this.refreshRate = schedulerSettings.getRefreshTime() * 60;
					System.out.println("Found the scheduler refresh rate in the db: " + refreshRate);
					System.out.println("Launching the scheduler from the db settings");
					//refreshData();
				}
			}
		}

	}

	class TestDashboardThread implements Runnable {
		UrlStatus doc;
	
		public TestDashboardThread(UrlStatus doc) {
			// TODO Auto-generated constructor stub
			super();
			this.doc = doc;
		}
		
		public void run(){ 
			System.out.println("++++++++++start thread for : " + doc.getUrl());
			try {
				// check is it GET or POST
				if (doc.isGet) {
					doc.checkStatusGet();
				} else {
					// it is POST - configuring json post message
					// loading statuses
					doc.checkStatusPostJson(doc.getJsonText());
				}
			} catch(Exception e){
				System.out.println(e);
			} finally {
				System.out.println("end");
			}  
			System.out.println("-----------ended thread for : " + doc.getUrl());  
		  }  
		 }  
	
	/**
	 * Reseting the data after a change
	 */
	public void resetUrls() {
		urlsPersisted = null;
		System.out.println("urls are reset to null");
		//rev1.0 - not refreshing URLs
		//refreshData();
		System.out.println("since rev1.0 - awaiting till the next call to refresh URLs");

	}

	public Collection<UrlStatusPersisted> getUrls() {
		if (urlsPersisted == null) {
			System.out.println("urls are null");
			//refreshData();
			
			UrlStatusPersistedStore store = UrlStatusPersistedStoreFactory.getInstance();
			if (store == null) {
				System.out.println("no store defined!");
			} else {
				urlsPersisted = store.getAll();
			}
			System.out.println("just read urls");
		}
		return urlsPersisted;
	}

	public void refreshData() {
		UrlStatusPersistedStore store = UrlStatusPersistedStoreFactory.getInstance();

		if (store == null) {
			System.out.println("no store defined!");
		} else {
			urlsPersisted = store.getAll();
			System.out.println("refreshing urls read from DB :" + urlsPersisted.size());

			// updating
			//for (UrlStatusPersisted doc : store.getAll()) {
			ExecutorService executor = Executors.newFixedThreadPool(noThreads);//creating a pool of noThreads threads
			int currentThread = 0;
			for (UrlStatusPersisted doc : urlsPersisted) {

				if (doc.getUrl() != null) {

					// iterate calls
					UrlStatus oldIteratedUrlStatus = new UrlStatus(doc.getUrl(), doc.getSslOn());
					oldIteratedUrlStatus.sslOn = doc.sslOn;
					oldIteratedUrlStatus.urlLogTails = doc.urlLogTails;
					oldIteratedUrlStatus.urlResponses = doc.urlResponses;
					oldIteratedUrlStatus.urlTimes = doc.urlTimes;
					oldIteratedUrlStatus.url = doc.url;
					oldIteratedUrlStatus.name = doc.name;
					oldIteratedUrlStatus.isGet = doc.isGet;
					oldIteratedUrlStatus.setJsonText(doc.jsonText);
					oldIteratedUrlStatus.set_id(doc.get_id());
					oldIteratedUrlStatus.set_rev(doc.get_rev());

					//rev1.0 - validate URLs in the thread pool
					//step 1 - load threads with the docs
					Runnable worker = new TestDashboardThread(oldIteratedUrlStatus);
					
					//step 2 - run them  
			        executor.execute(worker);//calling execute method of ExecutorService  
			         					
					/**
					 * 	moved to the thread 
					try {
						// check is it GET or POST
						if (oldIteratedUrlStatus.isGet) {
							oldIteratedUrlStatus.checkStatusGet();
						} else {
							// it is POST - configuring json post message
							//String json = "{\"name\":\"write test\"}";
							// loading statuses
							oldIteratedUrlStatus.checkStatusPostJson(oldIteratedUrlStatus.getJsonText());
						}
					} finally {
						System.out.println("end");
					}
					*/
				}
			}
			executor.shutdown();  	
			while (!executor.isTerminated()) {   }  
  
			System.out.println("Finished all threads");  
			//refreshing after update
			urlsPersisted = null;
			getUrls();
			
		}
	}

	// get/set refresh rate
	public void setRefreshRate(int refresh) {
		System.out.println("Dashboard: set refresh rate to " + refresh);
		this.refreshRate = refresh;

	}

	public int getRefreshRate() {
		return refreshRate;
	}

	public void setUrls(Collection<UrlStatusPersisted> urlCollection) {
		urlsPersisted = urlCollection;
	}

	// Runtime initialization
	// By defualt ThreadSafe
	public static DashboardData getInstance() {
		return instance;
	}

}
