package com.ibm.dashboard.rest;

import com.ibm.dashboard.UrlStatus;
import com.ibm.dashboard.store.UrlStatusPersisted;
import com.ibm.dashboard.store.UrlStatusPersistedStore;
import com.ibm.dashboard.store.UrlStatusPersistedStoreFactory;

public class UrlStatusAPI {
	//Our database store
		UrlStatusPersistedStore store = UrlStatusPersistedStoreFactory.getInstance();

		public UrlStatus newUrlStatus(UrlStatus urlStatus) {
			if(store == null) {
				System.out.println("UrlStatusAPI: store is null!!!");
				return urlStatus;
			}
			System.out.println("UrlStatusAPI: the store is not null.");
			
			UrlStatusPersisted urlStatusPersisted = new UrlStatusPersisted();
			urlStatusPersisted.sslOn = urlStatus.sslOn;
			urlStatusPersisted.urlLogTails = urlStatus.urlLogTails;
			urlStatusPersisted.urlResponses = urlStatus.urlResponses;
			urlStatusPersisted.urlTimes = urlStatus.urlTimes;
			urlStatusPersisted.url = urlStatus.url;
			urlStatusPersisted.set_id(urlStatus._id);
			urlStatusPersisted.set_rev(urlStatus._rev);
			urlStatusPersisted.name = urlStatus.name;
			urlStatusPersisted.isGet = urlStatus.isGet;
			urlStatusPersisted.jsonText = urlStatus.jsonText;
			
			//store.persist(urlStatusPersisted);
			urlStatusPersisted = store.update(urlStatusPersisted.get_id(), urlStatusPersisted);
			
			urlStatus._id = urlStatusPersisted.get_id();
			urlStatus._rev = urlStatusPersisted.get_rev(); 
		
			return urlStatus;
		}
}
