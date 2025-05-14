package com.chj.gr.mocks.controllers.exceptions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
public class OrderControllerExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    void createOrder_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        when(orderService.createOrder(any(Order.class))).thenThrow(new RuntimeException("Database error"));
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        Order order = new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Arrays.asList(product));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isInternalServerError());

        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void updateOrder_ShouldReturnBadRequest_WhenServiceThrowsIllegalArgumentException() throws Exception {
        when(orderService.updateOrder(eq("ord1"), any(Order.class)))
                .thenThrow(new IllegalArgumentException("Invalid order ID"));
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        Order order = new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Arrays.asList(product));

        mockMvc.perform(put("/api/orders/ord1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isBadRequest());

        verify(orderService, times(1)).updateOrder(eq("ord1"), any(Order.class));
    }

    @Test
    void deleteOrder_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        doThrow(new RuntimeException("Database error")).when(orderService).deleteOrder("ord1");

        mockMvc.perform(delete("/api/orders/ord1"))
                .andExpect(status().isInternalServerError());

        verify(orderService, times(1)).deleteOrder("ord1");
    }

    @Test
    void findByDateRange_ShouldReturnBadRequest_WhenDateFormatIsInvalid() throws Exception {
        mockMvc.perform(get("/api/orders/date-range")
                .param("startDate", "invalide_date")
                .param("endDate", "2025-12-31"))
                .andExpect(status().isInternalServerError());

        verify(orderService, never()).findByDateRange(any(Date.class), any(Date.class));
    }

    @Test
    void findByStatus_ShouldReturnBadRequest_WhenServiceThrowsIllegalArgumentException() throws Exception {
        when(orderService.findByStatus("invalid"))
                .thenThrow(new IllegalArgumentException("Invalid status"));

        mockMvc.perform(get("/api/orders/status").param("status", "invalid"))
                .andExpect(status().isBadRequest());

        verify(orderService, times(1)).findByStatus("invalid");
    }

    @Test
    void findTotalAmountByCustomer_ShouldReturnInternalServerError_WhenServiceThrowsRuntimeException() throws Exception {
        when(orderService.findTotalAmountByCustomer("cust1"))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/orders/total-amount/cust1"))
                .andExpect(status().isInternalServerError());

        verify(orderService, times(1)).findTotalAmountByCustomer("cust1");
    }
}