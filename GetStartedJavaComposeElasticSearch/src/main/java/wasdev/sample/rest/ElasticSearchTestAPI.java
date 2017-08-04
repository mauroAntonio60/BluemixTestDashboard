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

import java.io.IOException;
import java.util.List;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.cluster.Health;
import io.searchbox.core.Bulk;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;
import wasdev.sample.model.Article;
import wasdev.sample.store.SearchlyJestStore;

@ApplicationPath("test")
@Path("/elasticsearch")
public class ElasticSearchTestAPI extends Application {
	

final static Logger logger = LoggerFactory.getLogger(ElasticSearchTestAPI.class);

	// Our database store
	SearchlyJestStore store = new SearchlyJestStore();

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
			return "{service: 'elasticsearch', operations:[], response_code: 404, desc:'Error: no connection to elasticsearch'}";
		}

		// Call elasticsearch Search...

		String response = "{service: 'elasticsearch', operations: [" + searchlyHealth() + "," + indexSampleArticles() + ","
				+ searchlySearch() // + "," + deleteIndexSampleArticles()  
				+ "]"
				+ ", response_code: 200, desc:'operations implemented CISDI/CISDI'}";

		return response;

	}

	private String searchlyHealth() {

		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		long startTime;
		long endTime;
		JestClient client = store.getClient();
		JestResult result = null;
		Health health = new Health.Builder().build();

		// timed operation
		startTime = System.currentTimeMillis();

		if (client != null) {
			try {
				result = client.execute(health);
				// prints output of Elasticsearch cluster health check
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		endTime = System.currentTimeMillis();
		// end of timed operation

		System.out.printf("\n\n<------ CLUSTER HEALTH ------>\n%s\n\n", result.getJsonObject());

		responseTime = (endTime - startTime);
		// rev 0.5 jsonObject instead of jsonPrimitive - string
		if (result != null) {
			responseDesc = result.getJsonObject().toString();
		} else {
			responseDesc = "{health: 'null'}";
		}
		String responseString = "{type: 'health', response_time: " + responseTime + ", response_code: " + responseCode
				+ ", desc: " + responseDesc + "}";
		System.out.println(responseString);
		return responseString;

		// shuts down the connection
		// client.shutdownClient();
	}

	/**
	 * Reading all the documents from the db - returning the code 200, response
	 * time in ms, and the contents of the db in desc
	 * 
	 * @return A test case result of READ/CRUD operation
	 */
	private String searchlySearch() {
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		long startTime;
		long endTime;

		String searchParameter = "epic";
		
		// timed operation
		startTime = System.currentTimeMillis();
		
		List<Article> results = searchArticles(searchParameter);
		
		// TODO: get the ObjectID
		// Visitor readVisitor = read(operationalVisitor.getName());
		endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);
		// rev 0.5 jsonObject instead of jsonPrimitive - string
		responseDesc = "{'search string':'"
				+ searchParameter + "', "
				+ "'search result': '"
				+ (results.size() > 0 ? (results.get(0)).getContent() : "")
				+ "'}";
		String responseString = "{type: 'search', response_time: " + responseTime + ", response_code: " + responseCode
				+ ", desc: " + responseDesc + "}";
		System.out.println(responseString);
		return responseString;
	}
	
	public String indexSampleArticles() {
		long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		long startTime;
		long endTime;

        Article article1 = new Article();
        article1.setId(1L);
        article1.setAuthor("Robert Anthony Salvatore");
        article1.setContent("Homeland follows the story of Drizzt from around the time and circumstances of his birth and his upbringing amongst the drow (dark elves). " +
                "The book takes the reader into Menzoberranzan, the drow home city. From here, the reader follows Drizzt on his quest to follow his principles in a land where such " +
                "feelings are threatened by all his family including his mother Matron Malice. In an essence, the book introduces Drizzt Do'Urden," +
                " one of Salvatore's more famous characters from the Icewind Dale Trilogy.");

        Article article2 = new Article();
        article2.setId(2L);
        article2.setAuthor("John Ronald Reuel Tolkien");
        article2.setContent("The Lord of the Rings is an epic high fantasy novel written by English philologist and University of Oxford professor J. R. R. Tolkien. " +
                "The story began as a sequel to Tolkien's 1937 children's fantasy novel The Hobbit, but eventually developed into a much larger work. " +
                "It was written in stages between 1937 and 1949, much of it during World War II.[1] It is the third best-selling novel ever written, with over 150 million copies sold");
        
        // timed operation
		startTime = System.currentTimeMillis();
        try {
        	
        	JestClient client = store.getClient();
            IndicesExists indicesExists = new IndicesExists.Builder("articles").build();

    		
            JestResult result = client.execute(indicesExists);

            if (!result.isSucceeded()) {
                // Create articles index
                CreateIndex createIndex = new CreateIndex.Builder("articles").build();
                client.execute(createIndex);
            }

            /**
             *  if you don't want to use bulk api use below code in a loop.
             *
             *  Index index = new Index.Builder(source).index("articles").type("article").build();
             *  jestClient.execute(index);
             *
             */

            Bulk bulk = new Bulk.Builder()
                    .addAction(new Index.Builder(article1).index("articles").type("article").build())
                    .addAction(new Index.Builder(article2).index("articles").type("article").build())
                    .build();
            
            bulk = new Bulk.Builder()
                    .addAction(new Index.Builder(article1).index("articles").type("article").build())
                    .addAction(new Index.Builder(article2).index("articles").type("article").build())
                    .build();

            result = client.execute(bulk);
            responseDesc = result.getJsonString();
            //System.out.println(result.getJsonString());

        } catch (IOException e) {
            logger.error("Indexing error", e);
        } catch (Exception e) {
            logger.error("Indexing error", e);
        }
        
        endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);
		// rev 0.5 jsonObject instead of jsonPrimitive - string
		
		String responseString = "{type: 'create-indexes-articles', response_time: " + responseTime + ", response_code: " + responseCode
				+ ", desc: " + responseDesc + "}";
		System.out.println(responseString);
		return responseString;

    }

    public List<Article> searchArticles(String param) {
        try {
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.queryString(param));

            Search search = new Search.Builder(searchSourceBuilder.toString())
                    .addIndex("articles")
                    .addType("article")
                    .build();
            JestClient client = store.getClient();
            JestResult result = client.execute(search);
            return result.getSourceAsObjectList(Article.class);

        } catch (IOException e) {
            logger.error("Search error", e);
        } catch (Exception e) {
            logger.error("Search error", e);
        }
        return null;
    }
    
    public String deleteIndexSampleArticles() {
    	long responseTime = 0;
		String responseCode = "200";
		String responseDesc = "''";
		long startTime;
		long endTime;

        // timed operation
		startTime = System.currentTimeMillis();
        try {
            // Delete articles index if it is exists
        	JestClient client = store.getClient();
        	DeleteIndex deleteIndex = new DeleteIndex.Builder("articles").build();
        	JestResult result = client.execute(deleteIndex);
        	
        	responseDesc = result.getJsonString();
            //System.out.println(result.getJsonString());
        	
        } catch (Exception e) {
            logger.error("Indexing error", e);
        }
        endTime = System.currentTimeMillis();
		// end of timed operation

		responseTime = (endTime - startTime);
		// rev 0.5 jsonObject instead of jsonPrimitive - string
		
		String responseString = "{type: 'delete-indexes', response_time: " + responseTime + ", response_code: " + responseCode
				+ ", desc: " + responseDesc + "}";
		System.out.println(responseString);
		return responseString;

    }
}
