package com.rosariob.crud.couchbase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rosariob.crud.couchbase.entity.Customer;
import com.rosariob.crud.couchbase.rest.CustomerController;
import com.rosariob.crud.couchbase.service.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerServiceImpl customerService;

    private static final String URL_API = "/api/customers";

    @BeforeEach
    public void setUp() {
        Customer alex = new Customer("customer1","Alex", "Stone");

        when(customerService.findById(Mockito.anyString())).thenReturn(alex);
    }

    @Test
    public void findByIdOk() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get(URL_API + "/customer1")
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void findAllOk() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get(URL_API)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void createOk() throws Exception {
        Customer customer = new Customer("myId", "John", "Doe");
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(
                MockMvcRequestBuilders.post(URL_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(customer))
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void updateOk() throws Exception {
        Customer customer = new Customer("myId", "John", "Doe");
        ObjectMapper mapper = new ObjectMapper();
        mockMvc.perform(
                MockMvcRequestBuilders.put(URL_API)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(customer))
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteByIdOk() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete(URL_API + "/customer1")
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteAll() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete(URL_API)
        ).andExpect(MockMvcResultMatchers.status().isOk());
    }
}
