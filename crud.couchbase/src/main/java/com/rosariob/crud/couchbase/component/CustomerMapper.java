package com.rosariob.crud.couchbase.component;

import com.couchbase.client.java.json.JsonObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rosariob.crud.couchbase.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {
    public Customer mapJsonObjectToCustomer(JsonObject jsonObject, String collectionName){
        Customer customer = null;
        try {
            customer = new ObjectMapper().readValue(jsonObject.get(collectionName).toString(), Customer.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return customer;
    }
}
