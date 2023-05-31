package com.rosariob.crud.couchbase.rest;

import com.rosariob.crud.couchbase.entity.Customer;
import com.rosariob.crud.couchbase.service.CustomerService;
import com.rosariob.crud.couchbase.service.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerServiceImpl customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity findById(@PathVariable String customerId){
        try {
            return ResponseEntity.ok(customerService.findById(customerId));
        }
        catch(NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity findAll(){
        try {
            List<Customer> found = customerService.findAll();
            return ResponseEntity.ok(found);
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity create(@RequestBody Customer customer){
        try {
            return ResponseEntity.ok(customerService.save(customer));
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity update(@RequestBody Customer customer){
        try {
            return ResponseEntity.ok(customerService.save(customer));
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity deleteById(@PathVariable String customerId){
        try {
            customerService.deleteById(customerId);
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @DeleteMapping()
    public ResponseEntity deleteAll(){
        try {
            customerService.deleteAll();
            return ResponseEntity.ok().build();
        }
        catch(Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
