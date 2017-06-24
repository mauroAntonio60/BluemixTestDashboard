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
@Path("/mongo")
public class MongoTestAPI extends Application {

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
			return "{service: 'mongodb', operations:[], response_code: 404, desc:'Error: no connection to mongodb'}";
		}

		// Call mongodb Read

		String response = "{service: 'mongodb', operations: [" + mongodbCreate() + "," + mongodbRead() + ","
				+ mongodbUpdate() + "," + mongodbDelete() + "]"
				+ ", response_code: 200, desc:'operations implemented CRUD/CRUD'}";

		return response;

	}

	/**
	 * Test CRUD for mongodb. REST API GetStartedJava/test/mongodb/read : <code>
	 * GET http://localhost:9080/GetStartedJava/test/mongodb/read
	 * </code> 10 end points
	 * 
	 * Java (Liberty) - Test mongodb (GET) - Test Mongo (GET) - Test Postgres
	 * (GET) - Test MessageHub (GET) - Test Redis (GET)
	 * 
	 * Example responses:
	 * 
	 * Response: <code>
	 * {
	service: 'mongodb',
	operations: [{
		type: 'read',
		response_time: 120,
		response_code: 200,
		desc: 'some error desc from the service, or any general description about the operation'
	}, {
		type: 'insert',
		response_time: 120,
		response_code: 200,
		desc: 'some error desc from the service, or any general description about the operation'
	}, {
		type: 'update',
		response_time: 120,
		response_code: 200,
		desc: 'some error desc from the service, or any general description about the operation'
	}, {
		type: 'delete',
		response_time: 120,
		response_code: 200,
		desc: 'some error desc from the service, or any general description about the operation'
	}]
	}
	
	{
	service: 'message-hub',
	operations: [{
	type: 'create-topic',
	response_time: 120,
	response_code: 200,
	desc: 'some error desc from the service, or any general description about the operation'
	}, {
	type: 'produce',
	response_time: 120,
	response_code: 200,
	desc: 'some error desc from the service, or any general description about the operation'
	}, {
	type: 'delete-topic',
	response_time: 120,
	response_code: 200,
	desc: 'some error desc from the service, or any general description about the operation'
	}]
	}
	 * </code>
	 * 
	 * @return A test case result of all CRUD operations
	 */
	@GET
	@Path("/read")
	@Produces({ "application/json" })
	public String doTestR() {

		if (store == null) {
			return "{service: 'mongodb', operations:[], response_code: 404, desc:'Error: no connection to mongodb'}";
		}

		// Call mongodb Read
		// TODO store id of the record to read

		String response = "{service: 'mongodb', operations: [" + mongodbRead() + "]"
				+ ", response_code: 200, desc:'operation R/CRUD'}";

		return response;
	}

	/**
	 * Test CRUD for mongodb. REST API GetStartedJava/test/mongodb/read : <code>
	 * GET http://localhost:9080/GetStartedJava/test/mongodb/read
	 * </code> 10 end points
	 * 
	 * Java (Liberty) - Test mongodb (GET) - Test Mongo (GET) - Test Postgres
	 * (GET) - Test MessageHub (GET) - Test Redis (GET)
	 * 
	 * 
	 * @return A test case result of all CRUD operations
	 */
	@GET
	@Path("/create")
	@Produces({ "application/json" })
	public String doTestC() {

		if (store == null) {
			return "{service: 'mongodb', operations:[], response_code: 404, desc:'Error: no connection to mongodb'}";
		}

		// Call mongodb Create
		// TODO store id/name of the record to create

		String response = "{service: 'mongodb', operations: [" + mongodbCreate() + "]"
				+ ", response_code: 200, desc:'operation C/CRUD'}";
		return response;
	}

	/**
	 * Test CRUD for mongodb. REST API GetStartedJava/test/mongodb/delete :
	 * <code>
	 * GET http://localhost:9080/GetStartedJava/test/mongodb/delete
	 * </code> 10 end points
	 * 
	 * Java (Liberty) - Test mongodb (GET) - Test Mongo (GET) - Test Postgres
	 * (GET) - Test MessageHub (GET) - Test Redis (GET)
	 * 
	 * Example responses:
	 * 
	 * 
	 * @return A test case result of all CRUD operations
	 */
	@GET
	@Path("/delete")
	@Produces({ "application/json" })
	public String doTestD() {

		if (store == null) {
			return "{service: 'mongodb', operations:[], response_code: 404, desc:'Error: no connection to mongodb'}";
		}

		// Call mongodb Delete
		// TODO store id of the record to delete

		String response = "{service: 'mongodb', operations: [" + mongodbDelete() + "]"
				+ ", response_code: 200, desc:'operation D/CRUD'}";
		// cleaning the db afterwards
		// System.out.println(deleteAll());
		return response;
	}

	/**
	 * Test CRUD for mongodb. REST API GetStartedJava/test/mongodb/update :
	 * <code>
	 * GET http://localhost:9080/GetStartedJava/test/mongodb/update
	 * </code> 10 end points
	 * 
	 * Java (Liberty) - Test mongodb (GET) - Test Mongo (GET) - Test Postgres
	 * (GET) - Test MessageHub (GET) - Test Redis (GET)
	 * 
	 * Example responses:
	 * 
	 * 
	 * 
	 * @return A test case result of all CRUD operations
	 */
	@GET
	@Path("/update")
	@Produces({ "application/json" })
	public String doTestU() {

		if (store == null) {
			return "{service: 'mongodb', operations:[], response_code: 404, desc:'Error: no connection to mongodb'}";
		}

		// Call mongodb Update
		// TODO store id of the record to update

		String response = "{service: 'mongodb', operations: [" + mongodbUpdate() + "]"
				+ ", response_code: 200, desc:'operation D/CRUD'}";
		return response;
	}

	/**
	 * Reading all the documents from the db - returning the code 200, response
	 * time in ms, and the contents of the db in desc
	 * 
	 * @return A test case result of READ/CRUD operation
	 */
	private String mongodbRead() {
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		long startTime;
		long endTime;

		// timed operation
		startTime = System.currentTimeMillis();
		// TODO: get the ObjectID
		Visitor readVisitor = read(operationalVisitor.getName());
		endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);
		// rev 0.5 jsonObject instead of jsonPrimitive - string
		responseDesc = "{'visitor id': '" + readVisitor.get_id() + "'}";
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
	private String mongodbCreate() {
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
		responseDesc = "{'visitor id': '" + operationalVisitor.get_id() + "'}";
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
	private String mongodbUpdate() {
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		Visitor visitorUpdated = operationalVisitor;

		long startTime;
		long endTime;

		// timed operation
		startTime = System.currentTimeMillis();
		operationalVisitor = update(visitorUpdated);
		endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);
		// rev 0.5 jsonObject instead of jsonPrimitive - string
		responseDesc = "{'visitor id': '" + operationalVisitor.get_id() + "'}";
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
	private String mongodbDelete() {
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		Visitor visitorToBeDeleted = operationalVisitor;

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
		store.delete(visitorToBeDeleted.getName());
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

	/**
	 * Creates a new Visitor.
	 * 
	 * REST API example: <code>
	 * POST http://localhost:9080/GetStartedJava/api/visitors
	 * <code>
	 * POST Body:
	 * <code>
	 * {
	 *   "name":"Bob"
	 * }
	 * </code> Response: <code>
	 * {
	 *   "id":"123",
	 *   "name":"Bob"
	 * }
	 * </code>
	 * 
	 * @param visitor
	 *            The new Visitor to create.
	 * @return The Visitor after it has been stored. This will include a unique
	 *         ID for the Visitor.
	 */
	@POST
	@Path("/all")
	@Produces({ "application/json" })
	@Consumes("application/json")
	public String doTestCrudPost(String string) {
		System.out.println("got post req + " + string);
		return doTestCRUD();
	}

	public String newToDo(Visitor visitor) {
		if (store == null) {
			return String.format("Hello %s!", visitor.getName());
		}
		store.persist(visitor);
		return String.format("Hello %s! I've added you to the database.", visitor.getName());

	}

}