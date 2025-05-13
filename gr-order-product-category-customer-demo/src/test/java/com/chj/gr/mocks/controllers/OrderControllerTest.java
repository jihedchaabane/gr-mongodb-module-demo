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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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

import com.chj.gr.controller.OrderController;
import com.chj.gr.model.Category;
import com.chj.gr.model.Customer;
import com.chj.gr.model.Order;
import com.chj.gr.model.Product;
import com.chj.gr.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        Order order = new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Arrays.asList(product));
        when(orderService.createOrder(any(Order.class))).thenReturn(order);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("ord1"))
                .andExpect(jsonPath("$.status").value("En attente"));

        verify(orderService, times(1)).createOrder(any(Order.class));
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenIdExists() throws Exception {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        Order order = new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Collections.emptyList());
        when(orderService.getOrderById("ord1")).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/orders/ord1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("ord1"))
                .andExpect(jsonPath("$.status").value("En attente"));

        verify(orderService, times(1)).getOrderById("ord1");
    }

    @Test
    void getOrderById_ShouldReturnNotFound_WhenIdDoesNotExist() throws Exception {
        when(orderService.getOrderById("ord1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/ord1"))
                .andExpect(status().isNotFound());

        verify(orderService, times(1)).getOrderById("ord1");
    }

    @Test
    void getAllOrders_ShouldReturnListOfOrders() throws Exception {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        List<Order> orders = Arrays.asList(
                new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Collections.emptyList()),
                new Order("ord2", dateFormat.format(new Date()), "Expédiée", customer, Collections.emptyList())
        );
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value("ord1"))
                .andExpect(jsonPath("$[1].status").value("Expédiée"));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void updateOrder_ShouldReturnUpdatedOrder() throws Exception {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        Order order = new Order("ord1", dateFormat.format(new Date()), "Expédiée", customer, Collections.emptyList());
        when(orderService.updateOrder(eq("ord1"), any(Order.class))).thenReturn(order);

        mockMvc.perform(put("/api/orders/ord1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("ord1"))
                .andExpect(jsonPath("$.status").value("Expédiée"));

        verify(orderService, times(1)).updateOrder(eq("ord1"), any(Order.class));
    }

    @Test
    void deleteOrder_ShouldReturnNoContent() throws Exception {
        doNothing().when(orderService).deleteOrder("ord1");

        mockMvc.perform(delete("/api/orders/ord1"))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).deleteOrder("ord1");
    }

    @Test
    void findByDateRange_ShouldReturnMatchingOrders() throws Exception {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        List<Order> orders = Arrays.asList(new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Collections.emptyList()));
        when(orderService.findByDateRange(any(Date.class), any(Date.class))).thenReturn(orders);

        mockMvc.perform(get("/api/orders/date-range?startDate=2025-01-01&endDate=2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value("ord1"));

        verify(orderService, times(1)).findByDateRange(any(Date.class), any(Date.class));
    }

    @Test
    void findByStatus_ShouldReturnMatchingOrders() throws Exception {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        List<Order> orders = Arrays.asList(new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Collections.emptyList()));
        when(orderService.findByStatus("En attente")).thenReturn(orders);

        mockMvc.perform(get("/api/orders/status?status=En attente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value("ord1"));

        verify(orderService, times(1)).findByStatus("En attente");
    }

    @Test
    void findTotalAmountByCustomer_ShouldReturnTotalAmount() throws Exception {
        when(orderService.findTotalAmountByCustomer("cust1")).thenReturn(1500.0);

        mockMvc.perform(get("/api/orders/total-amount/cust1"))
                .andExpect(status().isOk())
                .andExpect(content().string("1500.0"));

        verify(orderService, times(1)).findTotalAmountByCustomer("cust1");
    }
}