package com.chj.gr.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chj.gr.model.Order;
import com.chj.gr.repository.OrderRepository;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> getOrderById(String id) {
        return orderRepository.findById(id);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateOrder(String id, Order order) {
        order.setOrderId(id);
        return orderRepository.save(order);
    }

    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }

    public List<Order> findByDateRange(Date startDate, Date endDate) {
        return orderRepository.findByDateRange(startDate, endDate);
    }

    public List<Order> findByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    public double findTotalAmountByCustomer(String customerId) {
        List<OrderRepository.TotalAmount> result = orderRepository.findTotalAmountByCustomer(customerId);
        return result.isEmpty() ? 0.0 : result.get(0).getTotalAmount();
    }
}