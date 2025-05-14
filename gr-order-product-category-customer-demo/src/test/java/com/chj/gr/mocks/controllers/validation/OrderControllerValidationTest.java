package com.chj.gr.mocks.controllers.validation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

import com.chj.gr.controller.OrderController;
import com.chj.gr.model.Category;
import com.chj.gr.model.Customer;
import com.chj.gr.model.Order;
import com.chj.gr.model.Product;
import com.chj.gr.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(OrderController.class)
public class OrderControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    void createOrder_ShouldReturnBadRequest_WhenOrderIdIsMissing() throws Exception {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        String invalidJson = "{\"orderDate\": \"2025-05-12\", \"status\": \"En attente\", \"customer\": " + objectMapper.writeValueAsString(customer) + ", \"products\": [" + objectMapper.writeValueAsString(product) + "]}";
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_ShouldReturnBadRequest_WhenCustomerIsMissing() throws Exception {
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        String invalidJson = "{\"orderId\": \"ord1\", \"orderDate\": \"2025-05-12\", \"status\": \"En attente\", \"products\": [" + objectMapper.writeValueAsString(product) + "]}";
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateOrder_ShouldReturnBadRequest_WhenStatusIsEmpty() throws Exception {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        String invalidJson = "{\"orderId\": \"ord1\", \"orderDate\": \"2025-05-12\", \"status\": \"\", \"customer\": " + objectMapper.writeValueAsString(customer) + ", \"products\": [" + objectMapper.writeValueAsString(product) + "]}";
        mockMvc.perform(put("/api/orders/ord1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource({
            "2025-01-01, 2025-12-31, 1",  // Plage normale
            "2025-12-31, 2025-01-01, 0",  // Plage inversée
            "2025-01-01, 2025-01-01, 1"   // Même jour
    })
    void findByDateRange_ShouldReturnOrders(String startDate, String endDate, int expectedSize) throws Exception {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        List<Order> orders = expectedSize > 0 ? Arrays.asList(
        		new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Collections.emptyList())) : Collections.emptyList();
        
        when(orderService.findByDateRange(any(Date.class), any(Date.class))).thenReturn(orders);

        mockMvc.perform(get("/api/orders/date-range")
                .param("startDate", startDate)
                .param("endDate", endDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedSize));

        verify(orderService, times(1)).findByDateRange(any(Date.class), any(Date.class));
    }

    @ParameterizedTest
    @CsvSource({
            "En attente, 1",  // Statut normal
            ", 0",           // Statut vide
            "Inconnu, 0"     // Statut non trouvé
    })
    void findByStatus_ShouldReturnOrders(String status, int expectedSize) throws Exception {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        List<Order> orders = expectedSize > 0 ? Arrays.asList(
        		new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Collections.emptyList())) : Collections.emptyList();
        when(orderService.findByStatus(status == null ? "" : status)).thenReturn(orders);

        mockMvc.perform(get("/api/orders/status").param("status", status == null ? "" : status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedSize));

        verify(orderService, times(1)).findByStatus(status == null ? "" : status);
    }
}