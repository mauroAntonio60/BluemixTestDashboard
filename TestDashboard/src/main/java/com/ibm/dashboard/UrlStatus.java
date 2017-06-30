/**
 * 
 */
package com.ibm.dashboard;

import java.io.IOException;
import java.lang.reflect.Array;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.dashboard.rest.UrlStatusAPI;
import com.ibm.dashboard.store.SchedulerSettings;
import com.ibm.dashboard.store.UrlStatusPersisted;

/**
 * @author mareksadowski
 *
 */
public class UrlStatus {
	public String _id;
	public String _rev;
	public String name;
	public String url;
	public Boolean sslOn = false; // true SSL is on
	public Boolean isGet = true; // false POST
	public String jsonText;
	public String[] urlResponses;
	public String[] urlTimes;
	public String[] urlLogTails;
	
	private SchedulerSettings scheduler = new SchedulerSettings();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Boolean getSslOn() {
		return sslOn;
	}

	public void setSslOn(Boolean sslOn) {
		this.sslOn = sslOn;
	}

	public String[] getUrlResponses() {
		return urlResponses;
	}

	public void setUrlResponses(String[] urlResponses) {
		this.urlResponses = urlResponses;
	}

	public String[] getUrlTimes() {
		return urlTimes;
	}

	public void setUrlTimes(String[] urlTimes) {
		this.urlTimes = urlTimes;
	}

	public String[] getUrlLogTails() {
		return urlLogTails;
	}

	public void setUrlLogTails(String[] urlLogTails) {
		this.urlLogTails = urlLogTails;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String get_rev() {
		return _rev;
	}

	public void set_rev(String _rev) {
		this._rev = _rev;
	}

	
	
	/**
	 * @return the isGet
	 */
	public Boolean getIsGet() {
		return isGet;
	}

	/**
	 * @param isGet the isGet to set
	 */
	public void setIsGet(Boolean isGet) {
		this.isGet = isGet;
	}

	/**
	 * @return the jsonText
	 */
	public String getJsonText() {
		return jsonText;
	}

	/**
	 * @param jsonText the jsonText to set
	 */
	public void setJsonText(String jsonText) {
		this.jsonText = jsonText;
	}

	public UrlStatus(String newUrl, Boolean isSsl) {
		url = newUrl;
		sslOn = isSsl;

		urlResponses = new String[1];
		urlTimes = new String[1];
		urlLogTails = new String[1];
		name = "url-name:" + url;
	}

	public void checkStatusPostJson(String json)
	// throws KeyManagementException, NoSuchAlgorithmException,
	// ClientProtocolException, IOException
	{

		CloseableHttpClient httpClient;
		// TODO: CR rev 0.4 - request to show PST date/time 
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS z");
        df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        String dateInPstZone = df.format(new java.util.Date());
        
		try {

		if (sslOn) {
			// getting sslContext
			DashboardConnectionContext dashboardContext = new DashboardConnectionContext();
			SSLContext sslContext = dashboardContext.getContext();

			// SSL based httpClient
			DashboardHttpClient dashboardHttp = new DashboardHttpClient(sslContext);
			httpClient = dashboardHttp.getHttpClient();
		} else {
			// regular http call - No SSL!
			httpClient = HttpClients.createDefault();
		}

		try {

			StringEntity stringEntity = new StringEntity(json, ContentType.create("application/json", Consts.UTF_8));
			stringEntity.setChunked(true);

			HttpPost httppost = new HttpPost(url);
			System.out.println("Executing request " + httppost.getRequestLine());

			httppost.setEntity(stringEntity);

			// TODO add the form based POST
			// HttpPost httpPost = new
			// HttpPost("http://localhost:9080/  some site   ");
			// List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			// nvps.add(new BasicNameValuePair("name", "vip"));
			// nvps.add(new BasicNameValuePair("password", "secret"));
			// httpPost.setEntity(new UrlEncodedFormEntity(nvps));

			CloseableHttpResponse response = httpClient.execute(httppost);

	        try {
				HttpEntity entity = response.getEntity();
				System.out.println("----------------------------------------");
				System.out.println(response.getStatusLine());
				// insert response to array

				// add element to beginning of array
				urlResponses = addNewElementToArray(urlResponses, response.getStatusLine().toString());
				urlTimes = addNewElementToArray(urlTimes, dateInPstZone);

				if ("HTTP/1.1 200 OK".contentEquals(response.getStatusLine().toString())) {
					//System.out.println("ok");

					// store the information from the response
					String body = "";
					if (entity != null) {
						body = EntityUtils.toString(entity);
						//rev 0.5 removing all except for 0..127 chars
						//fix for messageHub and node responses 
						//System.out.println("white spaced string: " + body);
						//removing all white spaces
						body = removeWhiteSpaces(body);
						System.out.println("no white spaced string: " + body);
						//System.out.println(body);
					}
					urlLogTails = addNewElementToArray(urlLogTails, body); // adding
																			// body
																			// instead
																			// of
																			// ""
					// urlLogTails = addNewElementToArray(urlLogTails, "");
				} else {
					System.out.println("failed");
					// checking the log from CF
					// TODO add call to CF
					urlLogTails = addNewElementToArray(urlLogTails, " no log tail :(");
				}

				EntityUtils.consume(entity);

			} finally {
				response.close();
			}
		} catch (Exception e) {
			System.out.println("Error! Unable to connect to " + url + e.toString());
			urlResponses = addNewElementToArray(urlResponses, "Error! Unable to connect to " + url + e.toString());
			urlTimes = addNewElementToArray(urlTimes, dateInPstZone);
			urlLogTails = addNewElementToArray(urlLogTails, e.toString());

			// e.printStackTrace();
		} finally {
			httpClient.close();
		}
		// TODO read & append statuses from DB

		// TODO write the latest status to DB
		UrlWriteStatus writeUrlStatus = new UrlWriteStatus();
		writeUrlStatus.urlStatus = this;
		UrlStatus newUrlStatus = writeUrlStatus.writeNewStatus();
		
		// TODO check this assignments
		this.sslOn = newUrlStatus.sslOn;
		this.urlLogTails = newUrlStatus.urlLogTails;
		this.urlResponses = newUrlStatus.urlResponses;
		this.urlTimes = newUrlStatus.urlTimes;
		this.url = newUrlStatus.url;
		this.set_id(newUrlStatus._id);
		this.set_rev(newUrlStatus._rev);
		
		} catch (Exception e) {
			// TODO KeyManagementException, NoSuchAlgorithmException,
			// ClientProtocolException, IOException
			System.out.println("Error! Unable to connect to " + url + e.toString());
			urlResponses = addNewElementToArray(urlResponses, "Error! Unable to connect to " + url + e.toString());
			urlTimes = addNewElementToArray(urlTimes, dateInPstZone);
			urlLogTails = addNewElementToArray(urlLogTails, e.toString());
			
			// TODO write the latest status to DB
			UrlWriteStatus writeUrlStatus = new UrlWriteStatus();
			writeUrlStatus.urlStatus = this;
			UrlStatus newUrlStatus = writeUrlStatus.writeNewStatus();
			
			this.sslOn = newUrlStatus.sslOn;
			this.urlLogTails = newUrlStatus.urlLogTails;
			this.urlResponses = newUrlStatus.urlResponses;
			this.urlTimes = newUrlStatus.urlTimes;
			this.url = newUrlStatus.url;
			this.set_id(newUrlStatus._id);
			this.set_rev(newUrlStatus._rev);

		}

	}

	public void checkStatusGet() {// throws IOException, KeyManagementException,
									// NoSuchAlgorithmException {

		CloseableHttpClient httpClient;
		// TODO: CR rev 0.4 - request to show PST date/time 
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS z");
		df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
		String dateInPstZone = df.format(new java.util.Date());
		        

		try {

			if (sslOn) {
				// getting sslContext
				DashboardConnectionContext dashboardContext = new DashboardConnectionContext();
				SSLContext sslContext = dashboardContext.getContext();

				// SSL based httpClient
				DashboardHttpClient dashboardHttp = new DashboardHttpClient(sslContext);
				httpClient = dashboardHttp.getHttpClient();
			} else {
				// regular http call - No SSL!
				httpClient = HttpClients.createDefault();
			}

			try {
				HttpGet httpget = new HttpGet(url);
				System.out.println("Executing request " + httpget.getRequestLine());
				CloseableHttpResponse response = null;
				response = httpClient.execute(httpget);

				try {
					HttpEntity entity = response.getEntity();
					System.out.println("----------------------------------------");
					System.out.println(response.getStatusLine());
					// insert response to array

					// add element to beginning of array
					urlResponses = addNewElementToArray(urlResponses, response.getStatusLine().toString());
					urlTimes = addNewElementToArray(urlTimes, dateInPstZone);

					if ("HTTP/1.1 200 OK".contentEquals(response.getStatusLine().toString())) {
						//System.out.println("ok");

						// store the information from the response
						String body = "";
						if (entity != null) {
							body = EntityUtils.toString(entity);
							//rev 0.5 removing _"_ - inserting _'_ instead
							//fix for messageHub and node responses 
							//System.out.println("white spaced string: " + body);
							body = removeWhiteSpaces(body);
							//rev 0.5 stabilizing scheduler
							//System.out.println("no white spaced string: " + body);
						}
						//System.out.println(body);
						urlLogTails = addNewElementToArray(urlLogTails, body); // adding
																				// body
																				// instead
																				// of
																				// ""

					} else {
						System.out.println("failed");
						// checking the log from CF
						// TODO add call to CF
						urlLogTails = addNewElementToArray(urlLogTails, " no log tail :(");
					}

					EntityUtils.consume(entity);

				} finally {
					response.close();
				}
			} catch (Exception e) {
				System.out.println("Error! Unable to connect to " + url + e.toString());
				urlResponses = addNewElementToArray(urlResponses, "Error! Unable to connect to " + url + e.toString());
				urlTimes = addNewElementToArray(urlTimes, dateInPstZone);
				urlLogTails = addNewElementToArray(urlLogTails, e.toString());

				// e.printStackTrace();
			} finally {
				httpClient.close();
			}

			// TODO read & append statuses from DB - is it done earlier?

			// TODO write the latest status to DB
			UrlWriteStatus storeUpdatedUrlStatus = new UrlWriteStatus();
			storeUpdatedUrlStatus.urlStatus = this;
			UrlStatus updatedUrlStatus = storeUpdatedUrlStatus.writeNewStatus();
			this.sslOn = updatedUrlStatus.sslOn;
			this.urlLogTails = updatedUrlStatus.urlLogTails;
			this.urlResponses = updatedUrlStatus.urlResponses;
			this.urlTimes = updatedUrlStatus.urlTimes;
			this.url = updatedUrlStatus.url;
			this.set_id(updatedUrlStatus._id);
			this.set_rev(updatedUrlStatus._rev);
		} catch (Exception e) {
			// TODO based on the exception update status to DB
			System.out.println("Error! Unable to connect to " + url + e.toString());
			urlResponses = addNewElementToArray(urlResponses, "Error! Unable to connect to " + url + e.toString());
			
			//
			//DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS z");
			//df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
			//String dateInPstZone = df.format(new java.util.Date());
			
			
			urlTimes = addNewElementToArray(urlTimes, dateInPstZone);//new java.util.Date().toString());
			urlLogTails = addNewElementToArray(urlLogTails, e.toString());
			
			UrlWriteStatus storeUpdatedUrlStatus = new UrlWriteStatus();
			storeUpdatedUrlStatus.urlStatus = this;
			UrlStatus updatedUrlStatus = storeUpdatedUrlStatus.writeNewStatus();
			this.sslOn = updatedUrlStatus.sslOn;
			this.urlLogTails = updatedUrlStatus.urlLogTails;
			this.urlResponses = updatedUrlStatus.urlResponses;
			this.urlTimes = updatedUrlStatus.urlTimes;
			this.url = updatedUrlStatus.url;
			this.set_id(updatedUrlStatus._id);
			this.set_rev(updatedUrlStatus._rev);
		}
		
		// TODO: update the latest status in the dashboard

	}

	/**
	 * adding new element to the existing array
	 * 
	 * @todo - rework it to be more efficient
	 * @param stringArray
	 * @param newString
	 * @return expanded array by 1, ie with added the first element from a
	 *         parameter string
	 */
	private String[] addNewElementToArray(String[] stringArray, String newString) {

		//TODO: trim null (first occurrence)
		
		
		List<String> stringList = new ArrayList<String>();
		stringList.add(newString);
		
		//TODO: prune more than default length - (200) calls: scheduler.getPruneAfter()
		//stringList.addAll(Arrays.asList(stringArray));
		List<String> subList = Arrays.asList(stringArray);
		if(stringArray != null && stringArray.length > scheduler.getPruneAfter()){
			System.out.println("length of the list to be prunned: "
					+ stringArray.length
					+ "prunning to (from SchedulerSettings): "
					+ scheduler.getPruneAfter());
			stringList.addAll(subList.subList(0, scheduler.getPruneAfter()));
		} else {
			stringList.addAll(Arrays.asList(stringArray));
		}
		
		String[] returnArray = new String[stringList.size()];
		stringList.toArray(returnArray);
		System.out.println("size of converted list:" + stringList.size());

		return returnArray;
	}
	
	private String removeWhiteSpaces(String originalString){
		//removing all white spaces
		String newString= "";
		System.out.print("clearing string: ");
		originalString = originalString.replaceAll("\n","");
		
		for (char c: originalString.toCharArray()){
			  if (((int)c)<=127){
				  newString = newString + c;
			  } else {
				  System.out.print("%");
			  }
		}
		return newString;
	}

}
