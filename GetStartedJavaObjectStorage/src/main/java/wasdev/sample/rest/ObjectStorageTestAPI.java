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
package wasdev.sample.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

import com.google.gson.Gson;

import wasdev.sample.Visitor;
import wasdev.sample.store.VisitorStore;
import wasdev.sample.store.VisitorStoreFactory;

@ApplicationPath("test")
@Path("/objectstorage")
public class ObjectStorageTestAPI extends Application {

	// Our database store
	VisitorStore store = VisitorStoreFactory.getInstance();
	Visitor operationalVisitor;

	/**
	 * //cleaning the db afterwards //System.out.println(deleteAll());
	 * 
	 * @return A test case result of all CRUD operations
	 */
	@GET
	@Path("/all")
	@Produces({ "application/json" })
	public String doTestCRUD() {

		if (store == null) {
			return "{service: 'objectstorage', operations:[], response_code: 404, desc:'Error: no connection to objectstorage'}";
		}

		// Call objectstorage Read
		System.out.println("entering test...");
		String response = "{service: 'objectstorage', operations: [" + objectstorageCreate() 
				+ "," 
				+ objectstorageRead() 
				+ ","
				+ objectstorageDelete() 
				+ "]"
				+ ", response_code: 200, desc:'operations implemented crd/CRD'}";
		System.out.println("ending test...");
		return response;

	}

	

	/**
	 * Test CRUD for objectstorage. REST API GetStartedJava/test/objectstorage/read : <code>
	 * GET http://localhost:9080/GetStartedJava/test/objectstorage/read
	 * </code> 10 end points
	 * 
	 * Java (Liberty) - Test redis (GET) - Test Mongo (GET) - Test Postgres
	 * (GET) - Test MessageHub (GET) - Test Redis (GET)
	 * 
	 * 
	 * @return A test case result of all CRUD operations
	 */
	@GET
	@Path("/read")
	@Produces({ "application/json" })
	public String doTestR() {

		if (store == null) {
			return "{service: 'objectstorage', operations:[], response_code: 404, desc:'Error: no connection to objectstorage'}";
		}

		// Call objectstorage Read
		// TODO store id of the record to read

		String response = "{service: 'objectstorage', operations: [" + objectstorageRead() + "]"
				+ ", response_code: 200, desc:'operation R/CRD'}";

		return response;
	}


	/**
	 * Reading all the documents from the db - returning the code 200, response
	 * time in ms, and the contents of the db in desc
	 * 
	 * @return A test case result of READ/CRUD operation
	 */
	private String objectstorageRead() {
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		long startTime;
		long endTime;

		// timed operation
		startTime = System.currentTimeMillis();
		operationalVisitor = read(operationalVisitor.get_id());
		endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);
		responseDesc = "{'visitor id': '" + operationalVisitor.get_id() + "'}";
		String responseString = "{type: 'read', response_time: " + responseTime + ", response_code: " + responseCode
				+ ", desc: " + responseDesc + "}";
		System.out.println(responseString);
		return responseString;
	}

	/**
	 * Writing a document to the db - returning the code 200, response time in
	 * ms, and the record id in the response
	 * 
	 * @return A test case result of CREATE/CRUD operation
	 */
	private String objectstorageCreate() {
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		Visitor visitor = new Visitor();
		visitor.setName("test case: " + System.currentTimeMillis());

		long startTime;
		long endTime;

		// timed operation
		startTime = System.currentTimeMillis();
		visitor.set_id(startTime+"");
		operationalVisitor = write(visitor);
		endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);
		responseDesc = "{'visitor': '" + operationalVisitor.get_id() + "," + operationalVisitor.getName() + "'}";
		String responseString = "{type: 'create', response_time: " + responseTime + ", response_code: " + responseCode
				+ ", desc: " + responseDesc + "}";
		System.out.println(responseString);
		return responseString;
	}

	/**
	 * deleting in a document to the db - returning the code 200, response time
	 * in ms, and the record id in the response
	 * 
	 * @return A test case result of delete/CRUD operation
	 */
	private String objectstorageDelete() {
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		long startTime;
		long endTime;

		// timed operation
		startTime = System.currentTimeMillis();
		delete(operationalVisitor.get_id());
		endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);
		responseDesc = "{'visitor id': '" + operationalVisitor.get_id() + "'}";
		String responseString = "{type: 'delete', response_time: " + responseTime + ", response_code: " + responseCode
				+ ", desc: " + responseDesc + "}";
		System.out.println(responseString);
		return responseString;
	}

	/**
	 * insert a visitor
	 * 
	 * @param visitor
	 * @return an object of a visitor with populated db _id and _rev fields
	 */
	private Visitor write(Visitor visitor) {
		visitor = store.persist(visitor);
		return visitor;
	}

	private Visitor read(String id) {
		Visitor visitor = store.get(id);
		return visitor;
	}

	private void delete(String id) {
		store.delete(id);
	}
	
	private String readAll() {
		List<String> names = new ArrayList<String>();
		for (Visitor doc : store.getAll()) {
			String name = doc.getName();
			if (name != null) {
				names.add(name);
			}
		}
		return "'" + new Gson().toJson(names) + "'";
	}

	/**
	 * cleaning up after the tests
	 * 
	 * @return all the names deleted
	 */
	private String deleteAll() {
		List<String> names = new ArrayList<String>();
		for (Visitor doc : store.getAll()) {
			String name = doc.getName();
			if (name != null) {
				names.add(name);
				//delete(doc);
			}
		}
		return "' deleted: " + new Gson().toJson(names) + "'";
	}

	@POST
	@Path("/all")
	@Produces({ "application/json" })
	@Consumes("application/json")
	public String doTestCrudPost(String string) {
		System.out.println("got POST req + " + string);
		return doTestCRUD();
	}

}