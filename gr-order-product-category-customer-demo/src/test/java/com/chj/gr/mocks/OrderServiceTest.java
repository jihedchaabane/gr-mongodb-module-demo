package com.chj.gr.mocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chj.gr.model.Category;
import com.chj.gr.model.Customer;
import com.chj.gr.model.Order;
import com.chj.gr.model.Product;
import com.chj.gr.repository.OrderRepository;
import com.chj.gr.service.OrderService;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    void createOrder_ShouldReturnSavedOrder() {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        Category category = new Category("cat1", "Electronics");
        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        Order order = new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Arrays.asList(product));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.createOrder(order);

        assertNotNull(result);
        assertEquals("ord1", result.getOrderId());
        assertEquals("En attente", result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenIdExists() {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        Order order = new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Collections.emptyList());
        when(orderRepository.findById("ord1")).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.getOrderById("ord1");

        assertTrue(result.isPresent());
        assertEquals("ord1", result.get().getOrderId());
        verify(orderRepository, times(1)).findById("ord1");
    }

    @Test
    void getOrderById_ShouldReturnEmpty_WhenIdDoesNotExist() {
        when(orderRepository.findById("ord1")).thenReturn(Optional.empty());

        Optional<Order> result = orderService.getOrderById("ord1");

        assertFalse(result.isPresent());
        verify(orderRepository, times(1)).findById("ord1");
    }

    @Test
    void getAllOrders_ShouldReturnListOfOrders() {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        List<Order> orders = Arrays.asList(
            new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Collections.emptyList()),
            new Order("ord2", dateFormat.format(new Date()), "Expédiée", customer, Collections.emptyList())
        );
        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        assertEquals("En attente", result.get(0).getStatus());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void updateOrder_ShouldReturnUpdatedOrder() {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        Order order = new Order("ord1", dateFormat.format(new Date()), "Expédiée", customer, Collections.emptyList());
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrder("ord1", order);

        assertNotNull(result);
        assertEquals("ord1", result.getOrderId());
        assertEquals("Expédiée", result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void deleteOrder_ShouldCallDeleteById() {
        doNothing().when(orderRepository).deleteById("ord1");

        orderService.deleteOrder("ord1");

        verify(orderRepository, times(1)).deleteById("ord1");
    }

    @Test
    void findByDateRange_ShouldReturnMatchingOrders() {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        Date startDate = new Date();
        Date endDate = new Date();
        List<Order> orders = Arrays.asList(new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Collections.emptyList()));
        when(orderRepository.findByDateRange(startDate, endDate)).thenReturn(orders);

        List<Order> result = orderService.findByDateRange(startDate, endDate);

        assertEquals(1, result.size());
        assertEquals("En attente", result.get(0).getStatus());
        verify(orderRepository, times(1)).findByDateRange(startDate, endDate);
    }

    @Test
    void findByStatus_ShouldReturnMatchingOrders() {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        List<Order> orders = Arrays.asList(new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Collections.emptyList()));
        when(orderRepository.findByStatus("En attente")).thenReturn(orders);

        List<Order> result = orderService.findByStatus("En attente");

        assertEquals(1, result.size());
        assertEquals("En attente", result.get(0).getStatus());
        verify(orderRepository, times(1)).findByStatus("En attente");
    }

    @Test
    void findTotalAmountByCustomer_ShouldReturnTotalAmount() {
        OrderRepository.TotalAmount totalAmount = mock(OrderRepository.TotalAmount.class);
        when(totalAmount.getTotalAmount()).thenReturn(1500.0);
        when(orderRepository.findTotalAmountByCustomer("cust1")).thenReturn(Arrays.asList(totalAmount));

        double result = orderService.findTotalAmountByCustomer("cust1");

        assertEquals(1500.0, result);
        verify(orderRepository, times(1)).findTotalAmountByCustomer("cust1");
    }

    @Test
    void findTotalAmountByCustomer_ShouldReturnZero_WhenNoOrders() {
        when(orderRepository.findTotalAmountByCustomer("cust1")).thenReturn(Collections.emptyList());

        double result = orderService.findTotalAmountByCustomer("cust1");

        assertEquals(0.0, result);
        verify(orderRepository, times(1)).findTotalAmountByCustomer("cust1");
    }
}