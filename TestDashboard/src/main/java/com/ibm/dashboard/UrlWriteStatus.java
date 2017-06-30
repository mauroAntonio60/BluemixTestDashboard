/**
 * 
 */
package com.ibm.dashboard;

import com.ibm.dashboard.rest.UrlStatusAPI;

/**
 * @author mareksadowski
 *
 */
public class UrlWriteStatus {
	public UrlStatus urlStatus;

	public UrlStatus writeNewStatus() {
		UrlStatusAPI urlStatusAPI = new UrlStatusAPI();
		UrlStatus newUrlStatus = urlStatusAPI.newUrlStatus(urlStatus);
		urlStatus._id = newUrlStatus._id;
		urlStatus._rev = newUrlStatus._rev;
		urlStatus.urlResponses = newUrlStatus.urlResponses;
		urlStatus.urlTimes = newUrlStatus.urlTimes;
		urlStatus.urlLogTails = newUrlStatus.urlLogTails;
		urlStatus.name = newUrlStatus.name;
		urlStatus.isGet = newUrlStatus.isGet;

		return urlStatus;
	}

}
