package com.chj.gr.mocks.controllers.exceptions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class CustomerControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCustomer_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        when(customerService.createCustomer(any(Customer.class))).thenThrow(new RuntimeException("Database error"));
        Customer customer = new Customer("cust1", "Jean", "Dupont");

        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isInternalServerError());

        verify(customerService, times(1)).createCustomer(any(Customer.class));
    }

    @Test
    void updateCustomer_ShouldReturnBadRequest_WhenServiceThrowsIllegalArgumentException() throws Exception {
        when(customerService.updateCustomer(eq("cust1"), any(Customer.class)))
                .thenThrow(new IllegalArgumentException("Invalid customer ID"));
        Customer customer = new Customer("cust1", "Jean", "Dupont");

        mockMvc.perform(put("/api/customers/cust1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isBadRequest());

        verify(customerService, times(1)).updateCustomer(eq("cust1"), any(Customer.class));
    }

    @Test
    void deleteCustomer_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        doThrow(new RuntimeException("Database error")).when(customerService).deleteCustomer("cust1");

        mockMvc.perform(delete("/api/customers/cust1"))
                .andExpect(status().isInternalServerError());

        verify(customerService, times(1)).deleteCustomer("cust1");
    }

    @Test
    void findByNamePartial_ShouldReturnBadRequest_WhenServiceThrowsIllegalArgumentException() throws Exception {
        when(customerService.findByNamePartial("invalid"))
                .thenThrow(new IllegalArgumentException("Invalid search term"));

        mockMvc.perform(get("/api/customers/search").param("name", "invalid"))
                .andExpect(status().isBadRequest());

        verify(customerService, times(1)).findByNamePartial("invalid");
    }

    @Test
    void findCustomersWithMinSpending_ShouldReturnBadRequest_WhenMinSpendingIsNegative() throws Exception {
        when(customerService.findCustomersWithMinSpending(-100.0))
                .thenThrow(new IllegalArgumentException("minSpending cannot be negative"));

        mockMvc.perform(get("/api/customers/min-spending").param("minSpending", "-100"))
                .andExpect(status().isBadRequest());

        verify(customerService, times(1)).findCustomersWithMinSpending(-100.0);
    }

    @Test
    void findTopActiveCustomers_ShouldReturnBadRequest_WhenLimitIsNegative() throws Exception {
        when(customerService.findTopActiveCustomers(-1))
                .thenThrow(new IllegalArgumentException("Limit cannot be negative"));

        mockMvc.perform(get("/api/customers/top-active").param("limit", "-1"))
                .andExpect(status().isBadRequest());

        verify(customerService, times(1)).findTopActiveCustomers(-1);
    }
}