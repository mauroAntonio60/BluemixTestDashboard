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
@Path("/cloudant")
public class CloudantTestAPI extends Application {

	// Our database store
	VisitorStore store = VisitorStoreFactory.getInstance();
	Visitor operationalVisitor;

	/**
	 * Test CRUD for Cloudant. REST API GetStartedJava/test/cloudant/all :
	 * <code>
	 * GET http://localhost:9080/GetStartedJava/test/cloudant/all
	 * </code> 10 end points
	 * 
	 * Response: <code>
	 * 
	 * </code>
	 * 
	 * @return A test case result of all CRUD operations
	 */
	@GET
	@Path("/all")
	@Produces({ "application/json" })
	public String doTestCRUD() {

		if (store == null) {
			return "{service: 'cloudant', operations:[], response_code: 404, desc:'Error: no connection to Cloudant'}";
		}

		// Call Cloudant Read

		String response = "{service: 'cloudant', operations: [" + cloudantCreate() + "," + cloudantRead() + ","
				+ cloudantUpdate() + "," + cloudantDelete() + "]"
				+ ", response_code: 200, desc:'operations implemented CRUD/CRUD'}";
		// cleaning the db afterwards
		// System.out.println(deleteAll());
		return response;

	}

	/**
	 * 
	 * 
	 * @return A test case result of read operations
	 */
	@GET
	@Path("/read")
	@Produces({ "application/json" })
	public String doTestR() {

		if (store == null) {
			return "{service: 'cloudant', operations:[], response_code: 404, desc:'Error: no connection to Cloudant'}";
		}

		// Call Cloudant Read
		// TODO store id of the record to read

		String response = "{service: 'cloudant', operations: [" + cloudantRead() + "]"
				+ ", response_code: 200, desc:'operation R/CRUD'}";
		// cleaning the db afterwards
		// System.out.println(deleteAll());
		return response;
	}

	/**
	 * 
	 * @return A test case result of create operations
	 */
	@GET
	@Path("/create")
	@Produces({ "application/json" })
	public String doTestC() {

		if (store == null) {
			return "{service: 'cloudant', operations:[], response_code: 404, desc:'Error: no connection to Cloudant'}";
		}

		// Call Cloudant Create
		// TODO store id/name of the record to create

		String response = "{service: 'cloudant', operations: [" + cloudantCreate() + "]"
				+ ", response_code: 200, desc:'operation C/CRUD'}";
		// cleaning the db afterwards
		// System.out.println(deleteAll());
		return response;
	}

	/**
	 * *
	 * 
	 * @return A test case result of delete operations
	 */
	@GET
	@Path("/delete")
	@Produces({ "application/json" })
	public String doTestD() {

		if (store == null) {
			return "{service: 'cloudant', operations:[], response_code: 404, desc:'Error: no connection to Cloudant'}";
		}

		// Call Cloudant Delete
		// TODO store id of the record to delete

		String response = "{service: 'cloudant', operations: [" + cloudantDelete() + "]"
				+ ", response_code: 200, desc:'operation D/CRUD'}";
		// cleaning the db afterwards
		// System.out.println(deleteAll());
		return response;
	}

	/**
	 * 
	 * 
	 * @return A test case result of update operations
	 */
	@GET
	@Path("/update")
	@Produces({ "application/json" })
	public String doTestU() {

		if (store == null) {
			return "{service: 'cloudant', operations:[], response_code: 404, desc:'Error: no connection to Cloudant'}";
		}

		// Call Cloudant Update
		// TODO store id of the record to update

		String response = "{service: 'cloudant', operations: [" + cloudantUpdate() + "]"
				+ ", response_code: 200, desc:'operation D/CRUD'}";
		// cleaning the db afterwards
		// System.out.println(deleteAll());
		return response;
	}

	/**
	 * Reading all the documents from the db - returning the code 200, response
	 * time in ms, and the contents of the db in desc
	 * 
	 * @return A test case result of READ/CRUD operation
	 */
	private String cloudantRead() {
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		long startTime;
		long endTime;

		// timed operation
		startTime = System.currentTimeMillis();
		Visitor readVisitor = read(operationalVisitor.get_id());
		endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);
		// rev 0.5 jsonObject instead of jsonPrimitive - string
		responseDesc = "{'visitor id': '" + readVisitor.get_id() + "', 'record ver': '" + readVisitor.get_rev() + "'}";
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
	private String cloudantCreate() {
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		Visitor visitor = new Visitor();
		visitor.setName("test case: " + System.currentTimeMillis());

		long startTime;
		long endTime;

		// timed operation
		startTime = System.currentTimeMillis();
		operationalVisitor = write(visitor);
		endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);
		// rev 0.5 jsonObject instead of jsonPrimitive - string
		responseDesc = "{'visitor id': '" + operationalVisitor.get_id() + "', 'record ver': '"
				+ operationalVisitor.get_rev() + "'}";
		String responseString = "{type: 'create', response_time: " + responseTime + ", response_code: " + responseCode
				+ ", desc: " + responseDesc + "}";
		System.out.println(responseString);
		return responseString;
	}

	/**
	 * updating in a document to the db - returning the code 200, response time
	 * in ms, and the record id in the response
	 * 
	 * @return A test case result of Update/CRUD operation
	 */
	private String cloudantUpdate() {
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		Visitor visitorUpdated = operationalVisitor;
		visitorUpdated.setName("test case: " + System.currentTimeMillis());

		long startTime;
		long endTime;

		// timed operation
		startTime = System.currentTimeMillis();
		operationalVisitor = update(visitorUpdated);
		endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);
		// rev 0.5 jsonObject instead of jsonPrimitive - string
		responseDesc = "{'visitor id': '" + operationalVisitor.get_id() + "', 'record ver': '"
				+ operationalVisitor.get_rev() + "'}";
		String responseString = "{type: 'update', response_time: " + responseTime + ", response_code: " + responseCode
				+ ", desc: " + responseDesc + "}";
		System.out.println(responseString);
		return responseString;
	}

	/**
	 * Deleting a document in the db - returning the code 200, response time in
	 * ms, and the record of a deleted id in the response
	 * 
	 * @return A test case result of Delete/CRUD operation
	 */
	private String cloudantDelete() {
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		Visitor visitorToBeDeleted = operationalVisitor;
		visitorToBeDeleted.setName("test case: " + System.currentTimeMillis());

		long startTime;
		long endTime;

		// timed operation
		startTime = System.currentTimeMillis();
		delete(visitorToBeDeleted);
		endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);

		// rev 0.5 jsonObject instead of jsonPrimitive - string
		responseDesc = "{ 'deleted visitor id': '" + operationalVisitor.get_id() + "'}";
		operationalVisitor = null; // we have deleted the self.visitor
		String responseString = "{type: 'delete', response_time: " + responseTime + ", response_code: " + responseCode
				+ ", desc: " + responseDesc + "}";
		System.out.println(responseString);
		return responseString;
	}

	/**
	 * deleting the test visitor in the store
	 * 
	 * @param visitorToBeDeleted
	 */
	private void delete(Visitor visitorToBeDeleted) {
		store.delete(visitorToBeDeleted.get_id());
	}

	/**
	 * update a visitor
	 * 
	 * @param an
	 *            updated visitor
	 * @return the persisted visitor with updated information: _id and _rev
	 */
	private Visitor update(Visitor visitor) {
		visitor = store.update(visitor.get_id(), visitor);
		return visitor;

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
				delete(doc);
			}
		}
		return "' deleted: " + new Gson().toJson(names) + "'";
	}

	/*    ID for the Visitor.
	 */
	@POST
	@Path("/all")
	@Produces({ "application/json" })
	@Consumes("application/json")
	public String doTestCrudPost(String string) {
		System.out.println("got post req + " + string);
		return doTestCRUD();
	}

}