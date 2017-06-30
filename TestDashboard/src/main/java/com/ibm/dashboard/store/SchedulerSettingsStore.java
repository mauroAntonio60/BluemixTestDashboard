/**
 * 
 */
package com.ibm.dashboard.store;

import java.util.Collection;

import com.cloudant.client.api.Database;

/**
 * @author mareksadowski
 *
 */
public interface SchedulerSettingsStore {
	
	/**
	 * Get the target db object.
	 * 
	 * @return Database.
  	 * @throws Exception 
	 */
  public Database getDB();
  
  	/**
	 * Gets all Visitors from the store.
	 * 
	 * @return All SchedulerSettings objects.
  	 * @throws Exception 
	 */
  public Collection<SchedulerSettings> getAll();

  /**
   * Gets an individual SchedulerSettings from the store.
   * @param id The ID of the SchedulerSettings to get.
   * @return The SchedulerSettings.
   */
  public SchedulerSettings get(String id);

  /**
   * Persists an SchedulerSettings to the store.
   * @param SchedulerSettings The SchedulerSettings to persist.
   * @return The persisted SchedulerSettings.  The SchedulerSettings will not have a unique ID..
   */
  public SchedulerSettings persist(SchedulerSettings schedulerSettings);

  /**
   * Updates an SchedulerSettings in the store.
   * @param id The ID of the SchedulerSettings to update.
   * @param SchedulerSettings The SchedulerSettings with updated information.
   * @return The updated SchedulerSettings.
   */
  public SchedulerSettings update(String id, SchedulerSettings schedulerSettings);

  /**
   * Deletes an SchedulerSettings from the store.
   * @param id The ID of the SchedulerSettings to delete.
   */
  public void delete(String id);
  
  /**
   * Counts the number of SchedulerSettings
   * @return The total number of SchedulerSettings objects.
 * @throws Exception 
   */
  public int count() throws Exception;

}
