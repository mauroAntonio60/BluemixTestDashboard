/**
 * 
 */
package com.ibm.dashboard.store;

/**
 * @author mareksadowski
 *
 */
public class SchedulerSettingsStoreFactory {

	private static SchedulerSettingsStore instance;
	static {
		CloudantSchedulerSettingsStore csss = new CloudantSchedulerSettingsStore();	
		if(csss.getDB() != null){
			instance = csss;
		}
	}
	
	public static SchedulerSettingsStore getInstance() {
		return instance;
	}

}
