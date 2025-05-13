package com.chj.gr.mocks.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.chj.gr.controller.CustomerController;
import com.chj.gr.model.Customer;
import com.chj.gr.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCustomer_ShouldReturnCreatedCustomer() throws Exception {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        when(customerService.createCustomer(any(Customer.class))).thenReturn(customer);

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("cust1"))
                .andExpect(jsonPath("$.firstName").value("Jean"));

        verify(customerService, times(1)).createCustomer(any(Customer.class));
    }

    @Test
    void getCustomerById_ShouldReturnCustomer_WhenIdExists() throws Exception {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        when(customerService.getCustomerById("cust1")).thenReturn(Optional.of(customer));

        mockMvc.perform(get("/api/customers/cust1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("cust1"))
                .andExpect(jsonPath("$.firstName").value("Jean"));

        verify(customerService, times(1)).getCustomerById("cust1");
    }

    @Test
    void getCustomerById_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        when(customerService.getCustomerById("cust1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/customers/cust1"))
                .andExpect(status().isNotFound());

        verify(customerService, times(1)).getCustomerById("cust1");
    }

    @Test
    void getAllCustomers_ShouldReturnListOfCustomers() throws Exception {
        List<Customer> customers = Arrays.asList(
                new Customer("cust1", "Jean", "Dupont"),
                new Customer("cust2", "Marie", "Curie")
        );
        when(customerService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("cust1"))
                .andExpect(jsonPath("$[1].firstName").value("Marie"));

        verify(customerService, times(1)).getAllCustomers();
    }

    @Test
    void updateCustomer_ShouldReturnUpdatedCustomer() throws Exception {
        Customer customer = new Customer("cust1", "Jean", "Dupont Updated");
        when(customerService.updateCustomer(eq("cust1"), any(Customer.class))).thenReturn(customer);

        mockMvc.perform(put("/api/customers/cust1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value("cust1"))
                .andExpect(jsonPath("$.lastName").value("Dupont Updated"));

        verify(customerService, times(1)).updateCustomer(eq("cust1"), any(Customer.class));
    }

    @Test
    void deleteCustomer_ShouldReturnNoContent() throws Exception {
        doNothing().when(customerService).deleteCustomer("cust1");

        mockMvc.perform(delete("/api/customers/cust1"))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).deleteCustomer("cust1");
    }

    @Test
    void findCustomersWithOrders_ShouldReturnCustomers() throws Exception {
        List<Customer> customers = Arrays.asList(new Customer("cust1", "Jean", "Dupont"));
        when(customerService.findCustomersWithOrders()).thenReturn(customers);

        mockMvc.perform(get("/api/customers/with-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("cust1"));

        verify(customerService, times(1)).findCustomersWithOrders();
    }

    @Test
    void findByNamePartial_ShouldReturnMatchingCustomers() throws Exception {
        List<Customer> customers = Arrays.asList(new Customer("cust1", "Jean", "Dupont"));
        when(customerService.findByNamePartial("jean")).thenReturn(customers);

        mockMvc.perform(get("/api/customers/search?name=jean"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Jean"));

        verify(customerService, times(1)).findByNamePartial("jean");
    }

    @Test
    void findCustomersWithMinSpending_ShouldReturnMatchingCustomers() throws Exception {
        List<Customer> customers = Arrays.asList(new Customer("cust1", "Jean", "Dupont"));
        when(customerService.findCustomersWithMinSpending(1000.0)).thenReturn(customers);

        mockMvc.perform(get("/api/customers/min-spending?minSpending=1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("cust1"));

        verify(customerService, times(1)).findCustomersWithMinSpending(1000.0);
    }

    @Test
    void findTopActiveCustomers_ShouldReturnTopCustomers() throws Exception {
        List<Customer> customers = Arrays.asList(new Customer("cust1", "Jean", "Dupont"));
        when(customerService.findTopActiveCustomers(5)).thenReturn(customers);

        mockMvc.perform(get("/api/customers/top-active?limit=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value("cust1"));

        verify(customerService, times(1)).findTopActiveCustomers(5);
    }
}