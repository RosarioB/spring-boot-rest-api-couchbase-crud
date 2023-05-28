package com.rosariob.crud.couchbase.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class CustomerServiceTestConfig {

    @Bean
    public CustomerService customerService() {
        return new CustomerServiceImpl();
    }
}