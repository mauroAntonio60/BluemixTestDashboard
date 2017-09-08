# MongoDB Java test app
This application is designed to undertake CRUD operations on Compose MongoDB app based on the Dedicated Bluemix (public Bluemix is also considered).

1. Please follow the below steps to deploy the app.
2. Please test the app
3. Please reference the app in the Test Dashboard app.

## Prerequisites

You'll need [Git](https://git-scm.com/downloads), [Cloud Foundry CLI](https://github.com/cloudfoundry/cli#downloads), [Maven](https://maven.apache.org/download.cgi) and a Dedicated Bluemix - also you might want to test the environment with Public Bluemix: [Bluemix account](https://console.ng.bluemix.net/registration/).

This application is based on the github.com/IBM-Bluemix/GetStartedJava.

## 1. Clone the sample app

Now you're ready to start working with the app. Clone the repo and change the directory to where the sample app is located.
  ```bash
  git clone https://github.com/blumareks/BluemixTestDashboard
  cd BluemixTestDashboard/GetStartedJavaMongoDb
  ```

## 2. Create the necessary Bluemix App and Services
Login to the Bluemix console.
Create the Java Liberty App
Create the Compose MongoDb service and bind it with the Java Liberty App. 

## 2. JVM System Properties for TLS/SSL connection to Compose MongoDB
You might notice that Compose MongoDB connectivity is SSL enabled.
Identify the Compose MongoDB connection URL and Certificate - find the link at the management console.

<p align="center">
  <kbd>
    <img src="docs/mongo_mngmnt.png" width="300" style="1px solid">
  </kbd>
</p>

Therefore our application will need to set several JVM system properties to ensure that the client is able to validate the TLS/SSL certificate presented by the server:

javax.net.ssl.trustStore: The path to a trust store containing the certificate of the signing authority
javax.net.ssl.trustStorePassword: The password to access this trust store

The trust store is typically created with the keytool command line program provided as part of the JDK. For example:

keytool -importcert -trustcacerts -file <path to certificate authority file>
            -keystore <path to trust store> -storepass <password>
            
The command for our system is the following:
create the mongoDBKey store: 
**keytool -importcert -trustcacerts -file ./mongodbcert.crt -keystore ./mongoKeyStore -storepass aftereight**
 
 Place the mongoKeyStore at this location: GetStartedJavaMongoDb/src/main/resources/mongoKeyStore
 
 The document after the mvn install is going to be stored at this location: 
 wasdev.sample.store.MongoDbVisitorStore at the createClient method
 
 - locally: /your path to the target: GetStartedJavaMongoDb/target/TestJavaMongo-1.0-SNAPSHOT/WEB-INF/classes/mongoKeyStore
 - on Bluemix (after cf push command) : /home/vcap/app/wlp/usr/servers/defaultServer/apps/myapp.war/WEB-INF/classes/mongoKeyStore
             
A typical application will also need to set several JVM system properties to ensure that the client presents an TLS/SSL certificate to the MongoDB server:

- javax.net.ssl.keyStore The path to a key store containing the client’s TLS/SSL certificates
- javax.net.ssl.keyStorePassword The password to access this key store

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
 - name: TestAppJavaMongoDB	
   random-route: true
   path: target/TestJavaMongo.war
   memory: 256M
   instances: 1
   name: test-java-mongodb
   host: test-java-mongodb
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

## 5. Access the test MongoDb application
Enter the name of the application and add the API call for the test:
https://test-java-mongodb.dys0.mybluemix.net/TestJavaMongo/test/mongo/all

You should be seeing something like this:
```javascript
{service: 'mongodb', operations: [{type: 'create', response_time: 30, response_code: 200, desc: {'visitor id': '594ddeee34a639002645674d'}},{type: 'read', response_time: 25, response_code: 200, desc: {'visitor id': '594ddeee34a639002645674d'}},{type: 'update', response_time: 49, response_code: 200, desc: {'visitor id': '594ddeee34a639002645674d'}},{type: 'delete', response_time: 28, response_code: 200, desc: { 'deleted visitor id': '594ddeee34a639002645674d'}}], response_code: 200, desc:'operations implemented CRUD/CRUD'}
```