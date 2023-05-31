package com.rosariob.crud.couchbase.service;

import com.rosariob.crud.couchbase.entity.Customer;
import com.rosariob.crud.couchbase.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class CustomerServiceTest {
    @MockBean
    private CustomerRepository repository;

    @MockBean
    private CouchbaseTemplate couchbaseTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Customer alex = new Customer("customer1","Alex", "Stone");
        Customer jack = new Customer("customer2","Jack", "Sparrow");

        when(repository.findById(alex.getId())).thenReturn(Optional.of(alex));
        when(repository.findAll()).thenReturn(List.of(alex,jack));
        when(repository.save(alex)).thenReturn(alex);
    }

    @Test
    public void findByIdOk(){
        CustomerService customerService = new CustomerServiceImpl(repository, couchbaseTemplate);
        Customer alex = new Customer("customer1","Alex", "Stone");
        String id = "customer1";
        Customer found = customerService.findById(id);
        Assertions.assertEquals(alex, found);
    }

    @Test
    public void findAllOk(){
        CustomerService customerService = new CustomerServiceImpl(repository, couchbaseTemplate);
        Customer alex = new Customer("customer1","Alex", "Stone");
        Customer jack = new Customer("customer2","Jack", "Sparrow");
        List<Customer> customers = customerService.findAll();
        Assertions.assertEquals(List.of(alex, jack), customers);
    }

    @Test
    public void saveOk(){
        CustomerService customerService = new CustomerServiceImpl(repository, couchbaseTemplate);
        Customer alex = new Customer("customer1","Alex", "Stone");
        Customer customer = customerService.save(alex);
        Assertions.assertEquals(alex, customer);
    }

    @Test
    public void deleteByIdOk(){
        CustomerService customerService = new CustomerServiceImpl(repository, couchbaseTemplate);
        Customer alex = new Customer("customer1","Alex", "Stone");
        String customerId = "customer1";
        customerService.deleteById(customerId);
        verify(repository, times(1)).delete(alex);
    }

    @Test
    public void deleteAllOk(){
        CustomerService customerService = new CustomerServiceImpl(repository, couchbaseTemplate);
        customerService.deleteAll();
        verify(repository, times(1)).deleteAll();
    }
}
