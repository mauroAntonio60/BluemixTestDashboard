/**
 * 
 */
package com.ibm.dashboard;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * @author marek sadowski mwsadows@us.ibm.com
 * @since 20170309
 *
 */
public class DashboardHttpClient {
	CloseableHttpClient httpClient = null;

	/**
	 * using https connections with NoopHostnameVerifier
	 */
	public DashboardHttpClient(SSLContext sslContext) {
		httpClient = HttpClients.custom()
				.setSSLContext(sslContext)
				.setSSLHostnameVerifier(new NoopHostnameVerifier())
				.build();
	}
	
	/**
	 * using http connections
	 */
	public DashboardHttpClient() {
		httpClient = HttpClients.createDefault();
	}
	
	public CloseableHttpClient getHttpClient() {
		return httpClient;
		
	}

	
}
