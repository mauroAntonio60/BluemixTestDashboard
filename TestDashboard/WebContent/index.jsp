<%@page import="com.ibm.dashboard.singleton.RefreshData"%>
<%@page import="com.ibm.dashboard.store.UrlStatusPersisted"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page
	import="
    java.util.ArrayList,
    java.util.Collection,
    java.text.SimpleDateFormat,
    java.util.Date,
    java.lang.Integer,
	java.util.List,
	java.io.File,
	javax.net.ssl.SSLContext,
	org.apache.http.ssl.SSLContextBuilder,
	org.apache.http.conn.ssl.NoopHostnameVerifier,
	org.apache.http.conn.ssl.SSLConnectionSocketFactory,
	org.apache.http.conn.ssl.TrustSelfSignedStrategy,
	org.apache.http.ssl.SSLContexts,
	java.security.KeyStore,
	java.io.FileInputStream,
	java.text.DateFormat,
	java.util.TimeZone,
	
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
    com.ibm.dashboard.store.*,
    com.ibm.dashboard.singleton.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
	int NUMBER_OF_DISPLAYED_RESULTS = 12;
	int TRESHOLD_TO_YELLOW = 1000;
	String foundRefreshTime;
	int refreshRate = -1;

	DashboardData dashboardData = DashboardData.getInstance();
	RefreshData refreshData = RefreshData.getInstance();

	foundRefreshTime = request.getParameter("time");
	if ((foundRefreshTime != null) && (!foundRefreshTime.isEmpty()) && (foundRefreshTime != "")) {
		System.out.println("found time: " + foundRefreshTime);
		//setting refresh in the background
		refreshRate = Integer.parseInt(foundRefreshTime);

		System.out.println("setting refreshing for " + refreshRate + "min");

		//TODO: CR 0.4 get refresh schedule from DB

		SchedulerSettingsStore schedulerStore = SchedulerSettingsStoreFactory.getInstance();
		if (schedulerStore == null) {
			System.out.println("no store defined!");
%>

No connection to the DB - refresh rate set only temporarily in memory

<%
		} else {
		//TODO: CR 0.4 get refresh schedule from DB

			SchedulerSettings schedulerSettings;
			Collection<SchedulerSettings> col = schedulerStore.getAll();
			if (col != null && col.size() > 0) {
				System.out.println("Found the scheduler in the db!");
				//get the first one
				schedulerSettings = (SchedulerSettings) (col.toArray())[0];
				schedulerSettings.setRefreshTime(refreshRate);
				schedulerStore.update(schedulerSettings.get_id(), schedulerSettings);
				System.out.println("updated the scheduler " + schedulerSettings.get_id() + " in the db with refresh rate: " + refreshRate);

			} else {
				System.out.println("There are no settings in the DB - creating a scheduler record");
				schedulerSettings = new SchedulerSettings();

				schedulerSettings.setRefreshTime(refreshRate);
				schedulerStore.persist(schedulerSettings);
				System.out.println("persisted the scheduler in the db with refresh rate: " + refreshRate);

			}
		}

		refreshData.isRefreshing = true;
		dashboardData.setRefreshRate(Integer.parseInt(foundRefreshTime) * 60);
		refreshData.refreshingData();

		// TODO: clear the time parameter 
		response.sendRedirect("index.jsp");

	} else {
		//foundRefreshTime = "60";
	}
%>

<meta http-equiv="refresh" content="60" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Automatic Dashboard rev 1.0</title>
<link rel="stylesheet" href="dashboard.css">
</head>
<body>
	<table>
		<tr>
			<td height=28></td>
			<td></td>
			<td></td>
		</tr>
		<tr>
			<td height=50 width=68 class="menu"></td>
			<td width=269 class="logo"></td>
			<td class="text">test dashboard</td>
		</tr>
	</table>

	<a href="manageUrls.jsp">To <b>manage urls</b> click here
	</a>


	<%
		UrlStatusPersistedStore store = UrlStatusPersistedStoreFactory.getInstance();

		if (store == null) {
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
	<br>
	<br>
	<%
		} else {
			
			String message2Print = "The next scheduled refresh will take place at " + dashboardData.nextRun  + " Pacific time";
			if (dashboardData.nextRun != null && dashboardData.nextRun !=""){
				%><br><b><%= message2Print %></b><BR>
				<%
			}
			//get the _id of the record from the url parameter :

			//working with the dashboarddata singleton cache instead of pulling the data from DB direct
	%>
	<table>

		<%
			// TODO decouple reading the status from checking the apps
				// get the singleton
				// get the latest json 
				// parse json

				// TODO do only display of data
				System.out.print("Refreshed the jsp page with urls: ");
				for (UrlStatusPersisted doc : dashboardData.getUrls()) {
					if (doc.getUrl() != null) {
		%>
		<tr>
			<td><%=doc.getName()%></td>
			<%
				try {
								//iterate through the statuses
								// TODO request to show only 40 first calls
								int max = (doc.urlResponses).length;
								
								//CR rev1.0 max 12 runs
								if (max > NUMBER_OF_DISPLAYED_RESULTS) {
									max = NUMBER_OF_DISPLAYED_RESULTS;
									//System.out.println("CR - showing only first 40 calls");
								}
								//CR rev0.4 adding time counting in minutes
								long time = 0;
								String userInput = doc.urlTimes[0];
								String expectedPattern = "yyyy-MM-dd HH:mm:SS z"; //from UrlStatus
								SimpleDateFormat formatter = new SimpleDateFormat(expectedPattern);
								//System.out.println("now time: ");
								Date datetimeZero = new Date();

								Date date;
								String dateString = "";
								long milisecondsZero = datetimeZero.getTime();
								long milisecondsTest;
								for (int i = 0; i < max; i++) {

									//cr rev0.4 adding time...

									try {
										// (2) give the formatter a String that matches the SimpleDateFormat pattern
										userInput = doc.urlTimes[i];
										//System.out.println("old call time: ");
										date = formatter.parse(userInput);
										//rev 1.0 setting the refresh time
										DateFormat df = new SimpleDateFormat("HH:mm");
										df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
										dateString = df.format(date);

										// (3) prints out "Tue Sep 22 00:00:00 EDT 2009"
										//System.out.println(dateString);
									} catch (Exception e) {
										System.out.println(e);
										date = datetimeZero;
									}
									milisecondsTest = date.getTime();
									time = (milisecondsZero - milisecondsTest) / (1000 * 60);

									//skip nulls
									// TODO: remove initial nulls
									if (doc.urlResponses[i] == null) {
										//System.out.println("skipping null values");
									} else if ("HTTP/1.1 200 OK".contentEquals(doc.urlResponses[i])) {

										//CR rev0.4 pretty forming information about the call
										String prettyInfo = "";
										int totalResponseTime = 0;

										//1 time - in minutes from the time 0
										prettyInfo = "request date: " + doc.urlTimes[i] + "\n";
										//2 info on the seperate calls
										prettyInfo = prettyInfo + "operation responses for: " + "\n";
										String jsonString = doc.urlLogTails[i].replaceAll("\"","'");
										//System.out.println("ok - for :"+ jsonString);

										UrlResponseDoc urlResponseDoc = new UrlResponseDoc(jsonString);
										//UrlResponse[] urlResponses = urlResponseDoc.getOperationArray();
										UrlResponse[] urlResponses = urlResponseDoc.getUrlResponses();
										for (int y = 0; y < urlResponses.length; y++) {
											prettyInfo = prettyInfo + "\n";
											prettyInfo = prettyInfo + urlResponses[y].getType() + "\n";
											prettyInfo = prettyInfo + " - time: " + urlResponses[y].getResponse_time()
													+ "\n";
											prettyInfo = prettyInfo + " - code: " + urlResponses[y].getResponse_code()
													+ "\n";

											prettyInfo = prettyInfo + " - details:" + urlResponses[y].getDescString().replaceAll("\"","'") + "\n";

											totalResponseTime = totalResponseTime + urlResponses[y].getResponse_time();
										}
										//before info: doc.urlTimes[i] + "\n" + doc.urlLogTails[i]

										prettyInfo = "total response time: " + totalResponseTime + "\n" + prettyInfo;
			%>
			<td align=center><img
				src=<%=((totalResponseTime < TRESHOLD_TO_YELLOW) ? "'./img/ok.png' alt='ok'"
											: "'./img/yellow.png' alt='too long'")%>
				title="<%=prettyInfo%>" /> 
				<br><%=(time)%> 
				<BR><%= dateString + " " %> </td>
			<%
				} else {
										// System.out.println("fail - for "+i);
			%>
			<td align=center><img src="./img/fail.png" alt="fail"
				title="<%=doc.urlTimes[i] + "\n" + doc.urlResponses[i]%>" /> 
				<br><%=(time)%>
				<BR><%= dateString + " " %> 
			</td>
			<%
				}
								}

							} finally {
								//System.out.println("end");
								System.out.print(".");
							}
			%>
		</tr>
		<%
			}
				}
			}
			System.out.println();
		%>
	</table>


</body>
</html>