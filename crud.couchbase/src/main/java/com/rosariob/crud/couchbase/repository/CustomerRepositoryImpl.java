package com.rosariob.crud.couchbase.repository;


import com.couchbase.client.java.query.QueryScanConsistency;
import com.rosariob.crud.couchbase.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.data.couchbase.repository.ScanConsistency;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ScanConsistency(query = QueryScanConsistency.REQUEST_PLUS)
public class CustomerRepositoryImpl implements CustomerRepository {
    private CouchbaseTemplate couchbaseTemplate;

    @Autowired
    public CustomerRepositoryImpl(CouchbaseTemplate couchbaseTemplate) {
        this.couchbaseTemplate = couchbaseTemplate;
    }
    @Override
    public List<Customer> findAll() {
        return couchbaseTemplate.findByQuery(Customer.class).all();
     }

    @Override
    public Customer create(Customer customer) {
        return couchbaseTemplate.insertById(Customer.class).one(customer);
    }

    @Override
    public Customer findById(String id) {
        return couchbaseTemplate.findById(Customer.class).one(id);
    }

    @Override
    public Customer update(Customer customer) {
        return couchbaseTemplate.replaceById(Customer.class).one(customer);
    }

    @Override
    public Customer upsert(Customer customer) {
        return couchbaseTemplate.upsertById(Customer.class).one(customer);
    }

    @Override
    public void deleteById(String id) {
        Customer customer = couchbaseTemplate.findById(Customer.class).one(id);
        couchbaseTemplate.removeById(Customer.class).withCas(customer.getCas()).one(id);
    }

    @Override
    public void deleteAll() {
        couchbaseTemplate.removeByQuery(Customer.class).all();
    }
}
