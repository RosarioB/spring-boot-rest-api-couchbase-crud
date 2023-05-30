package com.rosariob.crud.couchbase.repository;

import com.rosariob.crud.couchbase.entity.Customer;
import org.springframework.data.repository.ListCrudRepository;

public interface CustomerRepository extends ListCrudRepository<Customer, String> {

}