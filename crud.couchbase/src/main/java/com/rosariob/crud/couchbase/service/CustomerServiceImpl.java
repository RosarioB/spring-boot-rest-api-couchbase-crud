package com.rosariob.crud.couchbase.service;

import com.couchbase.client.core.msg.kv.DurabilityLevel;
import com.couchbase.client.java.transactions.Transactions;
import com.couchbase.client.java.transactions.config.TransactionOptions;
import com.rosariob.crud.couchbase.entity.Customer;
import com.rosariob.crud.couchbase.repository.CustomerRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository repository;

    @Autowired
    private CouchbaseTemplate couchbaseTemplate;

    private Transactions transactions;
    private TransactionOptions transactionOptions = TransactionOptions.transactionOptions()
            .durabilityLevel(DurabilityLevel.NONE);


    @PostConstruct
    public void postConstruct(){
        transactions = couchbaseTemplate.getCouchbaseClientFactory().getCluster().transactions();
    }

    @Override
    public Customer findById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll();
    }

    @Override
    public Customer create(Customer customer) {
        AtomicReference<Customer> atomicReference = new AtomicReference<>();
        transactions.run(ctx -> atomicReference.set(repository.create(customer)), transactionOptions);
        return atomicReference.get();
    }

    @Override
    public Customer update(Customer customer) {
        AtomicReference<Customer> atomicReference = new AtomicReference<>();
        transactions.run(ctx -> atomicReference.set(repository.update(customer)), transactionOptions);
        return atomicReference.get();
    }

    //cannot be used in transactions
    @Override
    public Customer upsert(Customer customer) {
        return repository.upsert(customer);
    }

    @Override
    public void deleteById(String id) {
        transactions.run(ctx -> repository.deleteById(id), transactionOptions);
    }

    @Override
    public void deleteAll() {
        transactions.run(ctx -> repository.deleteAll(), transactionOptions);
    }
}
