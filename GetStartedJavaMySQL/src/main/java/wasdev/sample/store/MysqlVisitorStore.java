/*******************************************************************************
 * Copyright (c) 2017 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package wasdev.sample.store;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.google.gson.JsonObject;

import wasdev.sample.Visitor;

public class MysqlVisitorStore implements VisitorStore {

	private Connection conn = null;
	
	public MysqlVisitorStore() {
		conn = createClient();

		if (conn != null) {
			Statement st;
			
			try {
				st = conn.createStatement();
	            ResultSet rs = st.executeQuery("SHOW DATABASES");
	            System.out.println("Connected to Mysql - Databases:");
	            while (rs.next()) {
	                String dbname = rs.getString("Database");
	                System.out.format(" - %s\n", dbname);
	            }
	            rs.close();
	            st.close();

	        } catch (Exception ex) {
	            ex.printStackTrace();
	            System.out.println("there is a problem with getting a connection to Mysql");
	        }			
		}
	}

	public Connection getConnection() {
		return conn;
	}

	private static Connection createClient() {
		String url = ""; 
		if (System.getenv("VCAP_SERVICES") != null) {
			// When running in Bluemix, the VCAP_SERVICES env var will have the
			// credentials for all bound/connected services
			// Parse the VCAP JSON structure looking for mysqll.
			JsonObject mysqlCredentials = VCAPHelper.getCloudCredentials("mysql");
			if (mysqlCredentials == null) {
				System.out.println("No mysql database service bound to this application");
				return null;
			}
			//System.out.println(mysqlCredentials);
			url = mysqlCredentials.get("uri").getAsString();
			//System.out.println("got mysql credentials from VCAP: " + url);

			// url manipulation - getting a User, the password, a link
			// mysql://admin:CUYOYTUESBZINXNG@sl-us-south-1-portal.10.dblayer.com:25511/compose
			
			String url1 = url.substring(8);//11); //removing "postgres://"
			// admin:CUYOYTUESBZINXNG@sl-us-south-1-portal.10.dblayer.com:25511/compose

			// search for : and @ to obtain user and password
			int indexEndUser = url1.indexOf(":");
			int indexEndPassword = url1.indexOf("@");
			// user  password         url
			String user = url1.substring(0, indexEndUser);
			String password = url1.substring(indexEndUser + 1, indexEndPassword);
			String urlHost = url1.substring(indexEndPassword + 1);
			
			//System.out.println(user);
			//System.out.println(password);
			//System.out.println(urlHost);

			// TODO: Add SSL
			/**
			 * TODO: add trust store manipulation + SSL 
			 * mysql://[username]:[password]@[host]:[port]/compose

		       System.setProperty("javax.net.ssl.trustStore","/home/project/truststore");
		           System.setProperty("javax.net.ssl.trustStorePassword","somepass");

		       String user = "admin";
		       String password = "mypass";
		       String URL = "jdbc:mysql://__________.dblayer.com:15942/compose" +

		                "?verifyServerCertificate=true"+
		                "&useSSL=true" +
		                "&requireSSL=true";
			 */
			

			url = "jdbc:mysql://" + urlHost + "?user=" + user + "&password=" + password + "&useSSL=false";
			
			//System.out.println("Connecting database...");
			Connection connection;
			try {
				connection = DriverManager.getConnection(url, user, password);
			    System.out.println("MySql Database connected!");
			    return connection;
			} catch (SQLException e) {
			    //throw new IllegalStateException("Cannot connect the database!", e);
				System.out.println("Cannot connect the database! - " + e.toString());
				return null;
			}
			
			
		} else {
			System.out.println("Running locally. Looking for credentials in mysql.properties");
			url = VCAPHelper.getLocalProperties("mysql.properties").getProperty("mysql_url");
			if (url == null || url.length() == 0) {
				System.out.println(
						"To use a database, set the MySQL url in src/main/resources/mysql.properties");
				return null;
			}
		}

		
		
		try {
			System.out.println("Connecting to mysql " + url);
			System.out.println("Loading driver...");
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			Connection conn = DriverManager.getConnection(url);

			System.out.println("Connected locally to mysql ");
			return conn;
		} catch (Exception e) {
			System.out.println("Unable to connect to database");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	/**
	 * not used for mysql
	 */
	public Object getDB() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Visitor> getAll() {
		System.out.println("starting reading all");
		Statement st1;
		Visitor vi = new Visitor();
		List<Visitor> visitors = new ArrayList<Visitor>();

		try {
			st1 = conn.createStatement();
			String qs1 = "SELECT * FROM visitor";
			ResultSet rs = st1.executeQuery(qs1);
			while (rs.next()) {

				
				int id = rs.getInt("_id");
				String visitorName = rs.getString("name");
				System.out.println("read name: " + visitorName + ", id: " + id );
				
				vi.set_id(id + "");
				vi.setName(visitorName);
				visitors.add(vi);
			}
			rs.close();
			st1.close();
			System.out.println("select done ");
		} catch (SQLException sqle) {
			System.out.println("Could not read");
		}
		return visitors;
	}

	@Override
	public void delete(String id) {
		Statement st1;

		try {
			st1 = conn.createStatement();
			
			String sql = "DELETE from visitor where _id="+ id;
	        st1.executeUpdate(sql);
	        st1.close();
	        //System.out.println("delete done ");

		} catch (SQLException sqle) {
			System.out.println("Could not delete");
			sqle.printStackTrace();

		}
	}

	@Override
	public int count() throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Visitor get(String name) {
		
		System.out.println("starting reading : " + name);
		Statement st1;
		Visitor vi = new Visitor();

		try {
			st1 = conn.createStatement();
			String qs1 = "SELECT * FROM visitor where name = '" + name + "'";
			ResultSet rs = st1.executeQuery(qs1);
			if (rs.next()) {

				int id = rs.getInt("_id");
				String visitorName = rs.getString("name");

				vi.set_id(id + "");
				vi.setName(visitorName);

			}
			rs.close();
			st1.close();
			System.out.println("select done ");
		} catch (SQLException sqle) {
			System.out.println("Could not read");
		}
		return vi;
	}

	@Override
	public Visitor persist(Visitor vi) {

		Statement st, st1;
		try {
			st = conn.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS visitor(_id SERIAL NOT NULL PRIMARY KEY,name varchar(225) NOT NULL UNIQUE)";
			st.executeUpdate(sql);
			st.close();
			//System.out.println("table created");
			
		} catch (SQLException e) {
			System.out.println("Could not persist (create table)");
			e.printStackTrace();
		}

		try {
			st1 = conn.createStatement();
			String sql = "INSERT INTO visitor (NAME)" + "VALUES ('" + vi.getName() + "')";
			st1.executeUpdate(sql);

			st1.close();
			//System.out.println("insert issued");

		} catch (SQLException sqle) {
			System.out.println("Could not persist");
			sqle.printStackTrace();
		}
				
		return vi;
	}

	@Override
	public Visitor update(String id, Visitor vi) {
		//System.out.println("starting udpate : " + id);
		Statement st1, st2;
		Visitor updatedVi = new Visitor();

		try {
			st1 = conn.createStatement();
			
			String sql = "UPDATE visitor set name = '"+ vi.getName() +"' where _id="+ id;
	        st1.executeUpdate(sql);
	        st1.close();
	        
	        st2 = conn.createStatement();
			String qs1 = "SELECT * FROM visitor where _id = " + id ;
			ResultSet rs = st2.executeQuery(qs1);
			if (rs.next()) {
				System.out.println("found updated result: " + rs.toString());
				int updatedId = rs.getInt("_id");
				String visitorName = rs.getString("name");

				updatedVi.set_id(updatedId + "");
				updatedVi.setName(visitorName);

			}
			
			rs.close();
			st2.close();
			//System.out.println("update done ");

		} catch (SQLException sqle) {
			System.out.println("Could not update");
			sqle.printStackTrace();
		}
		return updatedVi;
	}

}
