package com.chj.gr.util;

import com.chj.gr.model.Category;
import com.chj.gr.model.Customer;
import com.chj.gr.model.Order;
import com.chj.gr.model.Product;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderGenerator {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static List<Order> generateOrders(int count) {
        List<Order> orders = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Category category = new Category("cat" + i, "Category" + i);
            Customer customer = new Customer("cust" + i, "First" + i, "Last" + i);
            Product product = new Product("prod" + i, "Product" + i, 100.0 + i, 50, category);
            Order order = new Order(
                    UUID.randomUUID().toString(),
                    LocalDateTime.now().format(formatter),
                    "En attente",
                    customer,
                    new ArrayList<>(List.of(product))
            );
            orders.add(order);
        }
        return orders;
    }
}