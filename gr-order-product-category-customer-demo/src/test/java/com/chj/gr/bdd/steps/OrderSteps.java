package com.chj.gr.bdd.steps;

import java.text.ParseException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.chj.gr.model.Category;
import com.chj.gr.model.Customer;
import com.chj.gr.model.Order;
import com.chj.gr.model.Product;

import io.cucumber.java.en.Given;

@ActiveProfiles("test")
public class OrderSteps {

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Given("the order, customer, product, and category collections are empty")
    public void theOrderCustomerProductAndCategoryCollectionsAreEmpty() {
        mongoTemplate.dropCollection(Order.class);
        mongoTemplate.dropCollection(Customer.class);
        mongoTemplate.dropCollection(Product.class);
        mongoTemplate.dropCollection(Category.class);
    }

    @Given("an order exists with ID {string}, status {string}, customer ID {string}, and product ID {string}")
    public void anOrderExistsWithIdStatusCustomerIdAndProductId(String orderId, String status, String customerId, String productId) throws ParseException {
        Customer customer = mongoTemplate.findById(customerId, Customer.class);
        Product product = mongoTemplate.findById(productId, Product.class);
        Order order = new Order(orderId,
        			"2025-05-13 16:34:08",
        			status, 
        			customer, 
        			Arrays.asList(product)
        );
        mongoTemplate.save(order);
    }
}