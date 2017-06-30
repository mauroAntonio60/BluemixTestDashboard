/**
 * 
 */
package com.ibm.dashboard;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author mareksadowski
 *
 */
public class UrlResponseDoc {
	String service;
	JsonArray operations;
	int response_code;
	String desc;
	UrlResponse[] urlResponses;
	
	public UrlResponse[] getUrlResponses() {
		return urlResponses;
	}

	/**
	 * 
	 */
	public UrlResponseDoc() {
		// TODO Auto-generated constructor stub
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public JsonArray getOperations() {
		return operations;
	}

	public void setOperations(JsonArray operations) {
		this.operations = operations;
	}

	public int getResponse_code() {
		return response_code;
	}

	public void setResponse_code(int response_code) {
		this.response_code = response_code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public UrlResponse[] getOperationArray(){
		Gson gson = new Gson();
		
		UrlResponse[] urlResponses = gson.fromJson(operations, UrlResponse[].class);
		//System.out.println("found urlResponses with gson : " + urlResponses.length);
		return urlResponses;
	}
	
	public UrlResponseDoc (String jsonString){
		Gson gson = new Gson();
		JsonObject jsonObject = new JsonParser().parse(jsonString).getAsJsonObject();

		//System.out.println(jsonObject.get("service").getAsString());
		this.service = jsonObject.get("service").getAsString();
		
		//System.out.println(jsonObject.get("operations").getAsJsonArray());
		this.operations = jsonObject.get("operations").getAsJsonArray();
		
		//System.out.println(jsonObject.get("response_code").getAsInt());
		//this.response_code = jsonObject.get("response_code").getAsInt();
		//this.desc = jsonObject.get("desc").getAsString();
		
		this.urlResponses = gson.fromJson(this.operations, UrlResponse[].class);
		//System.out.println("size of the responses: " +urlResponses.length);
		
	}
	
}
