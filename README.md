# spring-boot-rest-api-couchbase-crud
Spring Boot REST APIs with CRUD for Couchbase. 

Each branch implements different features:

- ***basic_crud***: basic REST APIs with CRUD for Couchbase

- ***transactions_implementations***: REST APIs with transactions. In transactions Couchbase uses durability level majority by default. Here https://docs.couchbase.com/server/current/learn/data/durability.html it is explained how to configure the number of replicas in relation to the number of nodes. For example if you start a couchbase server in local and you have 1 node you must set a number of replicas equal to 0 otherwise it will raise an exception. The other solution could be to set the Durability level to NONE but the ACID guarantees won't be provided in transactions. Furthermore I am looking for next release of spring data couchbase in which will be solved this issue https://github.com/spring-projects/spring-data-couchbase/issues/1745 with the deleteAll method.

The APIs work fine but some tests may fail randomly if you run them altogheter. I am trying to solve the issue.

I have experienced some issue with some methods of the CouchbaseTemplate and also extending the CrudRepository so I have tried other ways to produce methods that work.

If you have any suggestions or thoughts feel free to contact me.

