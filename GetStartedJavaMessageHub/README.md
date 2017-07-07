# Message Hub (Kafka as a Service on Bluemix) Java test app
This application is designed to undertake Create Topic, Produce Message, Consume Message operations on IBM Message-Hub app based on the Dedicated Bluemix (public Bluemix is also considered).

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
  cd BluemixTestDashboard/GetStartedJavaMessageHub
  ```

## 2. Create the necessary Bluemix App and Services
Login to the Bluemix console.
Create the Java Liberty App
Create the IBM Message Hub service and bind it with the Java Liberty App. 

## 2. JVM System Properties for TLS/SSL connection to Compose MongoDB

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
 - name: TestAppJavaMessageHub
   random-route: true
   path: target/TestJavaMessageHub.war
   memory: 256M
   instances: 1
   name: test-java-messagehub
   host: test-java-messagehub
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

## 5. Access the test MessageHub application
Enter the name of the application and add the API call for the test:
https://test-java-messagehub.dys0.mybluemix.net/TestJavaMessageHub/test/messagehub/all

You should be seeing something like this:
```javascript
{service: 'message-hub', operations: [
{type: 'create topic', response_time: 121, response_code: 200, desc: {'Admin REST response': 'no response - the topic has been already created'}},
{type: 'produce message', response_time: 1, response_code: 200, desc: {'Message created': 'Message produced, offset: 523'}},
{type: 'consume message', response_time: 2064, response_code: 200, desc: {'Message consumed': 'Message consumed: ConsumerRecord(topic = test-java-messagehub-topic, partition = 0, offset = 523, CreateTime = 1499458409907, serialized key size = 3, serialized value size = 25, headers = RecordHeaders(headers = [], isReadOnly = false), key = key, value = This is a test message #2)'}}
], response_code: 200, desc:'operations implemented CrPCo/CrPCoD'}
```
