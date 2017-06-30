<%@page import="com.ibm.dashboard.store.UrlStatusPersisted"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="
    java.util.ArrayList,
    java.util.Collection,
	java.util.List,
	java.io.File,
	javax.net.ssl.SSLContext,
	javax.servlet.http.HttpSession,
	
	org.apache.http.ssl.SSLContextBuilder,
	org.apache.http.conn.ssl.NoopHostnameVerifier,
	org.apache.http.conn.ssl.SSLConnectionSocketFactory,
	org.apache.http.conn.ssl.TrustSelfSignedStrategy,
	org.apache.http.ssl.SSLContexts,
	java.security.KeyStore,
	java.io.FileInputStream,
	
    org.apache.http.HttpEntity,
    org.apache.http.NameValuePair,
    org.apache.http.client.entity.UrlEncodedFormEntity,
    org.apache.http.client.methods.CloseableHttpResponse,
    org.apache.http.client.methods.HttpGet,
    org.apache.http.client.methods.HttpPost,
    org.apache.http.impl.client.CloseableHttpClient,
    org.apache.http.impl.client.HttpClients,
    org.apache.http.message.BasicNameValuePair,
    org.apache.http.util.EntityUtils,
    org.apache.http.entity.StringEntity,
    org.apache.http.entity.ContentType,
    org.apache.http.Consts,
    
    com.ibm.dashboard.*,
    com.ibm.dashboard.singleton.*,
    com.ibm.dashboard.store.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Manage Dashboard rev 1.0</title>
<link rel="stylesheet" href="dashboard.css">
</head>
<body>
<table>
<tr>
<td height=28></td><td></td><td></td>
</tr>
<tr>
<td height=50 width=68 class="menu"></td><td width=269 class="logo"></td>
<td class="text">test dashboard - the url management</td>
</tr>
</table>

<a href="index.jsp">To display <b>the url dashboard</b> click here</a><br>


	<%
	
	// TODO: rev1.0 print a message: Schedule changes will take effect at XX:XX:XXX Pacific time
	// Retrieve the current session.
	// Do not create new session if not exists but return null
	HttpSession httpSession = request.getSession(false);
	if (httpSession != null){
	
		//reading the message
		String message2Print = (String) session.getAttribute("message");
		System.out.println("got the session with the message object: " + message2Print);
		if (message2Print!= null && message2Print !=""){
			%><b><%= message2Print %></b><BR>
			<%
			//remove the object from the session
			session.removeAttribute("message");
		}
	}
					
	String myCells = ""
			+ "<tr align=center style='font-weight:bold'><td>delete</td><td>url edit</td><td>get</td><td>post</td><td>json txt</td><td>modify</td></tr>";

	UrlStatusPersistedStore store = UrlStatusPersistedStoreFactory.getInstance();
	SchedulerSettingsStore schedulerStore = SchedulerSettingsStoreFactory.getInstance();
	
	if (store == null || schedulerStore == null) {
		System.out.println("no store defined!");
		%>
	<br> NO STORE DEFINED - DEFINE LOCAL STORE IN
	WEB-INF/CLASSES/cloudant-properties
	<br> OR BIND THE CLOUDANT DB TO THE SERVICE IF IT IS DEPLOYED ON
	BLUEMIX:
	<br> cf create-service "cloudantNoSQLDB Dedicated" "Shared
	Dedicated" NAME_OF_THE_CLOUDANT_SERVICE
	<br> cf bind-service NAME_OF_THIS_APPLICATION
	NAME_OF_THE_CLOUDANT_SERVICE
	<BR> you might need to restage the app:
	<br> cf restage NAME_OF_THIS_APPLICATION
	<br>
	<br>
	<%
	} else {
		List<UrlStatusPersisted> urlStatusPersisteds = new ArrayList<UrlStatusPersisted>();
		List<SchedulerSettings> schedulers = new ArrayList<SchedulerSettings>();
		%>
<br><b>Set refresh time.</b>
<form action="index.jsp" method="get">
Refresh time in minutes: <br>
<i>(the updated refresh time will be setup within 60 minutes)</i><br>
<%
int refreshRate = 0;

// TODO CR 0.4 get refresh schedule from DB

SchedulerSettings schedulerSettings;
Collection<SchedulerSettings> col = schedulerStore.getAll();
if (col != null && col.size() > 0) {
	System.out.println("Found the scheduler in the db!");
	//get the first one
	schedulerSettings = (SchedulerSettings)(col.toArray())[0];
	if (schedulerSettings != null && schedulerSettings.getRefreshTime() > 0){
		refreshRate = schedulerSettings.getRefreshTime();
		System.out.println("Found the scheduler refresh rate in the db: " + refreshRate);	
	}
}
DashboardData myDashboardData = DashboardData.getInstance();
//TODO: CR 0.4 get refresh schedule from DB
//refreshRate = myDashboardData.getRefreshRate();

//TODO: Rev 1.0 refresh from 10 minutes
//<input type="radio"  ((refreshRate == 5) ? "checked" : "") name="time" value="5">5
%>
  <input type="radio" <%= ((refreshRate == 10) ? "checked" : "") %> name="time" value="10">10
  <input type="radio" <%= ((refreshRate == 15) ? "checked" : "") %> name="time" value="15">15
  <input type="radio" <%= ((refreshRate == 20) ? "checked" : "") %> name="time" value="20">20
  <input type="radio" <%= ((refreshRate == 30) ? "checked" : "") %> name="time" value="30">30
  <input type="radio" <%= ((refreshRate == 60) ? "checked" : "") %> name="time" value="60">60
<input type="submit" class="box">
<BR>
</form>
		
<br><br>
<b>Add a new url for testing:</b>
<form action="AddNewURLFormHandler" method="get">


URL description name: <br><input type="text" name="url_name" size="80"><BR>
URL to test: <br><input type="text" name="url" size="80"><BR>
  
<br>GET/POST:
  <input type="radio" checked name="get" value="false">POST
  <input type="radio" name="get" value="true">GET
<p>
POST : JSON / FORM?
  <input type="radio" checked name="post.json" value="true">JSON
  <input type="radio" name="post.json" value="false">Form
<br>Example of the Post JSON<br>
<textarea name="json.txt" id="json.txt" cols="80" rows="5"></textarea>
<br>
<input type="submit" class="box">
<BR>
</form>

<%-- 
Mark the checkbox/es of the url/s to remove from the dashboard:<BR>
<form action="RemoveUrlFormHandler" method="get">
	<table>
	
	
--%>

		<%	
		
		for (UrlStatusPersisted doc : store.getAll()) {
			if (doc.getUrl() != null) {
			
				//getting the values for the modify
				myCells = myCells 
				+ "\n<tr>\n<form action=\"ModifyUrlFormHandler\" method=\"get\"><td><button name='delete' value='"
				+ doc.get_id()
				// TODO: CR #0.1.1 added - confirmation of the deletion
				+ "' type='submit' class='box_red' onclick=\"return confirm('Are you sure?')\">X</button>"
				+ "</td>\n<td>"
				+ "<input type='text' name='url_name."
						+ doc.get_id()
						+ "' value='" 
				+ doc.name 
				+ "' size='80'><br>"						
				+ "<input type='text' name='url."
						+ doc.get_id()
						+ "' value='" 
				+ doc.getUrl() 
				+ "' size='80'>"
				+ "</td>\n<td>" 
				+ "<input type='radio' " 
				+ ((doc.isGet) ? "checked" : "") 
				+ " name='get."
				+ doc.get_id()
				+ "'  value='true'>" + 
				"</td><td>" + 
				"<input type='radio' " 
				+ (!doc.isGet ? "checked" : "")
				+ " name='get."
				+ doc.get_id()
				+ "'  value='false'>"
				+ "</td>\n<td>" 
				+ "<textarea name='json.txt."
						+ doc.get_id()
						+ "' id='"
								+ doc.get_id()
								+ "' cols='80' rows='5'>"
				+ (doc.getJsonText() != null ? doc.getJsonText() : "") 
				+ "</textarea></td>\n<td>" 
				+ "<button name='modify' value='"
				+ doc.get_id()
				+"' type='submit' class='box'>update</button>"
				+ "</td>\n </form>	</tr>\n";
				
			
			
			//<tr><td><input type="checkbox" name="urls" value='"< %= doc.get_id()% >"'>< %= doc.getName() % ></td></tr>

			}
		}
	}
	// </table>
	//<button name="delete" value="value" type="submit" class="box_red">delete</button>
	//</form><br><br>	



// TODO: CR0.4 table with X for deletes and modify urls inline

%>
Mark the radio of the url to modify:<BR>
<!-- form action="ModifyUrlFormHandler" method="get" -->
	<table>
		<%= myCells %>
    </table>
	
<BR>
<!--  /form -->	

</body>
</html>