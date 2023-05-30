package com.rosariob.crud.couchbase.service;

import com.rosariob.crud.couchbase.entity.Customer;

import java.util.List;

public interface CustomerService {
    List<Customer> findAll();
    Customer save(Customer customer);
    Customer findById(String id);
    void deleteById(String id);
    void deleteAll();
}