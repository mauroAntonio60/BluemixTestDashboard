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


<%@ taglib uri="https://bluemix.net/tlds/bluedashtags" prefix="refreshT"%>
<%@ taglib uri="https://bluemix.net/tlds/bluedashtags/presentDashboard" prefix="dashboard"%>
<refreshT:refreshTime/>

<meta http-equiv="refresh" content="60" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Automatic Dashboard rev 1.1</title>
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



<% 	//TODO: CR 1.1 custom tag to replace the code	
	
	//tag comes here
%>	
	<dashboard:presentDashboard NUMBER_OF_DISPLAYED_RESULTS="15" TRESHOLD_TO_YELLOW="1200"/>


</body>
</html>