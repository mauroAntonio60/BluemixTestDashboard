/**
 * 
 */
package com.ibm.dashboard.store;

/**
 * @author mareksadowski
 *
 */
public class SchedulerSettings {
	private String _id;  //cloudant
	private String _rev; //cloudant
	int refreshTime = 5; //5min
	int tresholdToYellow = 1000; //ms
	int pruneAfter = 200;  //clean records after
	int displayFields = 40;
	String urlAddress = "";
	String urlName = "";
	String urlFirstId = "";
	String order = "";
	boolean isGet = false;
	String postJson = "";

	/**
	 * 
	 */
	public SchedulerSettings() {
		// TODO Auto-generated constructor stub
	}

	
	
	/**
	 * @return the _id
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * @param _id the _id to set
	 */
	public void set_id(String _id) {
		this._id = _id;
	}

	
	/**
	 * @return the _rev
	 */
	public String get_rev() {
		return _rev;
	}

	/**
	 * @param _rev the _rev to set
	 */
	public void set_rev(String _rev) {
		this._rev = _rev;
	}

	/**
	 * @return the refreshTime
	 */
	public int getRefreshTime() {
		return refreshTime;
	}

	/**
	 * @param refreshTime the refreshTime to set
	 */
	public void setRefreshTime(int refreshTime) {
		this.refreshTime = refreshTime;
	}

	/**
	 * @return the tresholdToYellow
	 */
	public int getTresholdToYellow() {
		return tresholdToYellow;
	}

	/**
	 * @param tresholdToYellow the tresholdToYellow to set
	 */
	public void setTresholdToYellow(int tresholdToYellow) {
		this.tresholdToYellow = tresholdToYellow;
	}



	/**
	 * @return the pruneAfter
	 */
	public int getPruneAfter() {
		return pruneAfter;
	}



	/**
	 * @param pruneAfter the pruneAfter to set
	 */
	public void setPruneAfter(int pruneAfter) {
		this.pruneAfter = pruneAfter;
	}



	/**
	 * @return the urlAddress
	 */
	public String getUrlAddress() {
		return urlAddress;
	}



	/**
	 * @param urlAddress the urlAddress to set
	 */
	public void setUrlAddress(String urlAddress) {
		this.urlAddress = urlAddress;
	}



	/**
	 * @return the urlName
	 */
	public String getUrlName() {
		return urlName;
	}



	/**
	 * @param urlName the urlName to set
	 */
	public void setUrlName(String urlName) {
		this.urlName = urlName;
	}



	/**
	 * @return the urlFirstId
	 */
	public String getUrlFirstId() {
		return urlFirstId;
	}



	/**
	 * @param urlFirstId the urlFirstId to set
	 */
	public void setUrlFirstId(String urlFirstId) {
		this.urlFirstId = urlFirstId;
	}



	/**
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}



	/**
	 * @param order the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
	}



	/**
	 * @return the isGet
	 */
	public boolean isGet() {
		return isGet;
	}



	/**
	 * @param isGet the isGet to set
	 */
	public void setGet(boolean isGet) {
		this.isGet = isGet;
	}



	/**
	 * @return the postJson
	 */
	public String getPostJson() {
		return postJson;
	}



	/**
	 * @param postJson the postJson to set
	 */
	public void setPostJson(String postJson) {
		this.postJson = postJson;
	}



	/**
	 * @return the displayFields
	 */
	public int getDisplayFields() {
		return displayFields;
	}



	/**
	 * @param displayFields the displayFields to set
	 */
	public void setDisplayFields(int displayFields) {
		this.displayFields = displayFields;
	}

	
	
}
