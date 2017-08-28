# Redis Java test app
This application is designed to undertake several typical operations on Compose Redis app based on the Dedicated Bluemix (public Bluemix is also considered).

1. Please follow the below steps to deploy the app.
2. Please test the app
3. Please reference the app in the Test Dashboard app.

## Prerequisites

You'll need [Git](https://git-scm.com/downloads), [Cloud Foundry CLI](https://github.com/cloudfoundry/cli#downloads), [Maven](https://maven.apache.org/download.cgi) and a Dedicated Bluemix - also you might want to test the environment with Public Bluemix: [Bluemix account](https://console.ng.bluemix.net/registration/).

This application is based on the github.com/IBM-Bluemix/GetStartedJava.
I used also information from http://www.baeldung.com/jedis-java-redis-client-library

## 1. Clone the sample app

Now you're ready to start working with the app. Clone the repo and change the directory to where the sample app is located.
  ```bash
  git clone https://github.com/blumareks/BluemixTestDashboard
  cd BluemixTestDashboard/GetStartedJavaRedis
  ```

## 2. Create the necessary Bluemix App and Services
Login to the Bluemix console.
Create the Java Liberty App
Create the Compose Redis service and bind it with the Java Liberty App. 

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
 - name: TestAppJavaRedis	
   random-route: true
   path: target/TestJavaRedis.war
   memory: 256M
   instances: 1
   name: test-java-redis
   host: test-java-redis
  ```

Choose your API endpoint
   ```
   cf api <API-endpoint>
   ```

Replace the *API-endpoint* in the command with an API endpoint from the following list of public Bluemix locations.
* https://api.ng.bluemix.net # US South
* https://api.eu-gb.bluemix.net # United Kingdom
* https://api.au-syd.bluemix.net # Sydney
* https://api.eu-de.bluemix.net # Germany

Login to your Bluemix account
  ```
  cf login
  ```

Push your application to Bluemix.
  ```
  cf push
  ```

This can take around two minutes. If there is an error in the deployment process you can use the command `cf logs <Your-App-Name> --recent` to troubleshoot.

## 5. Access the test Redis application
Enter the name of the application and add the API call for the test:
https://test-java-Redis.dys0.mybluemix.net/test/redis/all

You should be seeing something like this:
```javascript
{service: 'redis', operations: [
{type: 'create', response_time: 16, response_code: 200, desc: {'visitor': '1503895681168,test case: 1503895681168'}},
{type: 'read', response_time: 9, response_code: 200, desc: {'visitor id': '1503895681168'}},
{type: 'list push', response_time: 18, response_code: 200, desc: {'visitor pushed': '1503895681168,test case2: 1503895681272'}},
{type: 'list pop', response_time: 34, response_code: 200, desc: { 'list pop visitor id': '1503895681168'}},
{type: 'test sets', response_time: 344, response_code: 200, desc: {'visitor and setTests ': '1503895681376,test case: 1503895681376 size set 4 + it is there true'}}
], response_code: 200, desc:'operations implemented CRPPS/CRPPS HTPP-S'}
```
