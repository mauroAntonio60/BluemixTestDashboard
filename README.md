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
This test application tests connectivity to Cloudant NoSQLDb from Java platform.
[Use this link to view the test java Cloudant NoSQLDb repo](get-started-java-master)


## JavaMongoDB test application
This test application tests connectivity to MongoDB from Java platform.
[Use this link to view the test java MongoDB repo](GetStartedJavaMongoDb)



