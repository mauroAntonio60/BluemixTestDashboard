# Compose for PostgreSQL Java test app
This application is designed to undertake CRUD operations on Compose for PostgreSQL app based on the Dedicated Bluemix (public Bluemix is also considered).

1. Please follow the below steps to deploy the app.
2. Please test the app
3. Please reference the app in the Test Dashboard app.

## Prerequisites

You'll need [Git](https://git-scm.com/downloads), [Cloud Foundry CLI](https://github.com/cloudfoundry/cli#downloads), [Maven](https://maven.apache.org/download.cgi) and a Dedicated Bluemix - also you might want to test the environment with Public Bluemix: [Bluemix account](https://console.ng.bluemix.net/registration/).

This application is based on the github.com/IBM-Bluemix/GetStartedJava.
In addition for SQL and Java drivers I used: https://www.tutorialspoint.com/postgresql/postgresql_java.htm

## 1. Clone the sample app

Now you're ready to start working with the app. Clone the repo and change the directory to where the sample app is located.
  ```bash
  git clone https://github.com/blumareks/BluemixTestDashboard
  cd BluemixTestDashboard/GetStartedJavaPostgresql
  ```

## 2. Create the necessary Bluemix App and Services
Login to the Bluemix console.
Create the Java Liberty App
Create the Compose for Postgresql service and bind it with the Java Liberty App. 

## 3. Use Maven to build the app
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
 - name: TestAppJavaPostgresql	
   random-route: true
   path: target/TestJavaPostgresql.war
   memory: 256M
   instances: 1
   name: test-java-postgresql
   host: test-java-postgresql
  ```

Choose your API endpoint
   ```
   cf api <API-endpoint>
   ```

Replace the *API-endpoint* in the command with an API endpoint from the following list of public Bluemix locations.
* https://api.ng.bluemix.net # US South
* https://api.eu-gb.bluemix.net # United Kingdom
* https://api.au-syd.bluemix.net # Sydney
* https://api.eu-de.mybluemix.net # Germany

Login to your Bluemix account
  ```
  cf login
  ```

Push your application to Bluemix.
  ```
  cf push
  ```

This can take around two minutes. If there is an error in the deployment process you can use the command `cf logs <Your-App-Name> --recent` to troubleshoot.

## 5. Access the test PostgreSQL application
Enter the name of the application and add the API call for the test:
https://test-java-postgresql.dys0.mybluemix.net/test/postgresql/all

You should be seeing something like this:
```javascript
{service: 'postgresql', operations: [{type: 'create', response_time: 242, response_code: 200, desc: {'visitor': '1503872804808,test case: 1503872804808'}},{type: 'read', response_time: 121, response_code: 200, desc: {'visitor id': '23'}},{type: 'update', response_time: 244, response_code: 200, desc: {'visitor': '23,test case2: 1503872805176'}},{type: 'delete', response_time: 121, response_code: 200, desc: { 'deleted visitor id': '23'}}], response_code: 200, desc:'operations implemented CRUD/CRUD'}
```
