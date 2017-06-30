package com.ibm.dashboard.store;

import java.util.Collection;

import com.cloudant.client.api.Database;



public interface UrlStatusPersistedStore {

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
	 * @return All UrlStatusPersisted objects.
  	 * @throws Exception 
	 */
  public Collection<UrlStatusPersisted> getAll();

  /**
   * Gets an individual UrlStatusPersisted from the store.
   * @param id The ID of the UrlStatusPersisted to get.
   * @return The UrlStatusPersisted.
   */
  public UrlStatusPersisted get(String id);

  /**
   * Persists an UrlStatusPersisted to the store.
   * @param urlStatusPersisted The UrlStatusPersisted to persist.
   * @return The persisted UrlStatusPersisted.  The UrlStatusPersisted will not have a unique ID..
   */
  public UrlStatusPersisted persist(UrlStatusPersisted urlStatusPersisted);

  /**
   * Updates an UrlStatusPersisted in the store.
   * @param id The ID of the UrlStatusPersisted to update.
   * @param urlStatusPersisted The UrlStatusPersisted with updated information.
   * @return The updated UrlStatusPersisted.
   */
  public UrlStatusPersisted update(String id, UrlStatusPersisted urlStatusPersisted);

  /**
   * Deletes an UrlStatusPersisted from the store.
   * @param id The ID of the UrlStatusPersisted to delete.
   */
  public void delete(String id);
  
  /**
   * Counts the number of UrlStatusPersisted
   * @return The total number of UrlStatusPersisted objects.
 * @throws Exception 
   */
  public int count() throws Exception;

}
