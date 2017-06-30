/**
 * 
 */
package com.ibm.dashboard;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * @author mareksadowski
 *
 */
public class UrlResponse {
	String type; //"create"
	int response_time; //220 (ms)
	int response_code; //200 or 404
	/** rev 0.5
	 * initial code: desc: "visitor id: ff8f3ae37b6848b4a0baf009cbb98f59, record ver: 1-3c20addee4755e1aa987764b7db3d8a6"
	 * replaced with: 
	 * "desc": {
     *   "result": "Kafka Client initialized successfully...",
     *   "criteria": {
     *     "producer": "TestAppNode-efa99ac3-cbc4-436e-af89-f56d53ed9919",
     *     "topics": [
     *       {
     *         "name": "test-topic-node"
     *       }
     *     ]
     *   }
     * }
	 */
	JsonObject desc; 
	
	
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public int getResponse_time() {
		return response_time;
	}


	public void setResponse_time(int responseTime) {
		this.response_time = responseTime;
	}


	public int getResponse_code() {
		return response_code;
	}


	public void setResponse_code(int responseCode) {
		this.response_code = responseCode;
	}


	public JsonObject getDesc() {
		return desc;
	}

	public String getDescString() {
		return desc.toString();
	}
	
	public void setDesc(JsonObject desc) {
		this.desc = desc;
	}


	
	
	/**
	 * 
	 */
	public UrlResponse() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 */
	public UrlResponse(String jsonResponse) {
		// TODO using GJSON to get the fields populated
		Gson gson = new Gson();
		UrlResponse o = gson.fromJson(jsonResponse, UrlResponse.class);
		
		this.type = o.getType();
		this.response_time = o.getResponse_time();
		this.response_code = o.getResponse_code();
		this.desc = o.getDesc();
	}
	

}
