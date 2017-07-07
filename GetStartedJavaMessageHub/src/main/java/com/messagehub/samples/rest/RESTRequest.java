/**
 * Copyright 2015-2016 IBM
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
/**
 * Licensed Materials - Property of IBM
 * (c) Copyright IBM Corp. 2015-2016
 */
package com.messagehub.samples.rest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
//import org.apache.log4j.Level;
//import org.apache.log4j.//logger;

class RESTRequest {
	// private static final //logger //logger =
	// //logger.get//logger(RESTRequest.class);

	private String apiKey, baseUrl;

	public RESTRequest(String baseUrl, String apiKey) {
		this.apiKey = apiKey;
		this.baseUrl = baseUrl;
	}

	/**
	 * Execute a GET request against the specified REST target.
	 *
	 * @param target
	 *            {String} The REST API target to run against (for example,
	 *            '/admin/topics')
	 * @param acceptHeader
	 *            {Boolean} A flag to notify the caller whether or not to
	 *            include the 'Accept' header in its request.
	 * @return {String} The response received from the server.
	 * @throws Exception
	 */
	public String get(String target, boolean acceptHeader) throws Exception {
	       HttpsURLConnection connection = null;

	        if (!target.startsWith("/")) {
	            target = "/" + target;
	        }

	        try {
	            // Create secure connection to the REST URL.
	            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
	            sslContext.init(null, null, null);

	            URL url = new URL(baseUrl + target);
	            connection = (HttpsURLConnection) url.openConnection();
	            connection.setSSLSocketFactory(sslContext.getSocketFactory());
	            connection.setRequestMethod("GET");
	            // Apply API key header and kafka content type Accept header if
	            // the 'acceptHeader' flag is set to true.
	            connection.setRequestProperty("X-Auth-Token", this.apiKey);

	            if (acceptHeader) {
	                connection.setRequestProperty("Accept", "application/vnd.kafka.binary.v1+json");
	            }

	            // Read the response data from the request and return
	            // it to the function caller.
	            InputStream is = connection.getInputStream();
	            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	            String inputLine = "";
	            StringBuffer response = new StringBuffer();

	            while ((inputLine = rd.readLine()) != null) {
	                response.append(inputLine);
	            }

	            rd.close();

	            return response.toString();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            if (connection != null) {
	                connection.disconnect();
	            }
	        }

	        return "";
		/*if (!target.startsWith("/")) {
			target = "/" + target;
		}
		String uri = baseUrl + target;

		CloseableHttpClient httpClient = null;

		try {
			SSLContext sslContext = null;
			sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, null, null);

			httpClient = HttpClients.custom().setSSLContext(sslContext)
					.setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

			// httpClient = dashboardHttp.getHttpClient();
			String body = "";
			try {
				HttpGet httpget = new HttpGet(uri);
				//connection.setRequestProperty("X-Auth-Token", this.apiKey);
	            //connection.setRequestProperty("Content-Type", "application/json");
				//connection.setRequestProperty("Accept", "application/vnd.kafka.binary.v1+json")
				httpget.addHeader("X-Auth-Token", apiKey);
				httpget.addHeader("Content-Type", "application/json");
				httpget.addHeader("Accept", "application/vnd.kafka.binary.v1+json");
				
				
				System.out.println("Executing request " + httpget.getRequestLine());
				CloseableHttpResponse response = null;
				response = httpClient.execute(httpget);

				try {
					HttpEntity entity = response.getEntity();
					System.out.println("----------------------------------------");
					System.out.println(response.getStatusLine());
					
					if ("HTTP/1.1 200 OK".contentEquals(response.getStatusLine().toString())) {
						// System.out.println("ok");

						// store the information from the response

						if (entity != null) {
							body = EntityUtils.toString(entity);
						}
						System.out.println(body);

					} else {
						System.out.println("failed");
						body = " no body :(";
					}

					EntityUtils.consume(entity);

				} finally {
					response.close();
				}
			} catch (Exception e) {
				System.out.println("Error! Unable to connect to " + uri + e.toString());
				// urlResponses = addNewElementToArray(urlResponses, "Error!
				// Unable to connect to " + url + e.toString());
				// urlTimes = addNewElementToArray(urlTimes, dateInPstZone);
				// urlLogTails = addNewElementToArray(urlLogTails,
				// e.toString());

				// e.printStackTrace();
			} finally {
				httpClient.close();
			}*/

			// Create secure connection to the REST URL.
			// SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			// sslContext.init(null, null, null);

			// URL url = new URL(baseUrl + target);
			/*
			 * connection = (HttpsURLConnection) url.openConnection();
			 * connection.setSSLSocketFactory(sslContext.getSocketFactory());
			 * connection.setRequestMethod("GET"); // Apply API key header and
			 * kafka content type Accept header if // the 'acceptHeader' flag is
			 * set to true. connection.setRequestProperty("X-Auth-Token",
			 * this.apiKey);
			 * 
			 * if (acceptHeader) { connection.setRequestProperty("Accept",
			 * "application/vnd.kafka.binary.v1+json"); }
			 * 
			 * // Read the response data from the request and return // it to
			 * the function caller. InputStream is =
			 * connection.getInputStream(); BufferedReader rd = new
			 * BufferedReader(new InputStreamReader(is)); String inputLine = "";
			 * StringBuilder response = new StringBuilder();
			 * 
			 * while ((inputLine = rd.readLine()) != null) {
			 * response.append(inputLine); }
			 * 
			 * rd.close();
			
			return body;// response.toString();
		} catch (Exception e) {
			System.out.println("REST POST request failed with exception: " + e);
			throw e;
		}
*/
	}

	/**
	 * Execute a GET request against the specified REST target.
	 *
	 * @param target
	 *            {String} The REST API target to run against (for example,
	 *            '/admin/topics')
	 * @param body
	 *            {String} The data to be provided in the body section of the
	 *            POST request.
	 * @param ignoredErrorCodes
	 *            {int[]} An list of error codes which will be ignored as a
	 *            side-effect of the request. Can be provided as null.
	 * @return {String} The response received from the server.
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public String post(String target, String body, int[] ignoredErrorCodes)
			throws IOException, NoSuchAlgorithmException, KeyManagementException {
/*		int responseCode = 0;

		if (!target.startsWith("/")) {
			target = "/" + target;
		}
		String uri = baseUrl + target;

		CloseableHttpClient httpClient = null;

		try {
			String body = "";
			SSLContext sslContext = null;
			sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, null, null);

			httpClient = HttpClients.custom().setSSLContext(sslContext)
					.setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

			try {
				String json = "";
				StringEntity stringEntity = new StringEntity(json,
						ContentType.create("application/json", Consts.UTF_8));
				stringEntity.setChunked(true);

				HttpPost httppost = new HttpPost(uri);
				httppost.addHeader("X-Auth-Token", apiKey);
				httppost.addHeader("Content-Type", "application/json");
				httppost.addHeader("Accept", "application/vnd.kafka.binary.v1+json");
				
				System.out.println("Executing request " + httppost.getRequestLine());

				httppost.setEntity(stringEntity);

				CloseableHttpResponse response = httpClient.execute(httppost);

				try {
					HttpEntity entity = response.getEntity();
					System.out.println("----------------------------------------");
					System.out.println(response.getStatusLine());
					// insert response to array

					// add element to beginning of array
					if ("HTTP/1.1 200 OK".contentEquals(response.getStatusLine().toString())) {
						System.out.println("ok");

						// store the information from the response
						if (entity != null) {
							body = EntityUtils.toString(entity);
							System.out.println("no white spaced string: " + body);
						}
						System.out.println(body);

					} else {
						System.out.println("failed");
						body = " no body :(";
					}

					EntityUtils.consume(entity);

				} finally {
					response.close();
				}
			} catch (Exception e) {
				System.out.println("Error! Unable to connect to " + uri + e.toString());

				// e.printStackTrace();
			} finally {
				httpClient.close();
			}
			return body;
			
		} catch (Exception e) {
			// TODO KeyManagementException, NoSuchAlgorithmException,
			// ClientProtocolException, IOException
			System.out.println("Error! Unable to connect to " + uri + e.toString());
			return null;

		}*/
		
	       HttpsURLConnection connection = null;
	        int responseCode = 0;

	        if (!target.startsWith("/")) {
	            target = "/" + target;
	        }

	        try {

	            // Create secure connection to the REST URL.
	            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
	            sslContext.init(null, null, null);

	            URL url = new URL(baseUrl + target);
	            connection = (HttpsURLConnection) url.openConnection();
	            connection.setSSLSocketFactory(sslContext.getSocketFactory());
	            connection.setDoOutput(true);
	            connection.setRequestMethod("POST");

	            // Apply headers, in this case, the API key and Kafka content type.
	            connection.setRequestProperty("X-Auth-Token", this.apiKey);
	            connection.setRequestProperty("Content-Type", "application/json");

	            // Send the request, writing the body data
	            // to the output stream.
	            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	            wr.writeBytes(body);
	            wr.close();

	            responseCode = connection.getResponseCode();

	            // Retrieve the response, transform it, then
	            // return it to the caller.
	            InputStream is = connection.getInputStream();
	            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	            StringBuilder response = new StringBuilder();
	            String line;

	            while ((line = rd.readLine()) != null) {
	                response.append(line);
	                response.append('\r');
	            }

	            rd.close();

	            return response.toString();
	        } catch (Exception e) {
	            boolean isIgnored = false;

	            // Filter out error codes which are ignored. If the
	            // response code is in the ignore list, the error
	            // is not printed.
	            if (ignoredErrorCodes != null) {
	                for (int i = 0; i < ignoredErrorCodes.length; i++) {
	                    if (ignoredErrorCodes[i] == responseCode) {
	                        isIgnored = true;
	                    }
	                }
	            }

	            if (!isIgnored) {
	                e.printStackTrace();
	            }
	        } finally {
	            if (connection != null) {
	                connection.disconnect();
	            }
	        }

	        return "";

		/*
		 * try {
		 * 
		 * // Create secure connection to the REST URL. SSLContext sslContext =
		 * SSLContext.getInstance("TLSv1.2"); sslContext.init(null, null, null);
		 * 
		 * URL url = new URL(baseUrl + target); connection =
		 * (HttpsURLConnection) url.openConnection();
		 * connection.setSSLSocketFactory(sslContext.getSocketFactory());
		 * connection.setDoOutput(true); connection.setRequestMethod("POST");
		 * 
		 * // Apply headers, in this case, the API key and Kafka content type.
		 * connection.setRequestProperty("X-Auth-Token", this.apiKey);
		 * connection.setRequestProperty("Content-Type", "application/json");
		 * 
		 * // Send the request, writing the body data // to the output stream.
		 * DataOutputStream wr = new
		 * DataOutputStream(connection.getOutputStream()); wr.writeBytes(body);
		 * wr.close();
		 * 
		 * responseCode = connection.getResponseCode();
		 * 
		 * // Retrieve the response, transform it, then // return it to the
		 * caller. InputStream is = connection.getInputStream(); return
		 * inputStreamToString(is); } catch (IOException e) { boolean isIgnored
		 * = false;
		 * 
		 * // Filter out error codes which are ignored. If the // response code
		 * is in the ignore list, the error // is not printed. if
		 * (ignoredErrorCodes != null) { for (int i = 0; i <
		 * ignoredErrorCodes.length; i++) { if (ignoredErrorCodes[i] ==
		 * responseCode) { isIgnored = true; } } }
		 * 
		 * if (!isIgnored || connection == null) {
		 * System.out.println("REST POST request failed with exception: " + e);
		 * //// logger.log(Level.ERROR, //// "REST //// POST //// request ////
		 * failed //// with //// exception: //// " //// + //// e, //// e); throw
		 * e; } else { return inputStreamToString(connection.getErrorStream());
		 * } } finally { if (connection != null) { connection.disconnect(); } }
		 */

	}

	private String inputStreamToString(InputStream is) throws IOException {
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		StringBuilder response = new StringBuilder();
		String line;

		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}

		rd.close();

		return response.toString();
	}
}
