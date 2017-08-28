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
@Path("/redis")
public class RedisTestAPI extends Application {

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
			return "{service: 'redis', operations:[], response_code: 404, desc:'Error: no connection to redis'}";
		}

		// Call redis Read
		System.out.println("entering test...");
		String response = "{service: 'redis', operations: [" + redisCreate() 
				+ "," 
				+ redisRead() 
				+ ","
				+ redisListPush() + "," 
				+ redisListPop() + "," 
				+ redisTestSets() 
				+ "]"
				+ ", response_code: 200, desc:'operations implemented CRPPS/CRPPS HTPP-S'}";
		System.out.println("ending test...");
		return response;

	}

	private String redisTestSets() {
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
		operationalVisitor = testSets(visitor);
		endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);
		responseDesc = "{'visitor and setTests ': '" + operationalVisitor.get_id() + "," + operationalVisitor.getName() + "'}";
		String responseString = "{type: 'test sets', response_time: " + responseTime + ", response_code: " + responseCode
				+ ", desc: " + responseDesc + "}";
		System.out.println(responseString);
		return responseString;
	}

	private Visitor testSets(Visitor visitor) {
		visitor = store.testSets(visitor);
		return visitor;
	}

	/**
	 * Test CRUD for redis. REST API GetStartedJava/test/redis/read : <code>
	 * GET http://localhost:9080/GetStartedJava/test/redis/read
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
			return "{service: 'redis', operations:[], response_code: 404, desc:'Error: no connection to redis'}";
		}

		// Call redis Read
		// TODO store id of the record to read

		String response = "{service: 'redis', operations: [" + redisRead() + "]"
				+ ", response_code: 200, desc:'operation R/CRUD'}";

		return response;
	}


	/**
	 * Reading all the documents from the db - returning the code 200, response
	 * time in ms, and the contents of the db in desc
	 * 
	 * @return A test case result of READ/CRUD operation
	 */
	private String redisRead() {
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
	private String redisCreate() {
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
	 * updating in a document to the db - returning the code 200, response time
	 * in ms, and the record id in the response
	 * 
	 * @return A test case result of Update/CRUD operation
	 */
	private String redisListPush() {
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		Visitor visitorUpdated = operationalVisitor;
		visitorUpdated.setName("test case2: " + System.currentTimeMillis());
		
		long startTime;
		long endTime;

		// timed operation
		startTime = System.currentTimeMillis();
		operationalVisitor = listPush(visitorUpdated);
		endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);
		responseDesc = "{'visitor pushed': '" + operationalVisitor.get_id() + "," + operationalVisitor.getName() + "'}";
		String responseString = "{type: 'list push', response_time: " + responseTime + ", response_code: " + responseCode
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
	private String redisListPop() {
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		Visitor vi = new Visitor();
		long startTime;
		long endTime;

		// timed operation
		startTime = System.currentTimeMillis();
		vi = listPop(operationalVisitor);
		endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);

		responseDesc = "{ 'list pop visitor id': '" + operationalVisitor.get_id() + "'}";
		String responseString = "{type: 'list pop', response_time: " + responseTime + ", response_code: " + responseCode
				+ ", desc: " + responseDesc + "}";
		System.out.println(responseString);
		return responseString;
	}

	/**
	 * Redis list pop
	 * @param visitorToBePopped
	 * @return
	 */
	private Visitor listPop(Visitor visitorToBePopped) {
		Visitor vi = store.pop(visitorToBePopped.get_id());
		return vi;
	}

	/**
	 * update a visitor
	 * 
	 * @param an
	 *            updated visitor
	 * @return the persisted visitor with updated information: _id and _rev
	 */
	private Visitor listPush(Visitor visitor) {
		visitor = store.push(visitor);
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