package com.rosariob.crud.couchbase.service;

import com.rosariob.crud.couchbase.entity.Customer;
import com.rosariob.crud.couchbase.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return repository.findById(id).orElseThrow();
    }

    @Override
    public List<Customer> findAll() {
        return repository.findAll();
    }

    @Override
    public Customer save(Customer customer) {
        return repository.save(customer);
    }

    @Override
    public void deleteById(String id) {
        Customer customer = repository.findById(id).orElseThrow();
        repository.delete(customer);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
