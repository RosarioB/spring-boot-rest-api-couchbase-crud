package com.rosariob.crud.couchbase.repository;


import com.couchbase.client.core.msg.kv.DurabilityLevel;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryScanConsistency;
import com.couchbase.client.java.transactions.TransactionGetResult;
import com.couchbase.client.java.transactions.Transactions;
import com.couchbase.client.java.transactions.config.TransactionOptions;
import com.rosariob.crud.couchbase.component.CustomerMapper;
import com.rosariob.crud.couchbase.config.ApplicationProperties;
import com.rosariob.crud.couchbase.entity.Customer;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Repository
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public class CustomerRepository implements GenericRepository<Customer> {
    private CouchbaseTemplate couchbaseTemplate;
    private ApplicationProperties applicationProperties;
    private CustomerMapper customerMapper;
    private String bucketName;
    private String scopeName;
    private String collectionName;
    private String keySpace;
    private Transactions transactions;
    private Collection collection;
    private final TransactionOptions transactionOptions =
            TransactionOptions.transactionOptions().durabilityLevel(DurabilityLevel.NONE);


    @Autowired
    public CustomerRepository(CouchbaseTemplate couchbaseTemplate, ApplicationProperties applicationProperties,
                              CustomerMapper customerMapper) {
        this.couchbaseTemplate = couchbaseTemplate;
        this.applicationProperties = applicationProperties;
        this.customerMapper = customerMapper;
    }

    @PostConstruct
    private void postConstruct() {
        scopeName = applicationProperties.getScope();
        collectionName = applicationProperties.getCollection();
        bucketName = applicationProperties.getBucket();
        keySpace = String.join(".", bucketName, scopeName, collectionName);
        transactions = couchbaseTemplate.getCouchbaseClientFactory().getCluster().transactions();
        collection = couchbaseTemplate.getCollection(collectionName);
    }

    @Override
    public List<Customer> findAll() {
        List<JsonObject> jsonObjects = couchbaseTemplate.getCouchbaseClientFactory().getScope()
                .query(String.format("SELECT * FROM %1$s ", keySpace)).rowsAsObject();
        return jsonObjects.stream().map(obj -> customerMapper.mapJsonObjectToCustomer(obj, collectionName)).collect(Collectors.toList());
     }
    @Override
    public Customer findById(String id) {
        return couchbaseTemplate.getCollection(collectionName).get(id).contentAs(Customer.class);
    }

    @Override
    public Customer create(Customer customer) {
        AtomicReference<Customer> atomicReference = new AtomicReference<>();
        transactions.run(ctx -> atomicReference.set(
                ctx.insert(collection, customer.getId(), customer).contentAs(Customer.class)), transactionOptions);
        return atomicReference.get();
    }

    @Override
    public Customer update(Customer customer) {
        AtomicReference<Customer> atomicReference = new AtomicReference<>();
        transactions.run(ctx -> {
            TransactionGetResult transactionGetResult = ctx.get(collection, customer.getId());
            atomicReference.set(ctx.replace(transactionGetResult,customer).contentAs(Customer.class));
        }, transactionOptions);
        return atomicReference.get();
    }

    //not transactional right now
    @Override
    public Customer upsert(Customer customer) {
        couchbaseTemplate.getCollection(collectionName).upsert(customer.getId(),customer);
        return customer;
    }

    @Override
    public void deleteById(String id) {
        transactions.run(ctx -> {
            TransactionGetResult transactionGetResult = ctx.get(collection, id);
            ctx.remove(transactionGetResult);
        }, transactionOptions);
    }
    @Override
    public void deleteAll() {
        transactions.run(ctx -> ctx .query(String.format("DELETE FROM %1$s ", keySpace)), transactionOptions);
    }
}
