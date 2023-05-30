package com.rosariob.crud.couchbase.service;

import com.couchbase.client.java.transactions.Transactions;
import com.rosariob.crud.couchbase.entity.Customer;
import com.rosariob.crud.couchbase.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CustomerServiceImpl implements CustomerService {
    private CustomerRepository repository;
    private CouchbaseTemplate couchbaseTemplate;

    @Autowired
    public CustomerServiceImpl(CustomerRepository repository, CouchbaseTemplate couchbaseTemplate) {
        this.repository = repository;
        this.couchbaseTemplate = couchbaseTemplate;
    }

    @Override
    public Customer findById(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll();
    }

    @Override
    public Customer save(Customer customer) {
        Transactions transactions = couchbaseTemplate.getCouchbaseClientFactory().getCluster().transactions();
        AtomicReference<Customer> atomicReference = new AtomicReference<>();
        transactions.run(ctx -> atomicReference.set(repository.save(customer)));
        return atomicReference.get();
    }

    @Override
    public void deleteById(String id) {
        Transactions transactions = couchbaseTemplate.getCouchbaseClientFactory().getCluster().transactions();
        transactions.run(ctx -> repository.deleteById(id));
    }

    @Override
    public void deleteAll() {
        Transactions transactions = couchbaseTemplate.getCouchbaseClientFactory().getCluster().transactions();
        transactions.run(ctx -> repository.deleteAll());
    }
}
