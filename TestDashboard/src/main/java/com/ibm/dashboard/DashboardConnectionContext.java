/**
 * 
 */
package com.ibm.dashboard;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

/**
 * @author marek sadowski mwsadows@us.ibm.com
 * @since 20170309
 *
 */
public class DashboardConnectionContext {
	SSLContext sslContext = null;
	
	public DashboardConnectionContext() throws NoSuchAlgorithmException, KeyManagementException {
		// TODO Auto-generated constructor stub
		sslContext = SSLContext.getInstance("SSL");
		sslContext.init( null, null, null );
	}
	
	public SSLContext getContext () {
		return sslContext;
		
	}
	

}
