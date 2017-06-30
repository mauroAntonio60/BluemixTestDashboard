/**
 * 
 */
package com.ibm.dashboard.store;

/**
 * @author mareksadowski
 *
 */
public class UrlStatusPersisted {
	private String _id;
	private String _rev;
	public String name;
	public String url = null;
	public Boolean sslOn = false;
	public Boolean isGet = true; //false POST
	public String jsonText;
	public String[] urlResponses;
	public String[] urlTimes;
	public String[] urlLogTails;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public Boolean getIsGet() {
		return isGet;
	}
	public void setIsGet(Boolean isGet) {
		this.isGet = isGet;
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
	
	


}
