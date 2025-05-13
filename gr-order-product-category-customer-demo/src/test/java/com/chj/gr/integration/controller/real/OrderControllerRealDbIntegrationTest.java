package com.chj.gr.integration.controller.real;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.chj.gr.model.Category;
import com.chj.gr.model.Customer;
import com.chj.gr.model.Order;
import com.chj.gr.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class OrderControllerRealDbIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    private String baseUrl;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/orders";
        mongoTemplate.dropCollection(Order.class);
        mongoTemplate.dropCollection(Customer.class);
        mongoTemplate.dropCollection(Product.class);
        mongoTemplate.dropCollection(Category.class);
    }

    @AfterEach
    void tearDown() {
        mongoTemplate.dropCollection(Order.class);
        mongoTemplate.dropCollection(Customer.class);
        mongoTemplate.dropCollection(Product.class);
        mongoTemplate.dropCollection(Category.class);
    }

    @Test
    void createOrder_ShouldReturnCreatedOrder() throws Exception {
        Category category = new Category("cat1", "Electronics");
        mongoTemplate.save(category);

        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        mongoTemplate.save(product);

        Customer customer = new Customer("cust1", "Jean", "Dupont");
        mongoTemplate.save(customer);

        Order order = new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Arrays.asList(product));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(order), headers);

        ResponseEntity<Order> response = restTemplate.postForEntity(baseUrl, request, Order.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ord1", response.getBody().getOrderId());
        assertEquals("En attente", response.getBody().getStatus());
    }

    @Test
    void createOrder_ShouldReturnBadRequest_WhenCustomerIsMissing() throws Exception {
        Category category = new Category("cat1", "Electronics");
        mongoTemplate.save(category);

        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        mongoTemplate.save(product);

        String invalidJson = """
        		{
        			\"orderId\": \"ord1\", 
        			\"orderDate\": \"2025-05-12\", 
        			\"status\": \"En attente\", 
        			\"products\": [" $1 "]
        		}
        		""";
        invalidJson = invalidJson.replace("$1", objectMapper.writeValueAsString(product));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(invalidJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getOrderById_ShouldReturnOrder_WhenIdExists() {
        Category category = new Category("cat1", "Electronics");
        mongoTemplate.save(category);

        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        mongoTemplate.save(product);

        Customer customer = new Customer("cust1", "Jean", "Dupont");
        mongoTemplate.save(customer);

        Order order = new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Arrays.asList(product));
        mongoTemplate.save(order);

        ResponseEntity<Order> response = restTemplate.getForEntity(baseUrl + "/ord1", Order.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ord1", response.getBody().getOrderId());
    }

    @Test
    void getOrderById_ShouldReturnNotFound_WhenIdDoesNotExist() {
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/ord1", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateOrder_ShouldReturnUpdatedOrder() throws Exception {
        Category category = new Category("cat1", "Electronics");
        mongoTemplate.save(category);

        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        mongoTemplate.save(product);

        Customer customer = new Customer("cust1", "Jean", "Dupont");
        mongoTemplate.save(customer);

        Order order = new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Arrays.asList(product));
        mongoTemplate.save(order);

        Order updatedOrder = new Order("ord1", dateFormat.format(new Date()), "Expédiée", customer, Arrays.asList(product));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(updatedOrder), headers);

        ResponseEntity<Order> response = restTemplate.exchange(
                baseUrl + "/ord1", HttpMethod.PUT, request, Order.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Expédiée", response.getBody().getStatus());
    }

    @Test
    void deleteOrder_ShouldReturnNoContent() {
        Category category = new Category("cat1", "Electronics");
        mongoTemplate.save(category);

        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        mongoTemplate.save(product);

        Customer customer = new Customer("cust1", "Jean", "Dupont");
        mongoTemplate.save(customer);

        Order order = new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Arrays.asList(product));
        mongoTemplate.save(order);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/ord1", HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void findByStatus_ShouldReturnMatchingOrders() {
        Category category = new Category("cat1", "Electronics");
        mongoTemplate.save(category);

        Product product = new Product("prod1", "Smartphone", 599.99, 50, category);
        mongoTemplate.save(product);

        Customer customer = new Customer("cust1", "Jean", "Dupont");
        mongoTemplate.save(customer);

        Order order = new Order("ord1", dateFormat.format(new Date()), "En attente", customer, Arrays.asList(product));
        mongoTemplate.save(order);

        ResponseEntity<Order[]> response = restTemplate.getForEntity(
                baseUrl + "/status?status=En attente", Order[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals("ord1", response.getBody()[0].getOrderId());
    }

    @Test
    void findByDateRange_ShouldReturnBadRequest_WhenDateFormatIsInvalid() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl + "/date-range?startDate=invalid-date&endDate=2025-12-31", String.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}