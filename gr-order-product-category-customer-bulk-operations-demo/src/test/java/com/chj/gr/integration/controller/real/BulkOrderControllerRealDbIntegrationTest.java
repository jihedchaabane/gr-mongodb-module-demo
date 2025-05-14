package com.chj.gr.integration.controller.real;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.chj.gr.model.Category;
import com.chj.gr.model.Customer;
import com.chj.gr.model.Order;
import com.chj.gr.model.Product;
import com.chj.gr.util.OrderGenerator;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class BulkOrderControllerRealDbIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    private String baseUrl;
    
    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/bulk-orders";
//        mongoTemplate.dropCollection(Order.class);
        mongoTemplate.dropCollection(Customer.class);
        mongoTemplate.dropCollection(Product.class);
        mongoTemplate.dropCollection(Category.class);
    }

//    @AfterEach
//    void tearDown() {
//        mongoTemplate.dropCollection(Order.class);
//        mongoTemplate.dropCollection(Customer.class);
//        mongoTemplate.dropCollection(Product.class);
//        mongoTemplate.dropCollection(Category.class);
//    }

    @Test
    void createOrder_ShouldReturnCreatedOrders() throws Exception {
    	
    	int count = 10;
//    	Completed bulk save of   1 000 orders in  383 ms
//    	Completed bulk save of  10 000 orders in 1434 ms
//    	Completed bulk save of 100 000 orders in 8515 ms
//    	
    	List<Order> orders = OrderGenerator.generateOrders(count);
    	orders.forEach(order -> {
            assertNotNull(order.getOrderId(), "Order ID must not be null");
            assertFalse(order.getOrderId().isBlank(), "Order ID must not be blank");
            System.out.println("Order ID: " + order.getOrderId());
        });
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<Order>> request = new HttpEntity<List<Order>>(orders, headers);
        ResponseEntity<String> response = 
        		restTemplate.postForEntity(baseUrl, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(response.getBody(), String.format("Successfully saved %d orders", count));
    }

}