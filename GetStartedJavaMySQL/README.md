# MySQL Java test app
This application is designed to undertake CRUD operations on Compose MySQL app based on the Dedicated Bluemix (public Bluemix is also considered).

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
  cd BluemixTestDashboard/GetStartedJavaMySQL
  ```

## 2. Create the necessary Bluemix App and Services
Login to the Bluemix console.
Create the Java Liberty App
Create the Compose MySQL service and bind it with the Java Liberty App. 

## 2. JVM System Properties for TLS/SSL connection to Compose MySQL
Identify the Compose MySQL connection URL and Certificate - find the link at the management console.

Therefore our application will need to set several JVM system properties to ensure that the client is able to validate the TLS/SSL certificate presented by the server.

Copy the certificate between lines: ```-----BEGIN CERTIFICATE-----``` and ```-----END CERTIFICATE-----``` into the file mysqlcert.crt (I usually use ```cat > mysqlcert.crt``` and control-C to exit editing).

javax.net.ssl.trustStore: The path to a trust store containing the certificate of the signing authority
javax.net.ssl.trustStorePassword: The password to access this trust store

The trust store is typically created with the keytool command line program provided as part of the JDK. For example:

keytool -importcert -trustcacerts -file <path to certificate authority file>
            -keystore <path to trust store> -storepass <password>
            
The command for our system is the following:
create the MySQLKey store: 
**keytool -importcert -trustcacerts -file ./mysqlcert.crt -keystore ./mysqlKeyStore -storepass aftereight**
 
 Place the mysqlKeyStore at this location: GetStartedJavaMySQL/src/main/resources/mysqlKeyStore
 
 The document after the mvn install is going to be stored at this location: 
 wasdev.sample.store.MySQLVisitorStore at the createClient method
 
 - locally: /your path to the target: GetStartedJavaMySQL/target/TestJavaMySQL-1.0-SNAPSHOT/WEB-INF/classes/mysqlKeyStore
 - on Bluemix (after cf push command) : /home/vcap/app/wlp/usr/servers/defaultServer/apps/myapp.war/WEB-INF/classes/mysqlKeyStore
             
A typical application will also need to set several JVM system properties to ensure that the client presents an TLS/SSL certificate to the MySQL server:

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
 - name: TestAppJavaMySQL	
   random-route: true
   path: target/TestJavaMongo.war
   memory: 256M
   instances: 1
   name: test-java-MySQL
   host: test-java-MySQL
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

## 5. Access the test MySQL application
Enter the name of the application and add the API call for the test:
```https://<yourappname>.mybluemix.net/TestJavaMongo/test/mysql/all```

You should be seeing something like this:
```javascript
{service: 'mysql', operations: [
{type: 'create', response_time: 40, response_code: 200, desc: {'visitor': '1509049042749,test case: 1509049042749'}},
{type: 'read', response_time: 22, response_code: 200, desc: {'visitor id': '92'}},
{type: 'update', response_time: 108, response_code: 200, desc: {'visitor': '92,test case2: 1509049042830'}},
{type: 'delete', response_time: 11, response_code: 200, desc: { 'deleted visitor id': '92'}}
], response_code: 200, desc:'operations implemented CRUD/CRUD'}
```
