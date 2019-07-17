# BluemixTestDashboard
checking the response times of the IBM Cloud basic services - if you are using it please give me a star.

## TestDashboard
This service enables you to test the apps deployed on the bluemix. They ping these applications over the Restful API call. In return the application respond with the following JSON:
```
{service: 'mongodb', operations: [
{type: 'create', response_time: 38, response_code: 200, desc: {'visitor id': '5955a851ccc001002d0c0552'}},
{type: 'read', response_time: 22, response_code: 200, desc: {'visitor id': '5955a851ccc001002d0c0552'}},
{type: 'update', response_time: 32, response_code: 200, desc: {'visitor id': '5955a851ccc001002d0c0552'}},
{type: 'delete', response_time: 28, response_code: 200, desc: { 'deleted visitor id': '5955a851ccc001002d0c0552'}}
], response_code: 200, desc:'operations implemented CRUD/CRUD'}
```

[Use this link to view the test dashboard repo](TestDashboard)

## JavaCloudant test application
This application tests connectivity to Cloudant NoSQLDb from Java platform.
[Use this link to view the test java Cloudant NoSQLDb repo](get-started-java-master)


## JavaMongoDB test application
This test application checks connectivity to MongoDB from Java platform.
The provided java sample has been updated to work on IBM Cloud with Liberty and Mongodb services.
Try it at https://mauro-test-java-mongodb.eu-de.mybluemix.net/TestJavaMongo/test/mongo/all
[goto instructions https://developer.ibm.com/tutorials/check-response-times-for-crud-services-with-mongodb-and-java-liberty/ ]
[Use this link to view the test java MongoDB repo](GetStartedJavaMongoDb)

## Java Message Hub test application
This test application checks producing and consuming of the messages with IBM Message Hub.
[Use this link to view the test app for Message Hub](GetStartedJavaMessageHub)

## Java Compose for ElasticSearch test application
This test application checks indexing and searching thru the documents with Compose for ElasticSearch.
[Use this link to view the test app for Compose for ElasticSearch](GetStartedJavaComposeElasticSearch)

## Java Compose for Redis test application
This test application checks "Create Read Push Pop Sets" operations for Compose for Redis with Java on Bluemix.
[Use this link to view the test app for Compose for Redis](GetStartedJavaRedis)

## Java Compose for Postgresql test application
This test application checks CRUD operations for Compose for PostgreSQL with Java on Bluemix.
[Use this link to view the test app for Compose for PostgreSQL](GetStartedJavaPostgresql)


## Java Object Storage test application
This test application checks CRD operations for Object Storage with Java on Bluemix.
[Use this link to view the test app for Object Storage](GetStartedJavaObjectStorage)



## Java Compose for MySQL test application
This test application checks CRUD operations for Compose for MySql with Java on Bluemix.
[Use this link to view the test app for MySql](GetStartedJavaMySQL)
