package com.chj.gr.mocks.controllers.validation;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.chj.gr.controller.CustomerController;
import com.chj.gr.model.Customer;
import com.chj.gr.service.CustomerService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CustomerController.class)
public class CustomerControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Test
    @Disabled
    void createCustomer_ShouldReturnBadRequest_WhenCustomerIdIsMissing() throws Exception {
        String invalidJson = "{\"firstName\": \"Jean\", \"lastName\": \"Dupont\"}";
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Disabled
    void createCustomer_ShouldReturnBadRequest_WhenFirstNameIsEmpty() throws Exception {
        String invalidJson = "{\"customerId\": \"cust1\", \"firstName\": \"\", \"lastName\": \"Dupont\"}";
        mockMvc.perform(post("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Disabled
    void updateCustomer_ShouldReturnBadRequest_WhenLastNameIsMissing() throws Exception {
        String invalidJson = "{\"customerId\": \"cust1\", \"firstName\": \"Jean\"}";
        mockMvc.perform(put("/api/customers/cust1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource({
            "jean, 1",    // Recherche normale
            ", 0",        // Nom vide
            "xyz, 0"      // Nom non trouvé
    })
    void findByNamePartial_ShouldReturnCustomers(String name, int expectedSize) throws Exception {
        List<Customer> customers = expectedSize > 0 ? Arrays.asList(new Customer("cust1", "Jean", "Dupont")) : Collections.emptyList();
        when(customerService.findByNamePartial(name == null ? "" : name)).thenReturn(customers);

        mockMvc.perform(get("/api/customers/search").param("name", name == null ? "" : name))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedSize));

        verify(customerService, times(1)).findByNamePartial(name == null ? "" : name);
    }

    @ParameterizedTest
    @CsvSource({
            "1000, 1",    // Dépense minimum normale
            "0, 1",       // Zéro (tous clients)
            "-100, 0"     // Valeur négative
    })
    void findCustomersWithMinSpending_ShouldReturnCustomers(double minSpending, int expectedSize) throws Exception {
        List<Customer> customers = expectedSize > 0 ? Arrays.asList(new Customer("cust1", "Jean", "Dupont")) : Collections.emptyList();
        when(customerService.findCustomersWithMinSpending(minSpending)).thenReturn(customers);

        mockMvc.perform(get("/api/customers/min-spending").param("minSpending", String.valueOf(minSpending)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedSize));

        verify(customerService, times(1)).findCustomersWithMinSpending(minSpending);
    }

    @ParameterizedTest
    @CsvSource({
            "5, 1",    // Limite normale
            "0, 0",    // Zéro
            "-1, 0"    // Valeur négative
    })
    void findTopActiveCustomers_ShouldReturnCustomers(int limit, int expectedSize) throws Exception {
        List<Customer> customers = expectedSize > 0 ? Arrays.asList(new Customer("cust1", "Jean", "Dupont")) : Collections.emptyList();
        when(customerService.findTopActiveCustomers(limit)).thenReturn(customers);

        mockMvc.perform(get("/api/customers/top-active").param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedSize));

        verify(customerService, times(1)).findTopActiveCustomers(limit);
    }
}