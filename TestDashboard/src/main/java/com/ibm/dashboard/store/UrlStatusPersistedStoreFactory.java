package com.ibm.dashboard.store;

public class UrlStatusPersistedStoreFactory {

		
		private static UrlStatusPersistedStore instance;
		static {
			CloudantUrlStatusPersistedStore curlf = new CloudantUrlStatusPersistedStore();	
			if(curlf.getDB() != null){
				instance = curlf;
			}
		}
		
		public static UrlStatusPersistedStore getInstance() {
			return instance;
		}

}
