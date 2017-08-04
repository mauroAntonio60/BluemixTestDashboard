# Compose for ElasticSearch Java test app
This application is designed to create index, add articles, search articles and check health of the cluster for Compose for ElasticSearch app based on the Dedicated Bluemix (public Bluemix is also considered).

1. Please follow the below steps to deploy the app.
2. Please test the app
3. Please reference the app in the Test Dashboard app.

## Prerequisites

You'll need [Git](https://git-scm.com/downloads), [Cloud Foundry CLI](https://github.com/cloudfoundry/cli#downloads), [Maven](https://maven.apache.org/download.cgi) and a Dedicated Bluemix - also you might want to test the environment with Public Bluemix: [Bluemix account](https://console.ng.bluemix.net/registration/).

This application is based on the github.com/IBM-Bluemix/GetStartedJava and https://github.com/searchly/searchly-java-sample

## 1. Clone the sample app

Now you're ready to start working with the app. Clone the repo and change the directory to where the sample app is located.
  ```bash
  git clone https://github.com/blumareks/BluemixTestDashboard
  cd BluemixTestDashboard/GetStartedJavaComposeElasticSearch
  ```

## 2. Create the necessary Bluemix App and Services
Login to the Bluemix console.
Create the Java Liberty App
Create the ComposeElasticSearch service and bind it with the Java Liberty App. 

## 3. Make the app locally using MAVEN

Use Maven to install dependencies and build the .war file.

  ```
  mvn clean install
  ```

## 4. Deploy to Bluemix using command line

To deploy to Bluemix using command line update manifest.yml file. 
The manifest.yml includes basic information about your app, such as the name, the location of your app, how much memory to allocate for each instance, and how many instances to create on startup. 

The manifest.yml is provided in the sample.

  ```
---
applications:
 - name: TestAppJavaComposeElasticSearch	
   random-route: true
   path: target/TestJavaComposeElasticSearch.war
   memory: 256M
   instances: 1
   name: test-java-composeelasticsearch
   host: test-java-composeelasticsearch

  ```

Choose your API endpoint
   ```
   cf api <API-endpoint>
   ```

Replace the *API-endpoint* in the command with an API endpoint from the following list of public Bluemix locations.
* https://api.ng.bluemix.net # US South
* https://api.eu-gb.bluemix.net # United Kingdom
* https://api.au-syd.bluemix.net # Sydney

Login to your Bluemix account
  ```
  cf login
  ```

Push your application to Bluemix.
  ```
  cf push
  ```

This can take around two minutes. If there is an error in the deployment process you can use the command `cf logs <Your-App-Name> --recent` to troubleshoot.

## 5. Access the test ComposeElasticSearch application
Enter the name of the application and add the API call for the test:
https://test-java-composeelasticsearch.dys0.mybluemix.net/TestJavaComposeElasticSearch/test/elasticsearch/all

You should be seeing something like this:
```javascript
{service: 'elasticsearch', operations: [
{type: 'health', response_time: 301, response_code: 200, desc: {"cluster_name":"bmix-dal-yp-f914b69f-df93-4c56-a614-46f6d74b9480","status":"green","timed_out":false,"number_of_nodes":3,"number_of_data_nodes":3,"active_primary_shards":3,"active_shards":9,"relocating_shards":0,"initializing_shards":0,"unassigned_shards":0,"delayed_unassigned_shards":0,"number_of_pending_tasks":0,"number_of_in_flight_fetch":0,"task_max_waiting_in_queue_millis":0,"active_shards_percent_as_number":100.0}},
{type: 'create-indexes-articles', response_time: 311, response_code: 200, desc: {"took":23,"errors":false,"items":[{"index":{"_index":"articles","_type":"article","_id":"1","_version":11,"result":"updated","_shards":{"total":3,"successful":3,"failed":0},"created":false,"status":200}},{"index":{"_index":"articles","_type":"article","_id":"2","_version":11,"result":"updated","_shards":{"total":3,"successful":3,"failed":0},"created":false,"status":200}}]}},
{type: 'search', response_time: 178, response_code: 200, desc: {'search string':'epic', 'search result': 'The Lord of the Rings is an epic high fantasy novel written by English philologist and University of Oxford professor J. R. R. Tolkien. The story began as a sequel to Tolkien's 1937 children's fantasy novel The Hobbit, but eventually developed into a much larger work. It was written in stages between 1937 and 1949, much of it during World War II.[1] It is the third best-selling novel ever written, with over 150 million copies sold'}}
], response_code: 200, desc:'operations implemented CISDI/CISDI'}
```